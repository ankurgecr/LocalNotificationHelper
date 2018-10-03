LocalNotification Helper
===========

Getting started
---------------

![Notification Components](https://image.ibb.co/crmpxU/android_local_notification.png)

To get started with LocalNotification Helper, you'll need to get
add the dependency to your project's build.gradle file:

```
dependencies {
    //other dependencies
    implementation "android.helper:localnotification:1.0.3"
}
```
Then to sync up your project.

Now Open your Main Activity java file
```
import android.NotificationHelper;
```
and insert following code in your Application or Activity class
```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    NotificationHelper.init(
            getString(R.string.app_name),   /* Default title String you want to show */
            R.drawable.app_icon             /* Default icon you want to show */
    );
    //...
    //rest of the code
    //...
}

```

Good. Now you are all set to use LocalNotification Helper.

List of Notifications
--------
In order to get list of all notifications async, call this method:

```
NotificationHelper.getAll(new LocalNotificationHandler() {
     @Override
     public void onNotificationReceived(List<LocalNotification> notifications) {
         //..
     }
});
```
or for getting list of all notifications Sync, call this method:
```
List<LocalNotification> notificationList = NotificationHelper.getAllSync();
```
make sure you do not call this sync method from Activity's main thread. You call it like this:
```
void getAllNotificationsSync() {
    new Thread(new Runnable() {
        @Override
        public void run() {
            final List<LocalNotification> notificationList = NotificationHelper.getAllSync();
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
    long delay;               /* Delay in milliseconds after notification will triggered */
    int repeatCount = -1;     /* Number of repeat for your notification, -1 for 'infinite' */
    boolean isRepeat = false; /* true - if you want to repeat the same notification with fixed interval 'delay' */
}
```
Create/Schedule or Edit/Reschedule notification
--------
In order to schedule a notification, call:
```
NotificationHelper.schedule(
        idInt,
        "",
        smallIcon,
        largeIcon,
        titleString,
        contentString,
        delayInMillis,
        repeatCountInt
);
```
If you will pass the 'id' of existing/already scheduled notification, it will override/reschedule the previous notification with new parameters.

Get notification status
--------
In order to check if a notification is scheduled or not, call:
```
NotificationHelper.isScheduled(notificationId, new LocalNotificationStatusHandler() {
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
boolean scheduled = NotificationHelper.isScheduledSync(notificationId);
```
again, do not call this Sync method from Main thread of your activity.

Cancel/Unschedule notification
--------
For cancelling a notification, call
```
NotificationHelper.cancel(notificationId);
```
or
```
LocalNotification myNotification = /**/;
NotificationHelper.cancel(myNotification);
```

For cancelling all the scheduled notifications, call
```
NotificationHelper.cancelAll();
```

Demo
--------
Check the demo app for more details.
