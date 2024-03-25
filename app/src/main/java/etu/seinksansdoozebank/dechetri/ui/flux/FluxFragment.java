package etu.seinksansdoozebank.dechetri.ui.flux;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import etu.seinksansdoozebank.dechetri.databinding.FragmentFluxBinding;

public class FluxFragment extends Fragment {

    private FragmentFluxBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FluxViewModel fluxViewModel =
                new ViewModelProvider(this).get(FluxViewModel.class);

        binding = FragmentFluxBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textFlux;
        fluxViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}