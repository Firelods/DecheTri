package etu.seinksansdoozebank.dechetri.ui.notifications;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import etu.seinksansdoozebank.dechetri.R;

public class CreateNotification implements INotification {
    private int notificationId = 0;

    @Override
    public void sendNotification(Activity activity, Context context, String title, String message, String channelId, int priority) {
        askNotificationPermission(activity, context, new NotificationPermissionCallback() {
            @Override
            public void onPermissionGranted() {
                try {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                            .setContentTitle(title)
                            .setContentText(message)
                            .setPriority(priority);
                    NotificationManagerCompat.from(context).notify(++notificationId, builder.build());
                } catch (SecurityException e) {
                    Toast.makeText(context, R.string.send_notification_error, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onPermissionDenied() {
                // Do nothing
            }
        });
    }
}
