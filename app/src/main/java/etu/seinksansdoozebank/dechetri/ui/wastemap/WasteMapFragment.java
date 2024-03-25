package etu.seinksansdoozebank.dechetri.ui.wastemap;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
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

import java.util.Collections;

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
        markerDrawable= getResources().getDrawable(R.drawable.my_location);

        Context ctx = getContext();
        if (ctx != null) {
            Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));


            locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        }
        if (checkLocationPermission()) {
            Log.v(TAG, "Location permission granted");
            setupMapView();
            updateMapToCurrentLocation();
        } else {
            Log.v(TAG, "Location permission not granted");
            requestLocationPermission();
        }

        return view;
    }

    private boolean checkLocationPermission() {
        Context context = getContext();
        if (context == null) return false;

        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    private void setupMapView() {
        map = binding.mapView;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mapController.setCenter(startPoint);
                    mapController.setZoom(15.0);
                    addMarker(startPoint);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupMapView();
                updateMapToCurrentLocation();
            } else {
                // Handle the case where the user denies the permission.
            }
        }
    }
    private void addMarker(GeoPoint startPoint) {
        OverlayItem locationMarker = new OverlayItem("Votre Position", "Vous êtes ici", startPoint);
        locationMarker.setMarker(markerDrawable);

        ItemizedIconOverlay<OverlayItem> locationOverlay = new ItemizedIconOverlay<>(Collections.singletonList(locationMarker),
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}