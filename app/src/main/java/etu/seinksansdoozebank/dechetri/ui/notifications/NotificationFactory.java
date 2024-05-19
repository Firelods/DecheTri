package etu.seinksansdoozebank.dechetri.ui.notifications;

public abstract class NotificationFactory {
    public abstract INotification createNotification();

    public static NotificationFactory getFactory(NotificationType type) {
        switch (type) {
            case DELETE:
                return new DeleteNotificationFactory();
            case CREATE:
                return new CreateNotificationFactory();
            default:
                throw new IllegalArgumentException("Unknown Notification Type");
        }
    }
}