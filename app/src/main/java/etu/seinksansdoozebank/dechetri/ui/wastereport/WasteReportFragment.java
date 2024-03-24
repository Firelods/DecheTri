package etu.seinksansdoozebank.dechetri.ui.wastereport;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import etu.seinksansdoozebank.dechetri.R;

public class WasteReportFragment extends Fragment {

    private WasteReportViewModel mViewModel;

    public static WasteReportFragment newInstance() {
        return new WasteReportFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waste_report, container, false);
    }



}