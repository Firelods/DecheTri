package etu.seinksansdoozebank.dechetri.ui.wastereport;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.Manifest;
import etu.seinksansdoozebank.dechetri.R;


public class WasteDetailsReportFragment extends Fragment {


    private EditText locationEditText;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button confirmButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_waste_details_report, container, false);

        locationEditText = view.findViewById(R.id.LocalisationInput);
        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);

        //On regarde si l'utilisateur a donné les permissions pour le service de localisation à l'application
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            //On récupère la localisation du téléphone
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateLocation(lastKnownLocation);
        }

        confirmButton = view.findViewById(R.id.ValidateButton);
        confirmButton.setOnClickListener(v -> {
            //TODO : ajouter le déchet à la liste des déchets en ayant enregistrer toutes les informations actuelles / passer au fragment d'après
        });

        return view;
    }

    /*
        Ajout de la localisation sur l'EditText
     */
    private void updateLocation(Location location) {
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String locationString = latitude + "," + longitude;
            locationEditText.setText(locationString);
        }
    }

}