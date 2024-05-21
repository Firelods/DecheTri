package etu.seinksansdoozebank.dechetri.ui.flux;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

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
    private final String TAG = "512Bank " + getClass().getSimpleName();
    private FragmentFluxBinding binding;
    private FluxAdapter fluxAdapter;
    private Announcement item;
    private static final int PERMISSION_REQUEST_CALENDAR = 100;
    // Initialize as empty list
    private AnnouncementList announcementList;
    private Context context;
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
        // (1) : Create a dialog
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.alert_delete_title)
                .setMessage(R.string.alert_delete_message)
                .setPositiveButton(R.string.alert_delete_yes, (dialog, which) -> {
                    // (2) : Remove item from list
                    APIController.deleteAnnouncement(item.getId(), new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            String message = e.getMessage();
                            Log.e(TAG, "Error while removing announcement : " + message);
                            requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Erreur lors de la suppression de l'annonce : " + message, Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                            Log.d(TAG, "onResponse: " + response);
                            requireActivity().runOnUiThread(() -> {
                                swipeRefreshLayout.setRefreshing(true);
                                announcementList.updateList();
                                Toast.makeText(getContext(), R.string.remove_announcement_result_success, Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                })
                .setNegativeButton(R.string.alert_delete_no, (dialog, which) -> {
                    // (3) : Do nothing
                })
                .show();

        Button buttonPositive = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        buttonPositive.setTextColor(getResources().getColor(R.color.orange_600, null));
        Button buttonNegative = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        buttonNegative.setBackgroundColor(getResources().getColor(R.color.green_700, null));
        buttonNegative.setTextColor(getResources().getColor(R.color.white_100, null));
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
        NewAnnouncementFragmentDialog.newInstance().show(requireActivity().getSupportFragmentManager(), "NewAnnouncementFragmentDialog");
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        announcementList.updateList();
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
