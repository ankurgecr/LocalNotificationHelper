package android;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.helper.entities.LocalNotification;
import android.helper.entities.LocalNotificationHandler;
import android.helper.entities.LocalNotificationStatusHandler;
import android.helper.workers.TriggerNotificationWorker;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.State;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;

public class LocalNotificationHelper {

    private static final String TAG = "LocalNotificationHelper";

    public static final String KEY_DATA = "LNH_DATA";

    private static String mDefaultTitle;

    @DrawableRes
    private static int mDefaultSmallIcon = -1;

    @DrawableRes
    private static int mDefaultLargeIcon = -1;

    private static boolean debugMode = false;

    private static String defaultActionActivity = null;


    /**
     * @return default title for Notifications. If NULL or BLANK, it will show Application name
     */
    public static String getDefaultTitle() {
        return mDefaultTitle;
    }

    /**
     * @param title - default text for Notifications title. If NULL or BLANK, it will show Application name
     */
    public static void setDefaultTitle(String title) {
        LocalNotificationHelper.mDefaultTitle = title;
    }

    /**
     * @return default smallIcon for Notifications. If -1, it will show 'android.helper.R.drawable.lnh_ic_stat_default'
     */
    public static int getDefaultSmallIcon() {
        return mDefaultSmallIcon;
    }

    /**
     * @return default largeIcon for Notifications. If -1, it will show 'android.helper.R.drawable.lnh_ic_stat_default'
     */
    public static int getDefaultLargeIcon() {
        return mDefaultLargeIcon;
    }

    /**
     * @param icon - default resource for Notifications smallIcon. If -1, it will show 'android.helper.R.drawable.lnh_ic_stat_default'
     */
    public static void setDefaultSmallIcon(int icon) {
        LocalNotificationHelper.mDefaultSmallIcon = icon;
    }

    /**
     * @param icon - default resource for Notifications largeIcon. If -1, it will show 'android.helper.R.drawable.lnh_ic_stat_default'
     */
    public static void setDefaultLargeIcon(int icon) {
        LocalNotificationHelper.mDefaultLargeIcon = icon;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        LocalNotificationHelper.debugMode = debugMode;
    }

    /**
     * @return path of activity to launch when notification is clicked. If NULL it opens Application's main launcher activity.
     */
    public static String getDefaultActionActivity() {
        return defaultActionActivity;
    }

    /**
     * @param defaultActionActivity -  Full path (with package name) of Activity to launch when notification is clicked.
     *                              If NULL it opens Application's main lancher activity
     */
    public static void setDefaultActionActivity(String defaultActionActivity) {
        LocalNotificationHelper.defaultActionActivity = defaultActionActivity;
    }

    public static class Scheduler {

        private int notificationId;

        private String channelId;

        private String textTitle = mDefaultTitle;

        private String textContent;

        private String activity = defaultActionActivity;

        private String data;

        private int smallIcon = mDefaultSmallIcon;

        private int largeIcon = mDefaultLargeIcon;

        private long triggerTime;

        private long triggerDelay;

        private long repeatDelay;

        public Scheduler(int notificationId, String textContent) {
            this.notificationId = notificationId;
            this.textContent = textContent;
        }

        public Scheduler setChannelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public Scheduler setTextTitle(String textTitle) {
            this.textTitle = textTitle;
            return this;
        }

        public Scheduler setActivity(String activity) {
            this.activity = activity;
            return this;
        }

        public Scheduler setData(String data) {
            this.data = data;
            return this;
        }

        public Scheduler setSmallIcon(int smallIcon) {
            this.smallIcon = smallIcon;
            return this;
        }

        public Scheduler setLargeIcon(int largeIcon) {
            this.largeIcon = largeIcon;
            return this;
        }

        public Scheduler setTriggerTime(long triggerTime) {
            this.triggerTime = triggerTime;
            return this;
        }

        public Scheduler setTriggerDelay(long triggerDelay) {
            this.triggerDelay = triggerDelay;
            return this;
        }

        public Scheduler setTriggerTime(Date time) {
            long delay = time.getTime() - System.currentTimeMillis();
            if (delay < 0)
                delay = 0;
            this.triggerDelay = delay;
            return this;
        }

        public Scheduler setRepeatDelay(long repeatDelay) {
            this.repeatDelay = repeatDelay;
            return this;
        }

        public LocalNotification schedule() {
            if (!debugMode && repeatDelay > 0 && repeatDelay < PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS) {
                throw new IllegalArgumentException("Unable to schedule repeating notification with repeat time less then [" + PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS + "] millis");
            }

            LocalNotificationHelper.cancel(notificationId);
            if ((channelId == null || channelId.trim().length() == 0) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                channelId = TAG;// NotificationChannel.DEFAULT_CHANNEL_ID;
            }

            LocalNotification notification = new LocalNotification();
            notification.notificationId = this.notificationId;
            notification.channelId = this.channelId;
            notification.smallIcon = this.smallIcon;
            notification.largeIcon = this.largeIcon;
            notification.textTitle = this.textTitle;
            notification.textContent = this.textContent;
            notification.triggerTime = System.currentTimeMillis() + this.triggerDelay;
            notification.triggerDelay = this.triggerDelay;
            notification.repeatDelay = this.repeatDelay;
            notification.activity = this.activity;
            notification.data = data;

            scheduleNotificationJob(notification);

            return notification;
        }
    }

