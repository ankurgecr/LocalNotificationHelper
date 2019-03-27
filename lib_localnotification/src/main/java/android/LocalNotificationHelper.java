package android;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
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

    private static String mDefaultTitle;
    @DrawableRes
    private static int mDefaultIcon = -1;

    /**
     * call this method in 'onCreate' of your Application or Activity class
     * <p>
     * This method is Deprecated as Context is not required for upcoming versions.
     * No need to call init, by default App Name will be used as Notification Title
     * and default drawable will be shown as Notification smallIcon
     *
     * @param context      - Application or Activity context
     * @param defaultTitle - Text you want to show by default on Title of Local Notification
     * @param defaultIcon  - Icon you want to show by default with Local Notification
     */
    @Deprecated
    public static void init(
            Context context,
            String defaultTitle,
            @DrawableRes int defaultIcon
    ) {
        mDefaultTitle = defaultTitle;
        mDefaultIcon = defaultIcon;
    }

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
     * @return default smallIcon for Notifications. If -1, it will show {@link android.helper.R.drawable.lnh_ic_stat_default}
     */
    public static int getDefaultIcon() {
        return mDefaultIcon;
    }

    /**
     * @param icon - default resource for Notifications smallIcon. If -1, it will show {@link android.helper.R.drawable.lnh_ic_stat_default}
     */
    public static void setDefaultIcon(int icon) {
        LocalNotificationHelper.mDefaultIcon = icon;
    }

    /**
     * call this method in 'onCreate' of your Application or Activity class
     *
     * @param defaultTitle - Text you want to show by default on Title of Local Notification
     * @param defaultIcon  - Icon you want to show by default with Local Notification
     */
    public static void init(
            String defaultTitle,
            @DrawableRes int defaultIcon
    ) {
        mDefaultTitle = defaultTitle;
        mDefaultIcon = defaultIcon;
    }

    /**
     * Schedules a new One-Time local notification and overrides if the
     * same notification is scheduled with same 'notificationId'
     * <p>
     * This method will be removed in up coming version as it does not allow user to specify
     * different values for DELAY and REPEAT Timings
     *
     * @param notificationId - Unique int id of {@link LocalNotification}
     * @param textContent    - Body text of your {@link LocalNotification}
     * @param delay          - Delay time in millis after which your {@link LocalNotification} should be triggered
     * @return true if notification scheduled successfully
     */
    @Deprecated
    public static boolean schedule(
            int notificationId,
            String textContent,
            long delay,
            boolean isRepeat
    ) {
        return schedule(
                notificationId,
                null,
                mDefaultIcon,
                mDefaultIcon,
                mDefaultTitle,
                textContent,
                delay,
                isRepeat ? delay : 0
        );
    }

    /**
     * Schedules a new One-Time local notification and overrides if the
     * same notification is scheduled with same 'notificationId'
     *
     * @param notificationId - Unique int id of {@link LocalNotification}
     * @param textContent    - Body text of your {@link LocalNotification}
     * @param delay          - Delay time in millis after which your {@link LocalNotification} should be triggered
     * @return true if notification scheduled successfully
     */
    public static boolean schedule(
            int notificationId,
            String textContent,
            long delay
    ) {
        return schedule(
                notificationId,
                null,
                mDefaultIcon,
                mDefaultIcon,
                mDefaultTitle,
                textContent,
                delay,
                0
        );
    }

    /**
     * Schedules a new local notification and overrides if the
     * same notification is scheduled with same 'notificationId'
     *
     * @param notificationId - Unique int id of {@link LocalNotification}
     * @param textContent    - Body text of your {@link LocalNotification}
     * @param triggerDelay   - Delay time in millis after which your {@link LocalNotification} should be triggered
     * @param repeatDelay    - Repeats after time in millis after which your {@link LocalNotification} should be repeated
     * @return true if notification scheduled successfully
     */
    public static boolean schedule(
            int notificationId,
            String textContent,
            long triggerDelay,
            long repeatDelay
    ) {
        return schedule(
                notificationId,
                null,
                mDefaultIcon,
                mDefaultIcon,
                mDefaultTitle,
                textContent,
                triggerDelay,
                repeatDelay
        );
    }

    /**
     * Schedules a new One-Time local notification and overrides if the
     * same notification is scheduled with same 'notificationId'
     *
     * @param notificationId - Unique int id of {@link LocalNotification}
     * @param textContent    - Body text of your {@link LocalNotification}
     * @param triggerTime    - Time on which your {@link LocalNotification} should be triggered
     * @return true if notification scheduled successfully
     */
    public static boolean schedule(
            int notificationId,
            String textContent,
            Date triggerTime
    ) {
        long delay = triggerTime.getTime() - System.currentTimeMillis();
        if (delay < 0)
            delay = 0;
        return schedule(
                notificationId,
                textContent,
                delay
        );
    }

    /**
     * Schedules a new local notification and overrides if the
     * same notification is scheduled with same 'notificationId'
     *
     * @param notificationId - Unique int id of {@link LocalNotification}
     * @param textContent    - Body text of your {@link LocalNotification}
     * @param triggerTime    - Time on which your {@link LocalNotification} should be triggered
     * @param repeatDelay    - Repeats after time in millis after which your {@link LocalNotification} should be repeated
     * @return true if notification scheduled successfully
     */
    public static boolean schedule(
            int notificationId,
            String textContent,
            Date triggerTime,
            long repeatDelay
    ) {
        long delay = triggerTime.getTime() - System.currentTimeMillis();
        if (delay < 0)
            delay = 0;
        return schedule(
                notificationId,
                textContent,
                delay,
                repeatDelay
        );
    }

    /**
     * Schedules a new local notification and overrides if the
     * same notification is scheduled with same 'notificationId'
     *
     * @param notificationId - Unique int id of {@link LocalNotification}
     * @param channelId      - Channel name on which you want to display your {@link LocalNotification}
     * @param smallIcon      - Small monochrome png icon drawable you want to show with your {@link LocalNotification}
     * @param largeIcon      - Colored icon image drawable you want to show with your {@link LocalNotification}
     * @param textTitle      - Title text of your {@link LocalNotification}
     * @param textContent    - Body text of your {@link LocalNotification}
     * @param triggerDelay   - Delay time in millis after which your {@link LocalNotification} should be triggered
     * @param repeatDelay    - Repeats after time in millis after which your {@link LocalNotification} should be repeated, must be greater than or equats to {@Link PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS}
     * @return true if notification scheduled successfully
     */
    public static boolean schedule(
            int notificationId,
            String channelId,
            @DrawableRes int smallIcon,
            @DrawableRes int largeIcon,
            String textTitle,
            String textContent,
            long triggerDelay,
            long repeatDelay
    ) {
        if (repeatDelay > 0 && repeatDelay < PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS) {
            throw new IllegalArgumentException("Unable to schedule repeating notification with repeat time less then [" + PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS + "] millis");
        }

        cancel(notificationId);
        if ((channelId == null || channelId.trim().length() == 0) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = TAG;// NotificationChannel.DEFAULT_CHANNEL_ID;
        }

        LocalNotification notification = new LocalNotification();
        notification.notificationId = notificationId;
        notification.channelId = channelId;
        notification.smallIcon = smallIcon;
        notification.largeIcon = largeIcon;
        notification.textTitle = textTitle;
        notification.textContent = textContent;
        notification.triggerTime = System.currentTimeMillis() + triggerDelay;
        notification.triggerDelay = triggerDelay;
        notification.repeatDelay = repeatDelay;
        //SystemClock.elapsedRealtime() - can use this here for accuracy
        scheduleNotificationJob(notification);
        return true;
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
