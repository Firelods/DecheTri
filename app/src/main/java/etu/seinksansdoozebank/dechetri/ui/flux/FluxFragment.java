package etu.seinksansdoozebank.dechetri.ui.flux;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import etu.seinksansdoozebank.dechetri.R;
import etu.seinksansdoozebank.dechetri.databinding.FragmentFluxBinding;
import etu.seinksansdoozebank.dechetri.model.flux.Announcement;
import etu.seinksansdoozebank.dechetri.model.flux.AnnouncementList;

public class FluxFragment extends Fragment implements FluxAdapterListener {
    private final String TAG = "512Bank " + getClass().getSimpleName();
    private FragmentFluxBinding binding;
    private FluxAdapter fluxAdapter;
    private ListView listViewFlux;

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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onClickBin(ImageButton bin, Announcement item) {
        // Create a popup to confirm the deletion
        // (1) : Create a dialog
        Log.d(TAG, "onClickBin: " + item);

        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.alert_delete_title)
                .setMessage(R.string.alert_delete_message)
                .setPositiveButton(R.string.alert_delete_yes, (dialog, which) -> {
                    // (2) : Remove item from list
                    announcementList.remove(item);
                    fluxAdapter.notifyDataSetChanged();
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
}
