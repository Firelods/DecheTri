package etu.seinksansdoozebank.dechetri.ui.flux;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.model.flux.Announcement;
import etu.seinksansdoozebank.dechetri.model.flux.AnnouncementType;


public class FluxAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final FluxFragment activity;
    private final List<Announcement> announcementList;

    public FluxAdapter(FluxFragment activity, List<Announcement> announcementList) {
        this.activity = activity;
        this.announcementList = announcementList;
        this.mInflater = LayoutInflater.from(activity.getContext());
    }

    @Override
    public int getCount() {
        return announcementList.size();
    }

    @Override
    public Object getItem(int i) {
        return announcementList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View listItem;

        // (1) : Réutilisation des layouts
        listItem = convertView == null ? mInflater.inflate(R.layout.item_flux, parent, false) : convertView;
        listItem.setElevation(5);

        if (!announcementList.isEmpty()) {
            // (2) : Récupération des TextView de notre layout
            TextView title = listItem.findViewById(R.id.flux_title);
            TextView date = listItem.findViewById(R.id.flux_time);
            TextView description = listItem.findViewById(R.id.flux_description);
            ImageButton imageButton = listItem.findViewById(R.id.flux_image_bin);
            ImageButton imageButtonCalendar = listItem.findViewById(R.id.calendar);

            // (3) : Récupération de l'item courant
            Announcement announcement = announcementList.get(i);

            if (announcement.getType() == AnnouncementType.EVENT) {
                imageButtonCalendar.setVisibility(View.VISIBLE);
                imageButtonCalendar.setOnClickListener(v -> {
                    ((FluxAdapterListener) activity).onClickCalendar(imageButtonCalendar, announcement);
                });
            } else {
                imageButtonCalendar.setVisibility(View.GONE);
            }

            // (4) : Renseignement des valeurs
            title.setText(announcement.getTitle());
            date.setText(announcement.getPublicationDate().toString());
            description.setText(announcement.getDescription());

            SharedPreferences sharedPreferences = activity.getContext().getSharedPreferences(activity.getContext().getString(R.string.shared_preferences_file_key), MODE_PRIVATE);
            String role = sharedPreferences.getString(activity.getContext().getString(R.string.shared_preferences_key_role), activity.getContext().getResources().getString(R.string.role_user_title));

            if (role.equals(activity.getContext().getString(R.string.role_admin_title))) {
                imageButton.setVisibility(View.VISIBLE);
                imageButton.setOnClickListener(v -> {
                    // (5) : Suppression de l'item de la liste
                    ((FluxAdapterListener) activity).onClickBin(imageButton, announcement);
                });
            } else {
                imageButton.setVisibility(View.GONE);
            }
        }
        return listItem;
    }
}
