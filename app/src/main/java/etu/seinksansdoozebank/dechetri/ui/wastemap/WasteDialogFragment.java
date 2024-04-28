package etu.seinksansdoozebank.dechetri.ui.wastemap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.controller.api.APIController;
import etu.seinksansdoozebank.dechetri.databinding.FragmentWasteDialogBinding;
import etu.seinksansdoozebank.dechetri.model.waste.Waste;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * <p>A fragment that shows the details of waste on click of map.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     WasteDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */
public class WasteDialogFragment extends BottomSheetDialogFragment {

    private FragmentWasteDialogBinding binding;

    private Waste waste;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentWasteDialogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        Button buttonItinary = view.findViewById(R.id.buttonItinerary);
        TextView wasteAddress = view.findViewById(R.id.wasteAddress);
        TextView wasteName = view.findViewById(R.id.wasteName);
        Button buttonDelete = view.findViewById(R.id.btnDelete);

        if (getArguments() != null) {
            waste = (Waste) getArguments().getParcelable("waste");
            if (waste != null) {
                wasteName.setText(waste.getName());
                wasteAddress.setText(waste.getAddress());
            } else {
                Log.e("WasteDialogFragment", "Given waste is null");
            }
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

        buttonDelete.setOnClickListener(v -> APIController.deleteWaste(waste.getId(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Erreur lors de la suppression du déchet : " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Déchet supprimé avec succès.", Toast.LENGTH_SHORT).show();
                        dismiss();
                    });
                } else {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Erreur lors de la suppression du déchet : " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }
        }));
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
