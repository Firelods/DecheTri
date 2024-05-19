package etu.seinksansdoozebank.dechetri.ui.notifications;

import android.app.Activity;
import android.content.Context;

public interface INotification {
    void sendNotification(Activity activity, Context context, String title, String message);
}
