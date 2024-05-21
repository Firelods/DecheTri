package etu.seinksansdoozebank.dechetri.ui.notifications;

import android.app.Activity;
import android.content.Context;

public abstract class NotificationFactory {
    public abstract INotification createNotification(Activity activity, Context context, String title, String message, String channelId, int priority);

    public static NotificationFactory getFactory(NotificationType type) {
        switch (type) {
            case DELETE:
                return new DeleteNotificationFactory();
            case CREATE:
                return new CreateNotificationFactory();
            case TASK_COMPLETED:
                return new TaskCompletedNotificationFactory();
            default:
                throw new IllegalArgumentException("Unknown Notification Type");
        }
    }
}
