package etu.seinksansdoozebank.dechetri.ui.wastemap;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.IOException;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.controller.api.APIController;
import etu.seinksansdoozebank.dechetri.databinding.FragmentWasteDialogBinding;
import etu.seinksansdoozebank.dechetri.model.waste.Waste;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A fragment that shows the details of waste on click of map.</p>
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
        Button buttonConfirm = view.findViewById(R.id.btnConfirm);
        ImageView wasteImage = view.findViewById(R.id.wasteImage);
        TextView wasteType = view.findViewById(R.id.wasteType);

        if (getArguments() != null) {
            waste = getArguments().getParcelable("waste");
            if (waste != null) {
                wasteName.setText(waste.getName());
                wasteAddress.setText(waste.getAddress());
                wasteType.setText(waste.getType().getName());
                byte[] imageBytes = waste.getImageData();
                if (imageBytes != null){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    wasteImage.setImageBitmap(bitmap);
                } else {
                    Log.e("WasteDialogFragment", "Given waste image is null");
                }
            } else {
                Log.e("WasteDialogFragment", "Given waste is null");
            }
        }

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
        String defaultRole = getResources().getString(R.string.role_user_title); //user by default
        String role = sharedPreferences.getString(getString(R.string.shared_preferences_key_role), defaultRole);
        if (role.equals(getResources().getString(R.string.role_manager_title)) || role.equals(getResources().getString(R.string.role_employee_title))) {
            buttonItinary.setVisibility(View.VISIBLE);
            buttonConfirm.setVisibility(View.VISIBLE);
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

            buttonConfirm.setOnClickListener(v -> APIController.completeTask(waste.getId(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error completing task : " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    requireActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Task completed", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(getContext(), "Error completing task : " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }));
        } else {
            buttonItinary.setVisibility(View.GONE);
            buttonConfirm.setVisibility(View.GONE);
        }
        if (role.equals(getString(R.string.role_admin_title))) {
            this.configureDeleteButton(buttonDelete);
        } else {
            buttonDelete.setVisibility(View.GONE);
        }
        return binding.getRoot();
    }

    private void configureDeleteButton(Button buttonDelete) {
        buttonDelete.setVisibility(View.VISIBLE);
        buttonDelete.setOnClickListener(v->{
            onClickDeleteWaste();
        });
    }

    public void onClickDeleteWaste() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Confirmation de suppression")
                .setMessage("Etes-vous sur de vouloir supprimer ce signalement ? Ne le supprimer que s'il n'est pas sérieux, offensant ou hors de porté.")
                .setPositiveButton("Supprimer", (dialog, which) -> {
                    APIController.deleteWaste(waste.getId(), new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            String message = e.getMessage();
                            Log.e("APIController", "Error while removing waste : " + message);
                            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(),"Erreur lors de la suppression du déchet:"+e.getMessage(),Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                            requireActivity().runOnUiThread(() -> {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(),"Déchet supprimé avec succès.",Toast.LENGTH_SHORT).show();
                                    dismiss();
                                } else {
                                    Toast.makeText(getContext(),"Erreur lors de la suppression du déchet :"+response.message(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                })
                .setNegativeButton("Conserver", (dialog, which) -> {
                    // Do nothing
                })
                .show();

        Button deletionButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        deletionButton.setTextColor(getResources().getColor(R.color.orange_600, null));
        Button conservationButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        conservationButton.setBackgroundColor(getResources().getColor(R.color.green_700, null));
        conservationButton.setTextColor(getResources().getColor(R.color.white_100, null));
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
