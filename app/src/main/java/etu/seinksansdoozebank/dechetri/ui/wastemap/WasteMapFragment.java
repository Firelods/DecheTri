package etu.seinksansdoozebank.dechetri.ui.wastemap;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.databinding.FragmentWasteMapBinding;
import etu.seinksansdoozebank.dechetri.model.waste.Waste;
import etu.seinksansdoozebank.dechetri.model.waste.WasteList;

public class WasteMapFragment extends Fragment implements LocationListener {
    private FragmentWasteMapBinding binding;
    private MapView map;
    private IMapController mapController;
    private static final String TAG = "WasteMapFragment";
    private LocationManager locationManager;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Marker currentLocationMarker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWasteMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initializeLocationManager();

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Log.v(TAG, "Location permission granted");
                setupMapView();
                updateMapToCurrentLocation();
                WasteList wasteList = new WasteList();
                addWastePointsOnMap(wasteList);
            } else {
                Log.v(TAG, "Location permission denied");
            }
        });
        setupMapView();
        if (checkLocationPermission()) {
            updateMapToCurrentLocation();
            WasteList wasteList = new WasteList();
            addWastePointsOnMap(wasteList);
        } else {
            requestLocationPermission();
        }

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
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this::setLocationOnPoint);
        }
    }

    private void setLocationOnPoint(Location lastKnownLocation) {
        GeoPoint startPoint = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        mapController.setCenter(startPoint);
        mapController.setZoom(15.0);
        addMarker(startPoint);
    }

    private void addMarker(GeoPoint startPoint) {
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
        ArrayList<OverlayItem> items = new ArrayList<>();

        for (Waste location : wastes) {
            GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
            OverlayItem overlayItem = new OverlayItem(location.getAddress(), getString(R.string.dechet_ici), point);
            overlayItem.setMarker(ContextCompat.getDrawable(requireContext(), R.drawable.waste));
            items.add(overlayItem);
        }

        ItemizedIconOverlay<OverlayItem> wasteOverlay = new ItemizedIconOverlay<>(items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
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
        }, requireContext());

        map.getOverlays().add(wasteOverlay);
        map.invalidate(); // Rafraîchit la carte pour afficher les nouveaux éléments
    }

    public void showWasteDetails(Waste waste) {
        WasteDialogFragment wasteDialogFragment = new WasteDialogFragment();

        Bundle args = new Bundle();
        args.putParcelable("waste", waste);

        wasteDialogFragment.setArguments(args);

        wasteDialogFragment.show(getParentFragmentManager(), "wasteDetails");
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        map.onDetach();
    }
}
