Local Notification Helper
===========

Getting started
---------------

![Notification Components](https://image.ibb.co/crmpxU/android_local_notification.png)

To get started with LocalNotificationHelper, you'll need to get
add the dependency to your project's build.gradle file:

```
dependencies {
    //other dependencies
    implementation "android.helper:localnotification:1.1.0"
}
```
Then to sync up your project.

Good. Now you are all set to use LocalNotification Helper.

List of Notifications
--------
In order to get list of all notifications async, call this method:

```
LocalNotificationHelper.getAll(new LocalNotificationHandler() {
     @Override
     public void onNotificationReceived(List<LocalNotification> notifications) {
         //..
     }
});
```
or for getting list of all notifications Sync, call this method:
```
List<LocalNotification> notificationList = LocalNotificationHelper.getAllSync();
```
make sure you do not call this sync method from Activity's main thread. You call it like this:
```
void getAllNotificationsSync() {
    new Thread(new Runnable() {
        @Override
        public void run() {
            final List<LocalNotification> notificationList = LocalNotificationHelper.getAllSync();
            //.. do all Sync tasks here
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //.. this is main thread again, do all Async tasks here
                }
            });
        }
    }).start();
}
```
Here are the properties of LocalNotification model:
```
class LocalNotification {
    int notificationId;       /* Primary id of notification */
    String channelId;         /* Ignore this for now */
    String textTitle;         /* Title text on notification */
    String textContent;       /* Content text on notification */
    int smallIcon;            /* Small icon - Single color (Monochrome) png image with Transparency */
    int largeIcon;            /* Large icon - Any icon image jpeg/png */
    long triggerTime;          /* Time in milliseconds when notification is going to get triggered next time */
    long triggerDelay;         /* Delay in milliseconds after notification will triggered */
    long repeatDelay;          /* Delay in milliseconds after notification will repeated, 0 if not repeating */
}
```
Create/Schedule or Edit/Reschedule notification
--------
In order to schedule a simple notification, call:
```
boolean result = LocalNotificationHelper.schedule(
        intNotificationId,
        stringContent,
        longTriggerDelayInMillis
);
```
or in order to schedule a repeating notification, call:
```
boolean result = LocalNotificationHelper.schedule(
        intNotificationId,
        stringContent,
        longTriggerDelayInMillis,
        longRepeateDelayInMillis
);
```
or for more options call
```
boolean result = LocalNotificationHelper.schedule(
        intNotificationId,
        stringChannelName,
        intResSmallIcon,
        intResLargeIcon,
        stringTitle,
        stringContent,
        longTriggerDelayInMillis,
        longRepeatDelayInMillis,
        booleanShouldRepeat
);
```
result will be true if you notification is scheduled successfully.

Note:
- If you will pass the 'id' of existing/already scheduled notification, it will override/reschedule the previous notification with new parameters.
- If you are scheduling a repeating notification, make sure you keep minimum 15 minutes of interval because of limitations of WorkManager

Get notification status
--------
In order to check if a notification is scheduled or not, call:
```
LocalNotificationHelper.isScheduled(notificationId, new LocalNotificationStatusHandler() {
   @Override
   public void onNotificationStatusReceived(boolean scheduled) {
       if (scheduled) {
           //..
       } else {
           //..
       }
   }
});
```
Or to check the same in Sync method,
```
boolean scheduled = LocalNotificationHelper.isScheduledSync(notificationId);
```
again, do not call this Sync method from Main thread of your activity.

Cancel/Unschedule notification
--------
For cancelling a notification, call
```
LocalNotificationHelper.cancel(notificationId);
```
or
```
LocalNotification myNotification = /**/;
LocalNotificationHelper.cancel(myNotification);
```

For cancelling all the scheduled notifications, call
```
LocalNotificationHelper.cancelAll();
```

Technology
--------
LocalNotificationHelper library uses <a href="https://developer.android.com/topic/libraries/architecture/workmanager">WorkManager</a> internally. which is the best way to perform delayed task as per <a href="https://medium.com/@aleesha/the-amazing-workmanager-in-android-ba046b69295">this</a> and <a href="https://medium.com/mindorks/lets-work-manager-do-background-processing-58356e1ab844">this</a> articles comparing WorkManager with Job Schedulers, Services, Loaders and AlarmManager

Demo
--------
Check the demo app for more details.
