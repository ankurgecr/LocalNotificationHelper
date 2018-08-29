package info.ankurpandya.localnotificaion.demo;

import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.DrawableRes;

import java.util.List;

import info.ankurpandya.localnotificaion.demo.entities.AppDatabase;
import info.ankurpandya.localnotificaion.demo.entities.MyNotification;
import info.ankurpandya.localnotificaion.demo.entities.NotificationDao;
import info.ankurpandya.localnotificaion.demo.services.TriggerNotificationService;

public class NotificationHelper {

    private static Context mContext;
    private static Class<?> mCallerClass;
    private static AppDatabase appDatabase;
    private static NotificationDao notificationDao;

    private static String mDefaultTitle;
    @DrawableRes
    private static int mDefaultIcon;

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

        appDatabase = AppDatabase.getInstance(mContext);
        notificationDao = appDatabase.notificationDao();

        Intent intent = new Intent(context, TriggerNotificationService.class);
        context.startService(intent);
    }

    public static void schedule(
            int notificationId,
            String textContent,
            long delay,
            boolean isRepeat
    ) {
        String defaultChannelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            defaultChannelId = NotificationChannel.DEFAULT_CHANNEL_ID;
        }
        schedule(
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

    public static void schedule(
            int notificationId,
            String channelId,
            @DrawableRes int smallIcon,
            @DrawableRes int largeIcon,
            String textTitle,
            String textContent,
            long delay,
            boolean isRepeat
    ) {
        MyNotification notification = new MyNotification();
        notification.notificationId = notificationId;
        notification.channelId = channelId;
        notification.smallIcon = smallIcon;
        notification.largeIcon = largeIcon;
        notification.textTitle = textTitle;
        notification.textContent = textContent;
        notification.triggerTime = System.currentTimeMillis() + delay;
        notification.delay = delay;
        notification.isRepeat = isRepeat;

        //SystemClock.elapsedRealtime() - can use this here for accuracy

        notificationDao.save(notification);
    }

    public static List<MyNotification> getAll() {
        return notificationDao.getAll();
    }

    public static void cancel(int notificationId) {
        notificationDao.delete(notificationId);
    }

    public static void cancel(MyNotification notification) {
        notificationDao.delete(notification);
    }

    public static void cancelAll() {
        notificationDao.deleteAll();
    }

    public static boolean isScheduled(int notificationId) {
        return notificationDao.isScheduled(notificationId);
    }

    public static void destroy() {
        if (appDatabase != null && appDatabase.isOpen()) {
            appDatabase.close();
            appDatabase = null;
        }
    }

}
