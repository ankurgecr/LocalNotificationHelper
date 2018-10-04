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
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.State;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;

public class LocalNotificationHelper {

    private static final String TAG = "LocalNotificationHelper";

    private static Context mContext;
    private static String mDefaultTitle;
    @DrawableRes
    private static int mDefaultIcon = -1;
    private static WorkManager mWorkManager;

    /**
     * call this method in 'onCreate' of your Application or Activity class
     *
     * @param context      - Application or Activity context
     * @param defaultTitle - Text you want to show by default on Title of Local Notification
     * @param defaultIcon  - Icon you want to show by default with Local Notification
     */
    public static void init(
            Context context,
            String defaultTitle,
            @DrawableRes int defaultIcon
    ) {
        mContext = context;
        mDefaultTitle = defaultTitle;
        mDefaultIcon = defaultIcon;
        mWorkManager = WorkManager.getInstance();
    }

    /**
     * Schedules a new local notification and overrides if the
     * same notification is scheduled with same 'notificationId'
     *
     * @param notificationId - Unique int id of {@link LocalNotification}
     * @param textContent    - Body text of your {@link LocalNotification}
     * @param delay          - Delay time in millis after which your {@link LocalNotification} should be triggered
     * @param isRepeat       - boolean to indicate if {@link LocalNotification} should repeat after 'delay' interval or not
     * @return true if notification scheduled successfully
     */
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
                isRepeat
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
     * @param delay          - Delay time in millis after which your {@link LocalNotification} should be triggered
     * @param isRepeat       - boolean to indicate if {@link LocalNotification} should repeat after 'delay' interval or not
     * @return true if notification scheduled successfully
     */
    public static boolean schedule(
            int notificationId,
            String channelId,
            @DrawableRes int smallIcon,
            @DrawableRes int largeIcon,
            String textTitle,
            String textContent,
            long delay,
            boolean isRepeat
    ) {
        checkWorkManager();

        if (isRepeat && delay < PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS) {
            Toast.makeText(
                    mContext,
                    "Unable to schedule repeating notification with repeat time less then [" + PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS + "] millis",
                    Toast.LENGTH_SHORT
            ).show();
            return false;
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
        notification.triggerTime = System.currentTimeMillis() + delay;
        notification.delay = delay;
        notification.isRepeat = isRepeat;
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
        checkWorkManager();
        final LiveData<List<WorkStatus>> workStatusData = mWorkManager.getStatusesByTag(TriggerNotificationWorker.TAG);
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
        checkWorkManager();
        final List<WorkStatus> workStatuses
                = mWorkManager.synchronous().getStatusesByTagSync(TriggerNotificationWorker.TAG);
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
        checkWorkManager();
        //notificationDao.delete(notificationId);
        mWorkManager.cancelAllWorkByTag(notificationId + "");
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
        checkWorkManager();
        mWorkManager.cancelAllWork();
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
                = mWorkManager.getStatusesByTag(notificationId + "");
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
                = mWorkManager.synchronous().getStatusesByTagSync(notificationId + "");
        if (!workStatuses.isEmpty()) {
            WorkStatus status = workStatuses.get(0);
            if (isStatusScheduled(status)) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public static void destroy() {
        //not required any more
    }

    //region: Private methods
    private static void scheduleNotificationJob(LocalNotification notification) {
        if (notification.isRepeat) {
            PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(
                    TriggerNotificationWorker.class,
                    notification.delay,
                    TimeUnit.MILLISECONDS
            );
            builder.addTag(TriggerNotificationWorker.TAG);
            builder.addTag(notification.toTag());
            mWorkManager.enqueueUniquePeriodicWork(
                    notification.notificationId + "",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    builder.build()
            );
        } else {
            OneTimeWorkRequest.Builder builder = new OneTimeWorkRequest.Builder(
                    TriggerNotificationWorker.class
            );
            builder.addTag(TriggerNotificationWorker.TAG);
            builder.addTag(notification.toTag());
            builder.addTag(notification.notificationId + "");
            builder.setInitialDelay(
                    notification.delay,
                    TimeUnit.MILLISECONDS
            );
            mWorkManager.enqueue(
                    builder.build()
            );
        }
        //builder.setInputData(createInputData(notification));
    }

    private static boolean isStatusScheduled(WorkStatus status) {
        return (status.getState() == State.ENQUEUED || status.getState() == State.BLOCKED);
    }

    private static void checkWorkManager() {
        if (mWorkManager == null) {
            throw new RuntimeException("'LocalNotificationHelper' not initialised. Please initialise notification helper from your Application or Activity class");
        }
    }
    //endregion
}
