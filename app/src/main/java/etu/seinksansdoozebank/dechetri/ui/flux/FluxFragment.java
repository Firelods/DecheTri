package etu.seinksansdoozebank.dechetri.ui.flux;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import etu.seinksansdoozebank.dechetri.databinding.FragmentFluxBinding;
import etu.seinksansdoozebank.dechetri.model.flux.Flux;

public class FluxFragment extends Fragment implements FLuxAdapterListener {

    private FragmentFluxBinding binding;
    private FluxAdapter fluxAdapter;
    private ListView listViewFlux;
    private List<Flux> fluxList;

    public FluxFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFluxBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listViewFlux = binding.listViewFlux;

        // Create a list of flux
        fluxList = new ArrayList<>();
        fluxList.add(new Flux("Collecte des déchets", "Le 12/12/2021", "La collecte des déchets aura lieu le 12/12/2021.\nliorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam nec nisl ac nisi tincidunt tincidunt. Donec auctor, nunc nec ultricies ultricies, nunc nisl ultricies elit, nec ultricies elit nisl nec nisl. Nullam nec nisl ac nisi tincidunt tincidunt. Donec auctor, nunc nec ultricies ultricies, nunc nisl ultricies elit, nec ultricies elit nisl nec nisl. Nullam nec nisl ac nisi tincidunt tincidunt. Donec auctor, nunc nec ultricies ultricies, nunc nisl ultricies elit, nec ultricies elit nisl nec nisl."));
        fluxList.add(new Flux("Collecte des déchets", "Le 19/12/2021", "La collecte des déchets aura lieu le 19/12/2021."));
        fluxList.add(new Flux("Collecte des déchets", "Le 26/12/2021", "La collecte des déchets aura lieu le 26/12/2021."));
        fluxList.add(new Flux("Collecte des déchets", "Le 02/01/2022", "La collecte des déchets aura lieu le 02/01/2022."));
        fluxList.add(new Flux("Collecte des déchets", "Le 09/01/2022", "La collecte des déchets aura lieu le 09/01/2022."));
        fluxList.add(new Flux("Collecte des déchets", "Le 16/01/2022", "La collecte des déchets aura lieu le 16/01/2022."));
        fluxList.add(new Flux("Collecte des déchets", "Le 23/01/2022", "La collecte des déchets aura lieu le 23/01/2022."));
        fluxList.add(new Flux("Collecte des déchets", "Le 30/01/2022", "La collecte des déchets aura lieu le 30/01/2022."));
        fluxList.add(new Flux("Collecte des déchets", "Le 06/02/2022", "La collecte des déchets aura lieu le 06/02/2022."));

        // Create an adapter
        fluxAdapter = new FluxAdapter(getActivity(), fluxList);
        listViewFlux.setAdapter(fluxAdapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onClickBin(ImageButton bin, Flux item) {
        // remove item from list
        fluxList.remove(item);
    }
}
