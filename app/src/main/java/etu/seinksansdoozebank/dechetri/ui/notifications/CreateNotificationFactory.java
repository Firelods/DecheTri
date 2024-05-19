package etu.seinksansdoozebank.dechetri.ui.notifications;

public class CreateNotificationFactory extends NotificationFactory {
    @Override
    public INotification createNotification() {
        return new CreateNotification();
    }
}