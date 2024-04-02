package etu.seinksansdoozebank.dechetri.ui.wastereport;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.SimpleLocationOverlay;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.databinding.FragmentLocationChoiceBinding;
import etu.seinksansdoozebank.dechetri.databinding.FragmentWasteReportBinding;

public class LocationChoiceFragment extends Fragment {
    private FragmentLocationChoiceBinding binding;
    private AccessibleMapView map;
    private static final String TAG = "LocationChoiceFragment";
    private IMapController mapController;
    private Drawable markerDrawable;
    private LocationManager locationManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLocationChoiceBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        Context ctx = getContext();
        if (ctx != null) {
            markerDrawable = ContextCompat.getDrawable(getContext(), R.drawable.photo_library);
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
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, location -> {
                GeoPoint startPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                mapController.setCenter(startPoint);
            });
        }
        mapController.setZoom(15.0);

    }

    private void configureMapClick() {
        map.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                GeoPoint geoPoint = (GeoPoint) map.getProjection().fromPixels((int) event.getX(), (int) event.getY());
                addMarkerAtLocation(geoPoint);
                Log.v(TAG, "Location selected: " + geoPoint);
                v.performClick();
            }
            return false;
        });
    }

    private void addMarkerAtLocation(GeoPoint location) {

        map.getOverlays().clear();

        Marker marker = new Marker(map);
        marker.setPosition(location);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);


        marker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.my_location));
        marker.setTitle("Nouveau déchet");

        map.getOverlays().add(marker);

        map.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume(); // Pour gérer le cycle de vie de la carte
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause(); // Pour gérer le cycle de vie de la carte
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDetach(); // Pour nettoyer les ressources de la carte
    }
}
