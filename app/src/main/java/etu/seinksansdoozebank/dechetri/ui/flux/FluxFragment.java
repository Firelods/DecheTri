package etu.seinksansdoozebank.dechetri.ui.flux;


import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.faltenreich.skeletonlayout.Skeleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.controller.api.APIController;
import etu.seinksansdoozebank.dechetri.databinding.FragmentFluxBinding;
import etu.seinksansdoozebank.dechetri.model.flux.Announcement;
import etu.seinksansdoozebank.dechetri.model.flux.AnnouncementList;
import etu.seinksansdoozebank.dechetri.model.flux.AnnouncementListObserver;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FluxFragment extends Fragment implements FluxAdapterListener, AnnouncementListObserver {
    private static final String TAG = "512Bank";
    private FragmentFluxBinding binding;
    private FluxAdapter fluxAdapter;
    private Announcement item;
    private static final int PERMISSION_REQUEST_CALENDAR = 100;
    // Initialize as empty list
    private AnnouncementList announcementList;
    private Context context;
    private Calendar pickedDate;
    TextView et_date;
    private SwipeRefreshLayout swipeRefreshLayout;

    public FluxFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        context = requireContext();
        binding = FragmentFluxBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        ListView listViewFlux = binding.listViewFlux;
        swipeRefreshLayout = binding.swipeRefreshLayout;
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(announcementList::updateList);

        // Create an adapter
        fluxAdapter = new FluxAdapter(this, announcementList);
        listViewFlux.setAdapter(fluxAdapter);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(requireContext().getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
        String role = sharedPreferences.getString(requireContext().getString(R.string.shared_preferences_key_role), requireContext().getResources().getString(R.string.role_user_title));

        FloatingActionButton btn_add_announcement = root.findViewById(R.id.btn_add_announcement);
        if (role.equals(requireContext().getString(R.string.role_admin_title))) {
            btn_add_announcement.setVisibility(View.VISIBLE);
            btn_add_announcement.setOnClickListener(v -> showNewAnnouncementDialog());
        } else {
            btn_add_announcement.setVisibility(View.GONE);
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onClickBin(ImageButton bin, Announcement item) {
        // remove item from list
        APIController.deleteAnnouncement(item.getId(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                String message = e.getMessage();
                Log.e("APIController", "Error while removing announcement : " + message);
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Erreur lors de la suppression de l'annonce : " + message, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                Log.d(TAG + "FluxFragment", "onResponse: " + response);
                requireActivity().runOnUiThread(() -> {
                    swipeRefreshLayout.setRefreshing(true);
                    announcementList.updateList();
                    Toast.makeText(getContext(), R.string.remove_announcement_result_success, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onClickCalendar(ImageButton calendar, Announcement item) {
        this.item = item;
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, PERMISSION_REQUEST_CALENDAR);

        } else {
            ContentResolver cr = requireContext().getContentResolver();
            addEventToCalendar(cr, this.item);
        }
    }

    private void addEventToCalendar(ContentResolver cr, Announcement announcement) {


        // Récupérer le contexte de l'activité
        Context context = getContext();
        if (context == null) {
            Log.e("FluxFragment", "Le contexte est null");
            return;
        }

        // Créer un Intent pour ajouter un événement au calendrier
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE, announcement.getTitle());
        intent.putExtra(CalendarContract.Events.DESCRIPTION, announcement.getDescription());
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

        // Convertir la date en millisecondes
        Date date = announcement.getEventDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis() + (60 * 60 * 1000)); // 1 heure après le début

        // Vérifier si une application capable de gérer cet Intent est disponible
        PackageManager packageManager = context.getPackageManager();
        if (intent.resolveActivity(packageManager) != null) {
            // Lancer l'activité pour ajouter l'événement
            context.startActivity(intent);
        } else {
            Log.e("FluxFragment", "Aucune application de calendrier disponible");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CALENDAR) {
            // Vérifier si l'autorisation a été accordée
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ContentResolver cr = requireContext().getContentResolver();
                addEventToCalendar(cr, item);
            } else {
                Toast toast = Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void showNewAnnouncementDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.add_announcement_title)
                .setView(R.layout.add_announcement)
                .setPositiveButton(R.string.add_announcement_publish, null)
                .setNegativeButton(R.string.add_announcement_cancel, null).create();
        alertDialog.create();
        alertDialog.show();
        Button btn_add_date = alertDialog.findViewById(R.id.btn_add_date);
        et_date = alertDialog.findViewById(R.id.tv_date);
        assert et_date != null;
        et_date.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                showDateTimePicker();
            }
            return true;
        });
        TextView tv_date_label = alertDialog.findViewById(R.id.tv_date_label);
        if (btn_add_date != null) {
            btn_add_date.setOnClickListener(v1 -> {
                if (tv_date_label != null && et_date != null) {
                    if (tv_date_label.getVisibility() == View.GONE) {
                        showDateTimePicker();
                        tv_date_label.setVisibility(View.VISIBLE);
                        et_date.setVisibility(View.VISIBLE);
                        btn_add_date.setText(R.string.remove_date_text);
                    } else {
                        tv_date_label.setVisibility(View.GONE);
                        et_date.setVisibility(View.GONE);
                        btn_add_date.setText(R.string.add_date_text);
                        pickedDate = null;
                    }
                }
            });
        } else {
            throw new RuntimeException("btn_add_date is null");
        }
        Button buttonPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        buttonPositive.setOnClickListener(v -> {
            EditText et_title = alertDialog.findViewById(R.id.etxt_title);
            EditText et_description = alertDialog.findViewById(R.id.etxt_description);
            String title;
            String description;
            if (et_title != null && et_description != null && et_date != null) {
                title = et_title.getText().toString();
                description = et_description.getText().toString();
                publishAnnouncement(title, description, pickedDate);
                alertDialog.dismiss();
            }
        });
        buttonPositive.setBackgroundColor(getResources().getColor(R.color.green_700, null));
        buttonPositive.setTextColor(getResources().getColor(R.color.white_100, null));
    }

    public void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        pickedDate = Calendar.getInstance();
        new DatePickerDialog(context, (view, year, monthOfYear, dayOfMonth) -> {
            pickedDate.set(year, monthOfYear, dayOfMonth);
            new TimePickerDialog(context, (view1, hourOfDay, minute) -> {
                pickedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                pickedDate.set(Calendar.MINUTE, minute);
                et_date.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(pickedDate.getTime()));
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    private void publishAnnouncement(String title, String description, Calendar eventDate) {
        Callback onResponse = new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                String message = e.getMessage();
                Log.e("APIController", "Error while creating announcement : " + message);
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Erreur lors de la publication de l'annonce : " + message, Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    requireActivity().runOnUiThread(() -> {
                        swipeRefreshLayout.setRefreshing(true);
                        announcementList.updateList();
                        Toast.makeText(getContext(), R.string.add_announcement_result_success, Toast.LENGTH_SHORT).show();
                    });
                } else {
                    requireActivity().runOnUiThread(() -> {
                        try {
                            assert response.body() != null;
                            String body = response.body().string();
                            Log.e("APIController", "Error while creating announcement : " + body);
                            Toast.makeText(getContext(), R.string.add_announcement_result_error + " : " + body, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        announcementList = new AnnouncementList(requireActivity(), context);
        announcementList.addObserver(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        announcementList.removeObserver(this);
    }

    @Override
    public void onAnnouncementListChanged() {
        requireActivity().runOnUiThread(() -> {
            fluxAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
    }
}
