package android.helper.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.helper.entities.LocalNotification;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class TriggerNotificationWorker extends Worker {

    public static final String TAG = "TriggerNotificationWorker";

    private NotificationManager notificationManager;

    public TriggerNotificationWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams
    ) {
        super(context, workerParams);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            for (String tag : getTags()) {
                LocalNotification notification = LocalNotification.fromTag(tag);
                if (notification != null) {
                    triggerNotification(notification);
                }
            }
            return Result.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return Result.FAILURE;
        }
    }

    private void triggerNotification(LocalNotification notification) {
        createNotificationChannel(notification.channelId);
        notificationManager.notify(
                notification.notificationId,
                buildNotification(
                        getApplicationContext(),
                        notification
                )
        );
    }

    private boolean isChannelCreated(String channelId) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            return true;
        }
        NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
        return channel != null;
    }

    private void createNotificationChannel(String channelId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            boolean isChannelCreated = isChannelCreated(channelId);
            if (notificationManager != null && !isChannelCreated) {
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId, channelId, importance
                );
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    private static Notification buildNotification(
            Context context,
            LocalNotification notification
    ) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, notification.channelId)
                .setContentTitle(notification.textTitle)
                .setContentText(notification.textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        if (notification.smallIcon != -1) {
            mBuilder.setSmallIcon(notification.smallIcon);
        }
        if (notification.largeIcon != -1) {
            mBuilder.setLargeIcon(
                    ((BitmapDrawable) context.getResources().getDrawable(notification.largeIcon)).getBitmap()
            );
        }
        return mBuilder.build();
    }
}