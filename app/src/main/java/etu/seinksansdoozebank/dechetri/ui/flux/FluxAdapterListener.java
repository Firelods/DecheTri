package etu.seinksansdoozebank.dechetri.ui.flux;

import android.content.Context;
import android.widget.ImageButton;

import etu.seinksansdoozebank.dechetri.model.flux.Announcement;

public interface FluxAdapterListener {
    void onClickBin(ImageButton bin, Announcement item);
    void onClickCalendar(ImageButton calendar, Announcement item);

    Context getContext();
}
