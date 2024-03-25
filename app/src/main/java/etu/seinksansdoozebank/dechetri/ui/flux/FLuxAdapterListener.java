package etu.seinksansdoozebank.dechetri.ui.flux;

import android.content.Context;
import android.widget.ImageButton;

import etu.seinksansdoozebank.dechetri.model.flux.Flux;

public interface FLuxAdapterListener {
    void onClickBin(ImageButton bin, Flux item);

    Context getContext();
}
