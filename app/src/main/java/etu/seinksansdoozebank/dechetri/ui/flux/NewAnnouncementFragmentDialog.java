package etu.seinksansdoozebank.dechetri.ui.flux;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.controller.api.APIController;
import etu.seinksansdoozebank.dechetri.ui.notifications.INotification;
import etu.seinksansdoozebank.dechetri.ui.notifications.NotificationFactory;
import etu.seinksansdoozebank.dechetri.ui.notifications.NotificationHelper;
import etu.seinksansdoozebank.dechetri.ui.notifications.NotificationType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link DialogFragment} subclass.
 * Use the {@link NewAnnouncementFragmentDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewAnnouncementFragmentDialog extends DialogFragment {
    private final String TAG = "512Bank " + getClass().getSimpleName();
    private Context context;
    private Activity activity;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextDate;
    private Button publishButton;
    private Calendar pickedDate;
    private IFluxUpdateable fluxUpdateable;

    public NewAnnouncementFragmentDialog(IFluxUpdateable fluxUpdateable) {
        this.fluxUpdateable = fluxUpdateable;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewAnnouncementFragmentDialog.
     */
    public static NewAnnouncementFragmentDialog newInstance(IFluxUpdateable fluxUpdateable) {
        return new NewAnnouncementFragmentDialog(fluxUpdateable);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = requireActivity();
        Log.d(TAG, "onAttach: " + activity.getLocalClassName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_announcement_dialog, container, false);

        context = requireContext();

        editTextTitle = view.findViewById(R.id.etxt_title);
        editTextDescription = view.findViewById(R.id.etxt_description);
        editTextDate = view.findViewById(R.id.et_date);
        ImageView imageViewClickDate = view.findViewById(R.id.iv_clear_date);
        ImageView imageViewDateHelp = view.findViewById(R.id.iv_date_picker);
        publishButton = view.findViewById(R.id.btn_publish);
        Button cancel = view.findViewById(R.id.btn_cancel);
        imageViewClickDate.setOnClickListener(v -> {
            editTextDate.setText("");
            pickedDate = null;
        });
        imageViewDateHelp.setOnClickListener(v -> new AlertDialog.Builder(context)
                .setMessage(R.string.add_announcement_help_date)
                .setPositiveButton(R.string.add_announcement_help_button, (dialog, which) -> dialog.dismiss())
                .show());
        editTextDate.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                showDateTimePicker();
            }
            return true;
        });


        publishButton.setEnabled(false);
        setupTextWatcher();
        Log.d(TAG, "showNewAnnouncementDialog: " + publishButton.isEnabled());
        publishButton.setOnClickListener(v -> {
            String title;
            String description;
            if (editTextTitle != null && editTextDescription != null && editTextDate != null) {
                title = editTextTitle.getText().toString();
                description = editTextDescription.getText().toString();
                publishAnnouncement(title, description, pickedDate, this::dismiss);
            }
        });
        cancel.setOnClickListener(v -> this.dismiss());


        return view;
    }

    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        pickedDate = Calendar.getInstance();
        new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            pickedDate.set(year, monthOfYear, dayOfMonth);
            new TimePickerDialog(context, (view1, hourOfDay, minute) -> {
                pickedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                pickedDate.set(Calendar.MINUTE, minute);
                editTextDate.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(pickedDate.getTime()));
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void setupTextWatcher() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing to do
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged: " + editTextTitle.getText().toString().trim() + " " + editTextDescription.getText().toString().trim());
                publishButton.setEnabled(
                        !editTextTitle.getText().toString().trim().isEmpty() &&
                                !editTextDescription.getText().toString().trim().isEmpty()
                );
            }
        };

        editTextTitle.addTextChangedListener(textWatcher);
        editTextDescription.addTextChangedListener(textWatcher);
    }

    private void publishAnnouncement(String title, String description, Calendar eventDate, Runnable onComplete) {
        Callback onResponse = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                String message = e.getMessage();
                Log.e("APIController", "Error while creating announcement : " + message);
                activity.runOnUiThread(() -> {
                    Toast.makeText(activity.getApplicationContext(), R.string.erreur_lors_de_la_publication_de_l_annonce, Toast.LENGTH_SHORT).show();
                    NotificationFactory.sendNotification(NotificationType.CREATE, getActivity(), getContext(), getString(R.string.create_announcement), getString(R.string.erreur_lors_de_la_publication_de_l_annonce), NotificationHelper.CHANNEL_ID_CREATES, Notification.PRIORITY_MAX);
                    onComplete.run();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(getContext(), R.string.add_announcement_result_success, Toast.LENGTH_SHORT).show();
                        NotificationFactory.sendNotification(NotificationType.CREATE, getActivity(), getContext(), getString(R.string.create_announcement), "Annonce créée avec succès", NotificationHelper.CHANNEL_ID_CREATES, Notification.PRIORITY_DEFAULT);
                        fluxUpdateable.updateFlux();
                        onComplete.run();
                    });
                } else {
                    activity.runOnUiThread(() -> {
                        try {
                            assert response.body() != null;
                            String body = response.body().string();
                            Log.e("APIController", "Error while creating announcement : " + body);
                            Toast.makeText(activity.getApplicationContext(), R.string.erreur_lors_de_la_publication_de_l_annonce, Toast.LENGTH_SHORT).show();
                            NotificationFactory.sendNotification(NotificationType.CREATE, getActivity(), getContext(), getString(R.string.create_announcement), getString(R.string.erreur_lors_de_la_publication_de_l_annonce), NotificationHelper.CHANNEL_ID_CREATES, Notification.PRIORITY_MAX);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
                onComplete.run();
            }
        };
        if (eventDate == null) {
            APIController.createAnnouncementNews(title, description, onResponse);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            String formattedEventDate = sdf.format(new Date(eventDate.getTimeInMillis()));
            APIController.createAnnouncementEvent(title, description, formattedEventDate, onResponse);
        }
    }
}
