package etu.seinksansdoozebank.dechetri.ui.flux;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.model.flux.Flux;

public class FluxAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private FragmentActivity activity;
    private List<Flux> fluxList;

    public FluxAdapter(FragmentActivity activity, List<Flux> fluxList) {
        this.activity = activity;
        this.fluxList = fluxList;
        this.mInflater = LayoutInflater.from(activity.getBaseContext());
    }

    @Override
    public int getCount() {
        return fluxList.size();
    }

    @Override
    public Object getItem(int i) {
        return fluxList.get(i);
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

        System.out.println(listItem);

        if (!fluxList.isEmpty()) {
            // (2) : Récupération des TextView de notre layout
            TextView appName = listItem.findViewById(R.id.flux_app_name);
            TextView title = listItem.findViewById(R.id.flux_title);
            TextView date = listItem.findViewById(R.id.flux_time);
            TextView description = listItem.findViewById(R.id.flux_description);
            ImageButton imageButton = listItem.findViewById(R.id.flux_image_bin);

            System.out.println(appName);
            System.out.println(title);
            System.out.println(date);
            System.out.println(description);

            // (3) : Récupération de l'item courant
            Flux flux = fluxList.get(i);

            // (4) : Renseignement des valeurs
            appName.setText(flux.getAppName());
            title.setText(flux.getTitle());
            date.setText(flux.getDate());
            description.setText(flux.getDescription());

            // TODO: If it's an administrator, display the delete button and add the delete event
            imageButton.setVisibility(View.GONE);
        }

        return listItem;
    }
}
