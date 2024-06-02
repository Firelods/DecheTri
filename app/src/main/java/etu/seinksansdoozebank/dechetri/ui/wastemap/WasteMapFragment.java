package etu.seinksansdoozebank.dechetri.ui.wastemap;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.databinding.FragmentWasteMapBinding;
import etu.seinksansdoozebank.dechetri.model.user.User;
import etu.seinksansdoozebank.dechetri.model.waste.Waste;
import etu.seinksansdoozebank.dechetri.model.waste.WasteList;
import etu.seinksansdoozebank.dechetri.model.waste.WasteListObserver;

public class WasteMapFragment extends Fragment implements LocationListener, WasteListObserver, WasteDialogListener {
    private FragmentWasteMapBinding binding;
    private WasteList wasteList;
    private MapView map;
    private IMapController mapController;
    private static final String TAG = "WasteMapFragment";
    private LocationManager locationManager;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Marker currentLocationMarker;
    private final List<OverlayItem> items = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private Activity activity;
    private String id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWasteMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setRefreshing(true);

        // on scroll down disable swipeRefresh
        swipeRefreshLayout.setOnChildScrollUpCallback((parent, child) -> true);

        initializeLocationManager();

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Log.v(TAG, "Location permission granted");
                setupMapView();
                updateMapToCurrentLocation();
                wasteList = new WasteList(activity);
                addWastePointsOnMap(wasteList);
            } else {
                Log.v(TAG, "Location permission denied");
            }
        });
        setupMapView();
        if (checkLocationPermission()) {
            updateMapToCurrentLocation();
            wasteList = new WasteList(activity);
            addWastePointsOnMap(wasteList);
        } else {
            requestLocationPermission();
        }

        view.findViewById(R.id.btn_refresh_waste_point).setOnClickListener(v -> {
            // Set to the swipeRefreshLayout view the elevetion of 5
            swipeRefreshLayout.setElevation(5);
            swipeRefreshLayout.setRefreshing(true);
            wasteList = new WasteList(activity);
            wasteList.updateList();
        });

        return view;
    }

    private void initializeLocationManager() {
        Context ctx = getContext();
        if (ctx != null) {
            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));
            locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private boolean checkLocationPermission() {
        Context context = getContext();
        return context != null &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void setupMapView() {
        map = binding.mapView;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        mapController = map.getController();
        map.setMinZoomLevel(5.0);
        mapController.setZoom(10.0);
        mapController.setCenter(new GeoPoint(46.603354, 1.888334));
    }


    private void updateMapToCurrentLocation() {
        if ((ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || locationManager == null || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return;
        }
        if (getContext() != null && checkLocationPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.v(TAG, "Last known location from GPS provider: " + lastKnownLocation);
        if (lastKnownLocation == null) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        Log.v(TAG, "Last known location: " + lastKnownLocation);
        if (lastKnownLocation != null) {
            setLocationOnPoint(lastKnownLocation);
            mapController.setCenter(new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
            mapController.setZoom(15.0);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this::setLocationOnPoint);
        }
    }

    private void setLocationOnPoint(Location lastKnownLocation) {
        GeoPoint startPoint = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        addMarker(startPoint);
    }

    private void addMarker(GeoPoint startPoint) {
        if (map == null || map.getRepository() == null) return;
        Marker marker = new Marker(map);
        marker.setPosition(startPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        marker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.my_location));
        marker.setTitle(getString(R.string.votre_position));
        if (currentLocationMarker != null) {
            map.getOverlays().remove(currentLocationMarker);
        }
        map.getOverlays().add(marker);
        currentLocationMarker = marker;

        map.invalidate();
    }

    public void addWastePointsOnMap(List<Waste> wastes) {
        if (wastes == null || map == null || map.getRepository() == null) {
            return;
        }
        // Create a set to store the points of the new wastes
        Set<GeoPoint> newWastePoints = wastes.stream()
                .map(location -> new GeoPoint(location.getLatitude(), location.getLongitude()))
                .collect(Collectors.toSet());

        // Remove items that no longer exist in the updated list
        items.removeIf(item -> !newWastePoints.contains(item.getPoint()));

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
        id = sharedPreferences.getString(requireContext().getString(R.string.shared_preferences_key_user_id), requireContext().getResources().getString(R.string.role_user_id));
        // Add new items
        for (Waste location : wastes) {
            User userAssigned=location.getAssignee();
            GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
            if (items.stream().noneMatch(overlayItem -> overlayItem.getPoint().equals(point))) {
                OverlayItem overlayItem = new OverlayItem(location.getAddress(), getString(R.string.dechet_ici), point);
                if(userAssigned!=null && userAssigned.getId().equals(id)){
                    overlayItem.setMarker(ContextCompat.getDrawable(requireContext(),R.drawable.waste_to_pick));
                }else{
                    overlayItem.setMarker(ContextCompat.getDrawable(requireContext(), location.getType().getIcon()));
                }
                items.add(overlayItem);
            }
        }

        // Clear and re-add the overlay to refresh the map
        // filter not the location point (different marker)
        // remove everything but the location point
        List<Overlay> overlays = map.getOverlays();
        overlays.clear();
        if (currentLocationMarker != null) {
            overlays.add(currentLocationMarker);
        }
        map.getOverlays().add(new ItemizedIconOverlay<>(items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                showWasteDetails(wastes.get(index));
                return true;
            }

            @Override
            public boolean onItemLongPress(final int index, final OverlayItem item) {
                // Gestion du long press, si nécessaire
                return true;
            }
        }, requireContext()));
        map.invalidate(); // Rafraîchit la carte pour afficher les nouveaux éléments
    }


    public void showWasteDetails(Waste waste) {
        WasteDialogFragment wasteDialogFragment = new WasteDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable("waste", waste);

        wasteDialogFragment.setArguments(args);

        wasteDialogFragment.setWasteDialogListener(this);

        wasteDialogFragment.show(getParentFragmentManager(), "wasteDetails");
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        swipeRefreshLayout.setRefreshing(false);
        addMarker(new GeoPoint(location.getLatitude(), location.getLongitude()));
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(getContext(), getString(R.string.veuillez_activer_la_localisation), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationManager != null && checkLocationPermission()) {
            locationManager.removeUpdates(this);
        }
        map.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkLocationPermission()) {
            map.onResume();
            updateMapToCurrentLocation();
        } else {
            Toast.makeText(getContext(), getString(R.string.la_localisation_est_necessaire), Toast.LENGTH_SHORT).show();
        }
        wasteList = new WasteList(activity);
        wasteList.addObserver(this);
        wasteList.updateList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        map.onDetach();
        wasteList.removeObserver(this);
    }

    @Override
    public void onWasteListChanged() {
        requireActivity().runOnUiThread(() -> {
            Log.d(TAG, "onWasteListChanged: " + wasteList);
            swipeRefreshLayout.setRefreshing(false);
            addWastePointsOnMap(wasteList);
        });
    }

    @Override
    public void onWasteDialogChange() {
        if (wasteList != null) {
            wasteList.updateList();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

}
