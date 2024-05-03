package etu.seinksansdoozebank.dechetri.ui.wastereport;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.databinding.FragmentLocationChoiceBinding;

public class LocationChoiceFragment extends Fragment implements LocationListener {
    private FragmentLocationChoiceBinding binding;
    private AccessibleMapView map;
    private static final String TAG = "LocationChoiceFragment";
    private IMapController mapController;
    private LocationManager locationManager;
    private Marker currentLocationMarker;
    private Marker currentWasteLocationMarker;
    private Button validateButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLocationChoiceBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        validateButton = binding.validateButton;
        // deactivate button if no location is selected
        validateButton.setEnabled(false);
        validateButton.setOnClickListener(v -> {
            if (currentWasteLocationMarker != null) {
                GeoPoint location = currentWasteLocationMarker.getPosition();
                Log.v(TAG, "Location selected: " + location);
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", location.getLatitude());
                bundle.putDouble("longitude", location.getLongitude());
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_navigation_location_choice_to_navigation_waste_detail_report, bundle);
            }
        });
        Context ctx = getContext();
        if (ctx != null) {
            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));
            locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        }

        setupMapView();
        updateMapToCurrentLocation();
        configureMapClick();

        return view;
    }

    private void setupMapView() {
        map = binding.mapView1;
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        mapController = map.getController();
    }

    private boolean checkLocationPermission() {
        Context context = getContext();
        return context != null &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void updateMapToCurrentLocation() {
        if ((ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                || locationManager == null || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return;
        }
        if (getContext() != null && checkLocationPermission()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, (float) 0, this);
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
        addMarkerAtLocation(startPoint);
    }

    private void configureMapClick() {
        map.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                GeoPoint geoPoint = (GeoPoint) map.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                addWasteMarker(geoPoint);
                Log.v(TAG, "Location selected: " + geoPoint);
                v.performClick();
            }
            return false;
        });
    }

    private void addWasteMarker(GeoPoint startPoint) {
        Marker marker = new Marker(map);
        marker.setPosition(startPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);


        marker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.location));
        marker.setTitle("Nouveau déchet");
        if (currentWasteLocationMarker != null) {
            map.getOverlays().remove(currentWasteLocationMarker);
        }
        map.getOverlays().add(marker);
        currentWasteLocationMarker = marker;
        validateButton.setEnabled(true);
        map.invalidate();
    }

    private void addMarkerAtLocation(GeoPoint location) {
        if (map == null || map.getRepository() == null) {
            Log.e(TAG, "MapView is not initialized");
            return;
        }
        Marker marker = new Marker(map);
        marker.setPosition(location);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        marker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.my_location));

        marker.setTitle("Votre localisation");

        if (currentLocationMarker != null) {
            map.getOverlays().remove(currentLocationMarker);
        }
        map.getOverlays().add(marker);
        currentLocationMarker = marker;

        map.invalidate();
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        // if map is not initialized yet, do nothing
        if (map == null) {
            return;
        }
        addMarkerAtLocation(new GeoPoint(location.getLatitude(), location.getLongitude()));
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
        Toast.makeText(getContext(), "Veuillez activer la localisation", Toast.LENGTH_SHORT).show();
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
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this);
            }
            updateMapToCurrentLocation();
        } else {
            Toast.makeText(getContext(), "La localisation est nécessaire pour signaler le déchet", Toast.LENGTH_SHORT).show();
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
