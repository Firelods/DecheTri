package etu.seinksansdoozebank.dechetri.ui.notifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public interface INotification {
    void sendNotification(Activity activity, Context context, String title, String message, String channelId, int priority);

    default void askNotificationPermission(Activity activity, Context context, NotificationPermissionCallback callback) {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            Log.d("INotification", "Notifications are enabled");
            callback.onPermissionGranted();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("INotification", "Notifications are disabled");
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } else {
            Log.d("INotification", "Notifications are disabled");
            callback.onPermissionDenied();
        }
    }
}
