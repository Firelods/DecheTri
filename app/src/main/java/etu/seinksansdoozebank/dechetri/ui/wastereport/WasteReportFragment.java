package etu.seinksansdoozebank.dechetri.ui.wastereport;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import etu.seinksansdoozebank.dechetri.databinding.FragmentWasteReportBinding;

public class WasteReportFragment extends Fragment {
    private FragmentWasteReportBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        WasteReportViewModel wasteReportViewModel =
                new ViewModelProvider(this).get(WasteReportViewModel.class);

        binding = FragmentWasteReportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textWasteReport;
        wasteReportViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}