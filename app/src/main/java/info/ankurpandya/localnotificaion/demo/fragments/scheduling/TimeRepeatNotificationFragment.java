package info.ankurpandya.localnotificaion.demo.fragments.scheduling;

import android.LocalNotificationHelper;
import android.helper.DateTimePickerEditText;
import android.helper.entities.LocalNotification;
import android.view.View;

import info.ankurpandya.localnotificaion.demo.R;
import info.ankurpandya.localnotificaion.demo.utils.Constants;

public class TimeRepeatNotificationFragment extends BaseTimeSchedulerFragment {

    private DateTimePickerEditText edt_time;
    private DateTimePickerEditText edt_repeat;

    public TimeRepeatNotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_time_repeat_notification;
    }

    @Override
    public void setupViews(View view) {
        edt_time = view.findViewById(R.id.edt_time);
        edt_repeat = view.findViewById(R.id.edt_repeat);
    }

    @Override
    public void resetViews() {
        edt_time.setDate(null);
        edt_repeat.setDelay(0);
    }

    @Override
    public LocalNotification onScheduleRequested() {
        return new LocalNotificationHelper.Scheduler(
                Constants.NOTIFICATION_ID_TIME_REPEAT,
                getNotificationContent()
        )
                .setTriggerTime(edt_time.getTime())
                .setRepeatDelay(edt_repeat.getDelayMillis())
                .schedule();
    }
}
