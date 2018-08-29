package info.ankurpandya.localnotificaion.demo.utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;

import static android.content.Context.ALARM_SERVICE;

import java.util.Calendar;

import info.ankurpandya.localnotificaion.demo.receivers.MyNotificationPublisher;

public class NotificationHelper {

    public static int ALARM_TYPE_RTC = 100;
    private static AlarmManager alarmManagerRTC;
    private static PendingIntent alarmIntentRTC;

    public static int ALARM_TYPE_ELAPSED = 101;
    private static AlarmManager alarmManagerElapsed;
    private static PendingIntent alarmIntentElapsed;

    private static Context mContext;
    private static Class<?> mCallerClass;
    private static String mDefaultTitle;
    private static @DrawableRes
    int mDefaultIcon;

    public static void init(
            Class<?> callerClass,
            Context context,
            String defaultTitle,
            @DrawableRes int defaultIcon
    ) {
        mContext = context;
        mCallerClass = callerClass;
        mDefaultTitle = defaultTitle;
        mDefaultIcon = defaultIcon;
    }

    public static void createNotification(
            int notificationId,
            String textContent,
            long delay,
            boolean isRepeat
    ) {
        String defaultChannelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            defaultChannelId = NotificationChannel.DEFAULT_CHANNEL_ID;
        }
        createNotification(
                notificationId,
                defaultChannelId,
                mDefaultIcon,
                mDefaultIcon,
                mDefaultTitle,
                textContent,
                delay,
                isRepeat
        );
    }

    public static void createNotification(
            int notificationId,
            String channelId,
            @DrawableRes int smallIcon,
            @DrawableRes int largeIcon,
            String textTitle,
            String textContent,
            long delay,
            boolean isRepeat
    ) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext, channelId)
                .setSmallIcon(smallIcon)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        //.setAutoCancel(true)
        //.setLargeIcon(((BitmapDrawable) mContext.getResources().getDrawable(largeIcon)).getBitmap())
        //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Intent intent = new Intent(mContext, MyNotificationPublisher.class);
        PendingIntent activity = PendingIntent.getActivity(
                mContext,
                notificationId,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );
        mBuilder.setContentIntent(activity);

        Notification notification = mBuilder.build();

        Intent notificationIntent = new Intent(mContext, MyNotificationPublisher.class);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext,
                notificationId,
                notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        if (isRepeat) {
            alarmManager.setRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    futureInMillis,  //First trigger time
                    10000L,  //Interval/Delay time
                    pendingIntent
            );
        } else {
            alarmManager.set(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    futureInMillis,
                    pendingIntent
            );
        }
    }

    public static void cancelNotification(int notificationId) {
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent(mContext, MyNotificationPublisher.class);
        PendingIntent cancelServicePendingIntent = PendingIntent.getBroadcast(
                mContext,
                notificationId,
                notificationIntent,
                0
        );
        alarmManager.cancel(cancelServicePendingIntent);
    }

}
