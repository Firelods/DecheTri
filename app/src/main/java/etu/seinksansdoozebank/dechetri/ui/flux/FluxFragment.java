package etu.seinksansdoozebank.dechetri.ui.flux;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.GregorianCalendar;

import etu.seinksansdoozebank.dechetri.R;
import java.util.Calendar;


import etu.seinksansdoozebank.dechetri.databinding.FragmentFluxBinding;
import etu.seinksansdoozebank.dechetri.model.flux.Announcement;
import etu.seinksansdoozebank.dechetri.model.flux.AnnouncementList;

public class FluxFragment extends Fragment implements FluxAdapterListener {

    private FragmentFluxBinding binding;
    private FluxAdapter fluxAdapter;
    private ListView listViewFlux;

    private Announcement item;

    private final static int PERMISSION_REQUEST_CALENDAR = 100;

    private AnnouncementList announcementList = new AnnouncementList();

    public FluxFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFluxBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listViewFlux = binding.listViewFlux;

        // Create an adapter
        fluxAdapter = new FluxAdapter(this, announcementList);
        listViewFlux.setAdapter(fluxAdapter);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(getContext().getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
        String role = sharedPreferences.getString(getContext().getString(R.string.shared_preferences_key_role), getContext().getResources().getString(R.string.role_user_title));

        FloatingActionButton btn_add_announcement = root.findViewById(R.id.btn_add_announcement);
        if (role.equals(getContext().getString(R.string.role_admin_title))) {
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
        announcementList.remove(item);
        fluxAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClickCalendar(ImageButton calendar, Announcement item) {
        this.item=item;
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, PERMISSION_REQUEST_CALENDAR);

        } else {
            ContentResolver cr = getContext().getContentResolver();
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
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis() + (1 * 60 * 60 * 1000)); // 1 heure après le début

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
                ContentResolver cr = getContext().getContentResolver();
                addEventToCalendar(cr, item);
            } else {
                Toast toast = Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }






    private void showNewAnnouncementDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle("Nouvelle annonce")
                .setView(R.layout.add_announcement)
                .setPositiveButton("Ajouter", null)
                .setNegativeButton("Annuler", null).create();
        alertDialog.create();
        alertDialog.show();
        Button btn_add_date = alertDialog.findViewById(R.id.btn_add_date);
        DatePicker datePicker = alertDialog.findViewById(R.id.date_picker);
        TextView tv_date_label = alertDialog.findViewById(R.id.tv_date_label);
        if (btn_add_date != null) {
            btn_add_date.setOnClickListener(v1 -> {
                if (tv_date_label != null && datePicker != null) {
                    if (tv_date_label.getVisibility() == View.GONE) {
                        tv_date_label.setVisibility(View.VISIBLE);
                        datePicker.setVisibility(View.VISIBLE);
                        btn_add_date.setText("Enlever la date");
                    } else {
                        tv_date_label.setVisibility(View.GONE);
                        datePicker.setVisibility(View.GONE);
                        btn_add_date.setText("Ajouter une date");
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
            Date date;
            if (et_title != null && et_description != null && datePicker != null) {
                title = et_title.getText().toString();
                description = et_description.getText().toString();
                date = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth()).getTime();
                announcementList.add(new Announcement(title, date.toString(), description));
                fluxAdapter.notifyDataSetChanged();
                alertDialog.dismiss();
            }
        });
        buttonPositive.setBackgroundColor(getResources().getColor(R.color.green_700, null));
        buttonPositive.setTextColor(getResources().getColor(R.color.white_100, null));
    }
}
