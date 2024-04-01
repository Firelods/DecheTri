package etu.seinksansdoozebank.dechetri.ui.flux;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import etu.seinksansdoozebank.dechetri.databinding.FragmentFluxBinding;
import etu.seinksansdoozebank.dechetri.model.flux.Announcement;
import etu.seinksansdoozebank.dechetri.model.flux.AnnouncementList;

public class FluxFragment extends Fragment implements FluxAdapterListener {

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
        // remove item from list
        announcementList.remove(item);
        fluxAdapter.notifyDataSetChanged();
    }
}
