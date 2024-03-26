package etu.seinksansdoozebank.dechetri.ui.flux;

import android.content.Context;
import android.widget.ImageButton;

import etu.seinksansdoozebank.dechetri.model.flux.Announcement;

public interface FLuxAdapterListener {
    void onClickBin(ImageButton bin, Announcement item);

    Context getContext();
}
