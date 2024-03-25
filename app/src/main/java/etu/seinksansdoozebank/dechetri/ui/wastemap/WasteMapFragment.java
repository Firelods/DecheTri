package etu.seinksansdoozebank.dechetri.ui.wastemap;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.databinding.FragmentWasteMapBinding;

public class WasteMapFragment extends Fragment {
    private FragmentWasteMapBinding binding;

    private MapView map;
    private IMapController mapController;

    private static final String TAG = "OsmFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialisation du binding avec le layout du fragment
        binding = FragmentWasteMapBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialisation de la carte
        Context ctx = getContext();
        if (ctx != null) {
            Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        }

        if (isStoragePermissionGranted()) {
            // Configuration de la carte
            map = binding.mapView; // Assurez-vous que l'ID dans le layout du fragment correspond
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);
            mapController = map.getController();
            mapController.setZoom(15);
            GeoPoint startPoint = new GeoPoint(51496994, -134733);
            mapController.setCenter(startPoint);
        }

        return view;
    }

    private boolean isStoragePermissionGranted() {
        Context context = getContext();
        if (context == null) return false;

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission is granted");
            return true;
        } else {
            Log.v(TAG, "Permission is revoked");
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + " was " + grantResults[0]);
            // Reprendre les tâches nécessitant cette permission
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}