package etu.seinksansdoozebank.dechetri.ui.notifications;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.Objects;

public class NotificationHelper extends Application {
    public static final String CHANNEL_ID_ANNOUNCEMENTS = "channel_announcements";
    public static final String CHANNEL_ID_DELETES = "channel_deletes";
    public static final String CHANNEL_ID_CREATES = "channel_creates";
    public static final String CHANNEL_ID_REPORT_WASTE = "channel_report_waste";
    public static final String CHANNEL_ID_COMPLETE_TASK = "channel_complete_task";
    private static NotificationManager notificationManager;

    private void createNotificationChannel(String channelId, String name, String description, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, name, importance);
            notificationChannel.setDescription(description);
            notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        createNotificationChannel(CHANNEL_ID_ANNOUNCEMENTS, "Announcements", "Channel for announcements notifications", NotificationManager.IMPORTANCE_DEFAULT);
        createNotificationChannel(CHANNEL_ID_DELETES, "Deletes", "Channel for delete notifications", NotificationManager.IMPORTANCE_DEFAULT);
        createNotificationChannel(CHANNEL_ID_CREATES, "Creates", "Channel for create notifications", NotificationManager.IMPORTANCE_DEFAULT);
        createNotificationChannel(CHANNEL_ID_REPORT_WASTE, "Report Waste", "Channel for waste report notifications", NotificationManager.IMPORTANCE_DEFAULT);
        createNotificationChannel(CHANNEL_ID_COMPLETE_TASK, "Complete Task", "Channel for task completion notifications", NotificationManager.IMPORTANCE_DEFAULT);
    }
}
