package etu.seinksansdoozebank.dechetri.ui.notifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationManagerCompat;

public interface INotification {
    void sendNotification(Activity activity, Context context, String title, String message, String channelId, int priority);

    default void askNotificationPermission(Activity activity, Context context, NotificationPermissionCallback callback) {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            callback.onPermissionGranted();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Notifications are not enabled, prompt the user to enable them
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            // Optionally, you can show a dialog to guide the user
        }
        callback.onPermissionDenied();
    }
}
