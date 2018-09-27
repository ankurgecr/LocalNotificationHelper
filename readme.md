LocalNotification Helper
===========

Getting started
---------------

To get started with LocalNotification Helper, you'll need to get
add the dependency to your project build.gradle file:

To initialise your local repository using the LineageOS trees, use a command like this:
```
implementation "android.helper:localnotification:1.0.1"
```
Then to sync up your project.
Now Open your Main Activity java file
```
import android.NotificationHelper;
```
and insert following code in your overridden methods:
```
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationHelper.init(
                MainActivity.class,             /* You activity class */
                this,                           /* Context */
                getString(R.string.app_name),   /* Default title String you want to show */
                R.drawable.app_icon             /* Default icon you want to show */
        );
        //...
        //rest of the code
        //...
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationHelper.destroy();
        //...
        //rest of the code
        //...
    }
```

Good. You are all set to use LocalNotification Helper now.

In order to get list of all notifications, call this method:

```
NotificationHelper.getAll();
```

this will return list of LocalNotification objects. Here is the properties of LocalNotification class.
```
class LocalNotification {
    int notificationId;       /* Primary id of notification */
    String channelId;         /* Ignore this for now */
    String textTitle;         /* Title text on notification */
    String textContent;       /* Content text on notification */
    int smallIcon;            /* Small icon - Single color (Monochrome) png image with Transparency */
    int largeIcon;            /* Large icon - Any icon image jpg/png */
    long delay;               /* Delay in milliseconds after notification will triggered */
    int repeatCount = -1;     /* Number of repeat for your notification, -1 for 'infinite' */
    boolean isRepeat = false; /* true - if you want to repeat the same notification with fixed interval 'delay' */
}
```
In order to schedule a notification, call
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

In order to check if a notification is scheduled or not, periodically
```
boolean scheduled = NotificationHelper.isScheduled(notificationId)
```

For cancellation of a notification, call
```
NotificationHelper.cancel(notificationId);
```
or
```
LocalNotification myNotification = /**/;
NotificationHelper.cancel(myNotification);
```

For cancelling all the scheduled notifications, cancelling
```
NotificationHelper.cancelAll();
```

Demo
--------
Check the demo app for more details.