    /**
     * Returns the list of all {@link LocalNotification}s which
     * are scheduled and going to trigger in future in
     *
     * @param callback - object of interface {@link LocalNotificationHandler}
     */
    public static void getAll(final LocalNotificationHandler callback) {
        final LiveData<List<WorkStatus>> workStatusData = WorkManager.getInstance().getStatusesByTag(TriggerNotificationWorker.TAG);
        workStatusData.observeForever(new Observer<List<WorkStatus>>() {
            @Override
            public void onChanged(@Nullable List<WorkStatus> workStatuses) {
                workStatusData.removeObserver(this);
                List<LocalNotification> localNotifications = new ArrayList<>();
                if (workStatuses != null && !workStatuses.isEmpty()) {
                    for (WorkStatus status : workStatuses) {
                        if (isStatusScheduled(status)) {
                            for (String tag : status.getTags()) {
                                try {
                                    LocalNotification notification = new Gson().fromJson(
                                            tag,
                                            LocalNotification.class
                                    );
                                    if (notification != null) {
                                        localNotifications.add(notification);
                                    }
                                } catch (Exception ignored) {

                                }
                            }
                        }
                    }
                }
                callback.onNotificationReceived(localNotifications);
            }
        });
    }

    /**
     * Returns the list of all {@link LocalNotification}s which
     * are scheduled and going to trigger in future in
     *
     * @return list of local notifications which are scheduled
     */
    public static List<LocalNotification> getAllSync() {
        final List<WorkStatus> workStatuses
                = WorkManager.getInstance().synchronous().getStatusesByTagSync(TriggerNotificationWorker.TAG);
        List<LocalNotification> localNotifications = new ArrayList<>();
        if (!workStatuses.isEmpty()) {
            for (WorkStatus status : workStatuses) {
                if (isStatusScheduled(status)) {
                    for (String tag : status.getTags()) {
                        try {
                            LocalNotification notification = new Gson().fromJson(
                                    tag,
                                    LocalNotification.class
                            );
                            if (notification != null) {
                                localNotifications.add(notification);
                            }
                        } catch (Exception ignored) {

                        }
                    }
                }
            }
        }
        return localNotifications;
    }

    /**
     * Cancels a notification with particular
     *
     * @param notificationId of LocalNotification
     */
    public static void cancel(int notificationId) {
        WorkManager.getInstance().cancelAllWorkByTag(notificationId + "");
    }

    /**
     * Cancels the given
     *
     * @param notification to cancel
     */
    public static void cancel(LocalNotification notification) {
        cancel(notification.notificationId);
    }

    /**
     * Cancels all the scheduled notifications
     */
    public static void cancelAll() {
        WorkManager.getInstance().cancelAllWork();
    }

    /**
     * Returns the status of a notification scheduled on
     *
     * @param notificationId in a
     * @param callback       object of interface {@link LocalNotificationStatusHandler}
     */
    public static void isScheduled(
            int notificationId,
            final LocalNotificationStatusHandler callback
    ) {
        final LiveData<List<WorkStatus>> workStatusData
                = WorkManager.getInstance().getStatusesByTag(notificationId + "");
        workStatusData.observeForever(new Observer<List<WorkStatus>>() {
            @Override
            public void onChanged(@Nullable List<WorkStatus> workStatuses) {
                workStatusData.removeObserver(this);
                if (workStatuses != null && !workStatuses.isEmpty()) {
                    WorkStatus status = workStatuses.get(0);
                    if (isStatusScheduled(status)) {
                        callback.onNotificationStatusReceived(true);
                    } else {
                        callback.onNotificationStatusReceived(false);
                    }
                } else {
                    callback.onNotificationStatusReceived(false);
                }
            }
        });
    }

    /**
     * Returns the status of a notification scheduled on
     *
     * @param notificationId in a
     * @return boolean true of notification is scheduled
     */
    public static boolean isScheduledSync(
            int notificationId
    ) {
        final List<WorkStatus> workStatuses
                = WorkManager.getInstance().synchronous().getStatusesByTagSync(notificationId + "");
        if (!workStatuses.isEmpty()) {
            WorkStatus status = workStatuses.get(0);
            if (isStatusScheduled(status)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param notification to Schedule. See {@link LocalNotification} for more details
     */
    public static void scheduleNotificationJob(LocalNotification notification) {
        OneTimeWorkRequest.Builder builder = new OneTimeWorkRequest.Builder(
                TriggerNotificationWorker.class
        );
        builder.addTag(TriggerNotificationWorker.TAG);
        builder.addTag(notification.toTag());
        builder.addTag(notification.notificationId + "");
        builder.setInitialDelay(
                notification.triggerDelay,
                TimeUnit.MILLISECONDS
        );
        WorkManager.getInstance().enqueue(
                builder.build()
        );
    }

    
    public static @Nullable
    String parseNotificationData(Intent intent) {
        if (intent == null)
            return null;
        return intent.getStringExtra(LocalNotificationHelper.KEY_DATA);
    }

    @Deprecated
    public static void destroy() {
        //not required any more
    }

    //region: Private methods
    private static boolean isStatusScheduled(WorkStatus status) {
        return (status.getState() == State.ENQUEUED || status.getState() == State.BLOCKED);
    }
    //endregion
}
