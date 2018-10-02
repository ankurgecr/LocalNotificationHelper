package android.helper.entities;

import java.util.List;

public interface NotificationCallback {
    void onNotificationReceived(List<LocalNotification> notifications);
}
