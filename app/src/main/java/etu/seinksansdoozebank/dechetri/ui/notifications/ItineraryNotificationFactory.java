package etu.seinksansdoozebank.dechetri.ui.notifications;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import etu.seinksansdoozebank.dechetri.MainActivity;
import etu.seinksansdoozebank.dechetri.R;

public class ItineraryNotificationFactory extends NotificationFactory {
    @Override
    protected INotification createNotification(Activity activity, Context context, String title, String message, String channelId, int priority) {
        return new ItineraryNotification(this.createCallback(context, title, message, channelId, priority), activity, context);
    }

    private NotificationPermissionCallback createCallback(Context context, String title, String message, String channelId, int priority) {
        int currentNotificationId = getNextNotificationId();
        return new NotificationPermissionCallback() {
            @Override
            public void onPermissionGranted() {
                try {
                    // Build the notification
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                            .setContentTitle(title)
                            .setPriority(priority)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                            .setAutoCancel(true);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.notify(currentNotificationId, builder.build());
                } catch (SecurityException e) {
                    // Handle security exception
                    e.printStackTrace();
                }
            }

            @Override
            public void onPermissionDenied() {
                // Do nothing
            }
        };
    }
}
