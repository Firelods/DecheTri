package etu.seinksansdoozebank.dechetri.ui.flux;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


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
        // do nothing
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFluxBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listViewFlux = binding.listViewFlux;

        // Create an adapter
        fluxAdapter = new FluxAdapter(this, announcementList);
        listViewFlux.setAdapter(fluxAdapter);


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





}
