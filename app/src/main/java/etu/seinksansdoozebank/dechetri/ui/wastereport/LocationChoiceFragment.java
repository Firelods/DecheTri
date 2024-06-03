package etu.seinksansdoozebank.dechetri.ui.wastereport;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private Marker currentLocationMarker;
    private Marker currentWasteLocationMarker;
    private Button validateButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLocationChoiceBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        validateButton = binding.validateButton;
        if (getArguments() == null) {
            Log.e("LocationChoiceFragment", "Arguments are null");
        }
        byte[] chosenImage = getArguments().getByteArray("image");
        // deactivate button if no location is selected
        validateButton.setEnabled(false);
        validateButton.setOnClickListener(v -> {
            if (currentWasteLocationMarker != null) {
                GeoPoint location = currentWasteLocationMarker.getPosition();
                Log.v(TAG, "Location selected: " + location);
                Bundle bundle = new Bundle();

                bundle.putDouble("latitude", location.getLatitude());
                bundle.putDouble("longitude", location.getLongitude());
                bundle.putByteArray("image",chosenImage);
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_navigation_location_choice_to_navigation_waste_detail_report, bundle);
            }
        });
        initializeLocationManager();

        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                Log.v(TAG, "Location permission granted");
                setupMapView();
                configureMapClick();
                updateMapToCurrentLocation();
            } else {
                Log.v(TAG, "Location permission denied");
            }
        });
        setupMapView();
        if (checkLocationPermission()) {
            updateMapToCurrentLocation();
        } else {
            requestLocationPermission();
        }
        configureMapClick();

        return view;
    }

    private void initializeLocationManager() {
        Context ctx = getContext();
        if (ctx != null) {
            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osmdroid", Context.MODE_PRIVATE));
            locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        }
    }
    private void requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
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
            mapController.setCenter(new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
            mapController.setZoom(15.0);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this::setLocationOnPoint);
        }
    }

    private void setLocationOnPoint(Location lastKnownLocation) {
        GeoPoint startPoint = new GeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

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

    private void addMarker(GeoPoint location, int drawableId, String title, boolean isWasteMarker) {
        if (map == null || map.getRepository() == null) {
            Log.e(TAG, "MapView is not initialized");
            return;
        }
        Marker marker = new Marker(map);
        marker.setPosition(location);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        marker.setIcon(ContextCompat.getDrawable(requireContext(), drawableId));
        marker.setTitle(title);

        if (isWasteMarker) {
            if (currentWasteLocationMarker != null) {
                map.getOverlays().remove(currentWasteLocationMarker);
            }
            currentWasteLocationMarker = marker;
            validateButton.setEnabled(true);
        } else {
            if (currentLocationMarker != null) {
                map.getOverlays().remove(currentLocationMarker);
            }
            currentLocationMarker = marker;
        }

        map.getOverlays().add(marker);
        map.invalidate();
    }

    private void addWasteMarker(GeoPoint startPoint) {
        addMarker(startPoint, R.drawable.location, getString(R.string.nouveau_dechet), true);
    }

    private void addMarkerAtLocation(GeoPoint location) {
        addMarker(location, R.drawable.my_location, getString(R.string.votre_position), false);
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
            if (locationManager != null) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this);
            }
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
        if (map != null) {
            map.onDetach();
        }
    }
}
