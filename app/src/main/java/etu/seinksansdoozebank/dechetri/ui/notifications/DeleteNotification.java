package etu.seinksansdoozebank.dechetri.ui.notifications;

import android.app.Activity;
import android.content.Context;

public class DeleteNotification implements INotification {
    private final NotificationPermissionCallback callback;
    private final Activity activity;
    private final Context context;

    public DeleteNotification(NotificationPermissionCallback callback, Activity activity, Context context) {
        this.callback = callback;
        this.activity = activity;
        this.context = context;
    }

    @Override
    public void sendNotification() {
        askNotificationPermission(activity, context, this.callback);
    }
}
