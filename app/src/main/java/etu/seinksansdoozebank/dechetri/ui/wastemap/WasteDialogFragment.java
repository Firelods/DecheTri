package etu.seinksansdoozebank.dechetri.ui.wastemap;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Notification;
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
import etu.seinksansdoozebank.dechetri.ui.notifications.INotification;
import etu.seinksansdoozebank.dechetri.ui.notifications.ItineraryNotificationFactory;
import etu.seinksansdoozebank.dechetri.ui.notifications.NotificationFactory;
import etu.seinksansdoozebank.dechetri.ui.notifications.NotificationHelper;
import etu.seinksansdoozebank.dechetri.ui.notifications.NotificationType;
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
        final int[] notificationId = new int[1];
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
                if (imageBytes != null) {
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

                    notificationId[0] = NotificationFactory.sendNotification(NotificationType.ITINERARY, getActivity(), getContext(), "Itinerary", "Itinerary for waste: " + waste.getName() + " at address " + waste.getAddress(), NotificationHelper.CHANNEL_ID_ITINERARY, Notification.PRIORITY_DEFAULT);
                }
            });

            buttonConfirm.setOnClickListener(v -> APIController.completeTask(waste.getId(), new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.d("WasteDialogFragment", "Error completing task for waste at " + waste.getName() + " : " + e.getMessage());
                    requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error completing task for waste at " + waste.getName(), Toast.LENGTH_SHORT).show());
                    NotificationFactory.sendNotification(NotificationType.TASK_COMPLETED, getActivity(), getContext(), "Error completing task", "Error completing task for waste at " + waste.getName(), NotificationHelper.CHANNEL_ID_COMPLETE_TASK, Notification.PRIORITY_HIGH);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    requireActivity().runOnUiThread(() -> {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Task completed", Toast.LENGTH_SHORT).show();
                            NotificationFactory.removeNotification(getContext(), notificationId[0]);
                            dismiss();
                        } else {
                            Log.d("WasteDialogFragment", "Else Error completing task for waste at " + waste.getName() + " : " + response.message());
                            Toast.makeText(getContext(), "Error completing task : " + response.message(), Toast.LENGTH_SHORT).show();
                            NotificationFactory.sendNotification(NotificationType.TASK_COMPLETED, getActivity(), getContext(), "Error completing task", "Error completing task for waste at " + waste.getName(), NotificationHelper.CHANNEL_ID_COMPLETE_TASK, Notification.PRIORITY_HIGH);
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
        buttonDelete.setOnClickListener(v -> APIController.deleteWaste(waste.getId(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error deleting waste : " + waste.getName(), Toast.LENGTH_SHORT).show();
                    NotificationFactory.sendNotification(NotificationType.DELETE, getActivity(), getContext(), "Error deleting waste", "Error deleting waste : " + waste.getName(), NotificationHelper.CHANNEL_ID_DELETES, Notification.PRIORITY_HIGH);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Waste successfully deleted", Toast.LENGTH_SHORT).show();
                        NotificationFactory.sendNotification(NotificationType.DELETE, getActivity(), getContext(), "Waste successfully deleted", "Waste successfully deleted : " + waste.getName(), NotificationHelper.CHANNEL_ID_DELETES, Notification.PRIORITY_DEFAULT);
                        dismiss();
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error deleting waste : " + response.message(), Toast.LENGTH_SHORT).show();
                        NotificationFactory.sendNotification(NotificationType.DELETE, getActivity(), getContext(), "Error deleting waste", "Error deleting waste : " + waste.getName(), NotificationHelper.CHANNEL_ID_DELETES, Notification.PRIORITY_HIGH);
                    });
                }
            }
        }));
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void removeNotification(NotificationFactory factory, int notificationId) {
        factory.removeNotification(getContext(), notificationId);
    }
}
