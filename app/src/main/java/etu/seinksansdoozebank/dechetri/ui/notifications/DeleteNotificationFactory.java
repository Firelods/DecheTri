package etu.seinksansdoozebank.dechetri.ui.notifications;

public class DeleteNotificationFactory extends NotificationFactory {
    @Override
    public INotification createNotification() {
        return new DeleteNotification();
    }
}