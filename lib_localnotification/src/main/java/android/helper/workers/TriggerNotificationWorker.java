package android.helper.workers;

import android.LocalNotificationHelper;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.helper.R;
import android.helper.entities.LocalNotification;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

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
                    if (System.currentTimeMillis() >= notification.triggerTime) {
                        triggerNotification(notification);
                    }
                    if (notification.repeatDelay > 0) {
                        long missedTime = System.currentTimeMillis() - notification.triggerTime;
                        while (missedTime > notification.repeatDelay) {
                            missedTime -= notification.repeatDelay;
                        }
                        long newTriggerDelay = notification.repeatDelay - missedTime;
                        notification.triggerDelay = newTriggerDelay;
                        notification.triggerTime = System.currentTimeMillis() + newTriggerDelay;
                        LocalNotificationHelper.scheduleNotificationJob(notification);
                    }
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
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
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
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, notification.channelId);
        if (!TextUtils.isEmpty(notification.textTitle)) {
            mBuilder.setContentTitle(notification.textTitle);
        } else {
            mBuilder.setContentTitle(getDefaultAppName(context));
        }
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(notification.textContent));
        mBuilder.setContentText(notification.textContent);
        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (notification.smallIcon != -1) {
            mBuilder.setSmallIcon(notification.smallIcon);
        } else {
            mBuilder.setSmallIcon(R.drawable.lnh_ic_stat_default);
        }
        if (notification.largeIcon != -1) {
            mBuilder.setLargeIcon(
                    ((BitmapDrawable) context.getResources().getDrawable(notification.largeIcon)).getBitmap()
            );
        }

//        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        if(soundUri != null) {
//            mBuilder.setSound(soundUri);
//        }

        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setAutoCancel(true);

        Intent mainIntent = null;

        boolean activityReserved = false;
        if (!TextUtils.isEmpty(notification.activity)) {
            try {
                Class<?> c = Class.forName(notification.activity);
                mainIntent = new Intent(context, c);
                activityReserved = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!activityReserved) {
            mainIntent = getLauncherActivityIntent(context);
        }

        if (!TextUtils.isEmpty(notification.data)) {
            mainIntent.putExtra(LocalNotificationHelper.KEY_DATA, notification.data);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(
                context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        mBuilder.setContentIntent(contentIntent);
        Notification androidNotification = mBuilder.build();
        androidNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        return androidNotification;
    }

    public static Intent getLauncherActivityIntent(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        } else {
            intent = new Intent(context, null);
        }
        return intent;
    }

    private static CharSequence getDefaultAppName(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            ApplicationInfo applicationInfo = context.getApplicationInfo();
            int stringId = applicationInfo.labelRes;
            return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
        } else {
            return "";
        }
    }
}