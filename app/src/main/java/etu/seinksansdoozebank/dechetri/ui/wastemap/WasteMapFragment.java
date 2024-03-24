package etu.seinksansdoozebank.dechetri.ui.wastemap;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import etu.seinksansdoozebank.dechetri.R;

public class WasteMapFragment extends Fragment {

    private WasteMapViewModel mViewModel;

    public static WasteMapFragment newInstance() {
        return new WasteMapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_waste_map, container, false);
    }



}