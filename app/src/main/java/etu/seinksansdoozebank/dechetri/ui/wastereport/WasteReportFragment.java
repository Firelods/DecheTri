package etu.seinksansdoozebank.dechetri.ui.wastereport;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import etu.seinksansdoozebank.dechetri.databinding.FragmentWasteReportBinding;

public class WasteReportFragment extends Fragment {
    private FragmentWasteReportBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentWasteReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}