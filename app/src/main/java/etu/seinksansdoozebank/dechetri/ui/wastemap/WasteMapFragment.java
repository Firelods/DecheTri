package etu.seinksansdoozebank.dechetri.ui.wastemap;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.activity.result.ActivityResultLauncher;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.databinding.FragmentWasteMapBinding;

public class WasteMapFragment extends Fragment {
    private FragmentWasteMapBinding binding;
    private MapView map;
    private IMapController mapController;
    private static final String TAG = "WasteMapFragment";
    private LocationManager locationManager;
    Drawable markerDrawable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWasteMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Context ctx = getContext();
        if (ctx != null) {
            markerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.my_location);
            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));
            locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        }
        if (checkLocationPermission()) {
            Log.v(TAG, "Location permission granted");
            setupMapView();
            updateMapToCurrentLocation();

            WasteList wasteList = new WasteList();
            addWastePointsOnMap(wasteList);

        } else {
            Log.v(TAG, "Location permission not granted");
            requestLocationPermission();
        }


        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
        if (checkLocationPermission()) {
            updateMapToCurrentLocation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDetach();
    }

    private boolean checkLocationPermission() {
        Context context = getContext();
        if (context == null) return false;

        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    setupMapView();
                    updateMapToCurrentLocation();
                } else {
                    // Handle the case where permission is denied
                    Log.v(TAG, "Location permission denied");
                }
            });

    private void requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void setupMapView() {
        map = binding.mapView;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        mapController = map.getController();
    }

    private void updateMapToCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.v(TAG, "Last known location from GPS provider: " + lastKnownLocation);
        if (lastKnownLocation == null) {
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        Log.v(TAG, "Last known location: " + lastKnownLocation);
        if (lastKnownLocation != null) {
            GeoPoint startPoint = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mapController.setCenter(startPoint);
            addMarker(startPoint);
            mapController.setZoom(15.0);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, location -> {
                GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                mapController.setCenter(startPoint);
                mapController.setZoom(15.0);
                addMarker(startPoint);
            });
        }
    }

    private void addMarker(GeoPoint startPoint) {
        OverlayItem locationMarker = new OverlayItem("Votre Position", "Vous êtes ici", startPoint);
        locationMarker.setMarker(markerDrawable);

        ArrayList<OverlayItem> items = new ArrayList<>();
        items.add(locationMarker);

        ItemizedIconOverlay<OverlayItem> locationOverlay = new ItemizedIconOverlay<>(
                items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return true;
                    }
                }, requireContext());

        map.getOverlays().add(locationOverlay);
        map.invalidate();
    }

    public void addWastePointsOnMap(List<Waste> wastes) {
        ArrayList<OverlayItem> items = new ArrayList<>();

        for (Waste location : wastes) {
            GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
            OverlayItem overlayItem = new OverlayItem(location.getAddress(), "Déchet ici", point);
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
        args.putString("name", waste.getName());
        args.putDouble("latitude", waste.getLatitude());
        args.putDouble("longitude", waste.getLongitude());
        args.putString("address", waste.getAddress());

        wasteDialogFragment.setArguments(args);

        wasteDialogFragment.show(getParentFragmentManager(), "wasteDetails");
    }
}