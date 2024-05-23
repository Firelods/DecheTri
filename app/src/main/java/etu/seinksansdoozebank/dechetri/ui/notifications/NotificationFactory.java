package etu.seinksansdoozebank.dechetri.ui.notifications;

import android.app.Activity;
import android.content.Context;

import androidx.core.app.NotificationManagerCompat;

public abstract class NotificationFactory {
    private static int notificationIdCounter = 0;

    protected static synchronized int getNextNotificationId() {
        return notificationIdCounter++;
    }


    public abstract INotification createNotification(Activity activity, Context context, String title, String message, String channelId, int priority);

    public static NotificationFactory getFactory(NotificationType type) {
        switch (type) {
            case DELETE:
                return new DeleteNotificationFactory();
            case CREATE:
                return new CreateNotificationFactory();
            case TASK_COMPLETED:
                return new TaskCompletedNotificationFactory();
            case ITINERARY:
                return new ItineraryNotificationFactory();
            default:
                throw new IllegalArgumentException("Unknown Notification Type");
        }
    }

    public static int sendNotification(NotificationType type, Activity activity, Context context, String title, String message, String channelId, int priority) {
        NotificationFactory factory = getFactory(type);
        INotification notification = factory.createNotification(activity, context, title, message, channelId, priority);
        notification.sendNotification();
        return notificationIdCounter;
    }

    public static void removeNotification(Context context, int notificationId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(notificationId);
    }
}
