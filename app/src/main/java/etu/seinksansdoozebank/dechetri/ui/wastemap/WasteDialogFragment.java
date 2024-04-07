package etu.seinksansdoozebank.dechetri.ui.wastemap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.databinding.FragmentWasteDialogBinding;

/**
 * <p>A fragment that shows the details of waste on click of map.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     WasteDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class WasteDialogFragment extends BottomSheetDialogFragment {

    private FragmentWasteDialogBinding binding;

    private Button buttonItinary;
    private TextView wasteAddress;

    private TextView wasteName;

    private Waste waste;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentWasteDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        buttonItinary = view.findViewById(R.id.buttonItinerary);
        wasteAddress = view.findViewById(R.id.wasteAddress);
        wasteName = view.findViewById(R.id.wasteName);

        if (getArguments() != null) {
            String name = getArguments().getString("name");
            wasteName.setText(name);
            double latitude = getArguments().getDouble("latitude");
            double longitude = getArguments().getDouble("longitude");
            String address = getArguments().getString("address");
            wasteAddress.setText(address);
            waste = new Waste(name, latitude, longitude, address);
        }
        buttonItinary.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View v) {
                String address = waste.getAddress();
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Uri.encode(address));

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(mapIntent);
            }
        });
        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}