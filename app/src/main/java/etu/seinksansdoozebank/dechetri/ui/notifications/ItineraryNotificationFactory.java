package etu.seinksansdoozebank.dechetri.ui.notifications;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

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
                    // Create an intent that opens the MainActivity
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                    // Build the notification
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                            .setContentTitle(title)
                            .setPriority(priority)
                            .setContentIntent(pendingIntent)
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