package etu.seinksansdoozebank.dechetri.ui.wastereport;

import android.content.Context;
import android.util.AttributeSet;

import org.osmdroid.views.MapView;

public class AccessibleMapView extends MapView {
    public AccessibleMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
