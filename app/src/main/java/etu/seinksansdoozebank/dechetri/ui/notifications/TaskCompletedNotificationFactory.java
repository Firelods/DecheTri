package etu.seinksansdoozebank.dechetri.ui.notifications;

public class TaskCompletedNotificationFactory extends NotificationFactory {

    @Override
    public INotification createNotification() {
        return new TaskCompletedNotification();
    }
}
