package android.helper.entities;

import java.util.List;

public interface LocalNotificationHandler {
    void onNotificationReceived(List<LocalNotification> notifications);
}
