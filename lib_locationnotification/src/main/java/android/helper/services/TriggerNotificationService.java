package android.helper.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.helper.entities.AppDatabase;
import android.helper.entities.LocalNotification;
import android.helper.entities.NotificationDao;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.List;

public class TriggerNotificationService extends Service {

    private Handler handler = new Handler();
    private Runnable getAdCampaignsTask;
    private AppDatabase appDatabase;
    private NotificationDao notificationDao;
    private NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        appDatabase = AppDatabase.getInstance(this);
        notificationDao = appDatabase.notificationDao();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        startTimer();
    }

    private void startTimer() {
        if (handler != null && getAdCampaignsTask != null) {
            handler.removeCallbacks(getAdCampaignsTask);
        }
        getAdCampaignsTask = new Runnable() {
            @Override
            public void run() {
                triggerNotifications();
                handler.postDelayed(this, 1000L);
            }
        };
        handler.post(getAdCampaignsTask);
    }

    private void triggerNotifications() {
        long currentTime = System.currentTimeMillis();
        List<LocalNotification> notificationList =
                notificationDao.loadTriggerable(currentTime);

        if (notificationList == null || notificationList.size() == 0) {
            //System.out.println("-- No notification to trigger now --");
        } else {
            for (LocalNotification notification : notificationList) {
                //System.out.println("---- found notification [" + notification.notificationId + "] ----");
                triggerNotification(notification);
                if (notification.isRepeat) {
                    notification.triggerTime = currentTime + notification.delay;
                    if (notification.repeatCount > 0) {
                        notification.repeatCount = notification.repeatCount - 1;
                        if (notification.repeatCount == 0) {
                            notification.isRepeat = false;
                        }
                    }
                    notificationDao.save(notification);
                } else {
                    notificationDao.delete(notification);
                }
            }
        }
    }

    private void triggerNotification(LocalNotification notification) {
        notificationManager.notify(
                notification.notificationId,
                buildNotification(
                        getApplicationContext(),
                        notification.notificationId,
                        notification.channelId,
                        notification.smallIcon,
                        notification.largeIcon,
                        notification.textTitle,
                        notification.textContent
                )
        );
    }

    private static Notification buildNotification(
            Context context,
            int notificationId,
            String channelId,
            @DrawableRes int smallIcon,
            @DrawableRes int largeIcon,
            String textTitle,
            String textContent
    ) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (smallIcon != -1) {
            mBuilder.setSmallIcon(smallIcon);
        }
        if (largeIcon != -1) {
            mBuilder.setLargeIcon(
                    ((BitmapDrawable) context.getResources().getDrawable(largeIcon)).getBitmap()
            );
        }

        //.setCategory(NotificationCompat.CATEGORY_MESSAGE);
        //.setAutoCancel(true)
        //
        //.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        return mBuilder.build();
    }


    @Override
    public void onDestroy() {
        handler.removeCallbacks(getAdCampaignsTask);
        appDatabase.close();
        super.onDestroy();
    }
}