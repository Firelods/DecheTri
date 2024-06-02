package etu.seinksansdoozebank.dechetri.ui.notifications;

import android.app.Activity;
import android.content.Context;

public class CreateNotification implements INotification {
    private final NotificationPermissionCallback callback;
    private final Activity activity;
    private final Context context;

    public CreateNotification(NotificationPermissionCallback callback, Activity activity, Context context) {
        this.callback = callback;
        this.activity = activity;
        this.context = context;
    }

    @Override
    public void sendNotification() {
        askNotificationPermission(activity, context, this.callback);
    }

}
