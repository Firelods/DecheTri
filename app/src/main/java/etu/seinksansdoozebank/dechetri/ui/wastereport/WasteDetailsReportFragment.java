package etu.seinksansdoozebank.dechetri.ui.wastereport;


import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.faltenreich.skeletonlayout.Skeleton;

import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import etu.seinksansdoozebank.dechetri.R;


public class WasteDetailsReportFragment extends Fragment {
    interface GeocodeCallback {
        void onAddressFound(String address);

        void onError(Exception e);
    }

    private final Executor executor = Executors.newSingleThreadExecutor();
    private EditText localisationInput;

    private Skeleton skeleton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waste_details_report, container, false);
        localisationInput = view.findViewById(R.id.LocalisationInput);
        TextView locationTextView = view.findViewById(R.id.locationText);
        Button previousButton = view.findViewById(R.id.PrecedentButton);
        skeleton = view.findViewById(R.id.skeletonLayout);
        skeleton.showSkeleton();

        previousButton.setOnClickListener(v -> {
            requireActivity().onBackPressed();// TODO: Demander au prof pour l'UX du bouton précédent
        });
        if (getArguments() == null) {
            Log.e("WasteDetailsReportFragment", "Arguments are null");
            return view;
        }
        double latitude = getArguments().getDouble("latitude");
        double longitude = getArguments().getDouble("longitude");
        locationTextView.setText("Lat : " + latitude + " Long : " + longitude);
        fetchAddress(latitude, longitude, new GeocodeCallback() {
            @Override
            public void onAddressFound(String address) {
                requireActivity().runOnUiThread(() -> {
                    Log.d("Address", "Address: " + address);
                    localisationInput.setText(address);
                    skeleton.showOriginal();
                    localisationInput.setEnabled(false);
                });
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() -> Log.e("Geocoding", "Failed to fetch address", e));
            }
        });

        return view;
    }

    private void fetchAddress(double latitude, double longitude, GeocodeCallback callback) {
        executor.execute(() -> {
            try {
                GeocoderNominatim geocoder = new GeocoderNominatim(Locale.getDefault(), "user-agent");
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (!addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    callback.onAddressFound(address.getAddressLine(0) + ", " + address.getLocality() + ", " + address.getCountryName());
                } else {
                    callback.onAddressFound("No address found");
                }
            } catch (IOException e) {
                callback.onError(e);
            }
        });
    }
}
