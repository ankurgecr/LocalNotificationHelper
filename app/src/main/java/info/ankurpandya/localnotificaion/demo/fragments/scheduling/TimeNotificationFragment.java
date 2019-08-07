package info.ankurpandya.localnotificaion.demo.fragments.scheduling;

import android.LocalNotificationHelper;
import android.helper.DateTimePickerEditText;
import android.helper.entities.LocalNotification;
import android.view.View;

import info.ankurpandya.localnotificaion.demo.R;
import info.ankurpandya.localnotificaion.demo.utils.Constants;

public class TimeNotificationFragment extends BaseTimeSchedulerFragment {

    private DateTimePickerEditText edt_time;

    public TimeNotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_time_notification;
    }

    @Override
    public void setupViews(View view) {
        edt_time = view.findViewById(R.id.edt_time);
    }

    @Override
    public void resetViews() {
        edt_time.setDate(null);
    }

    @Override
    public LocalNotification onScheduleRequested() {
        return new LocalNotificationHelper.Scheduler(
                Constants.NOTIFICATION_ID_TIME,
                getNotificationContent()
        )
                .setTriggerTime(edt_time.getDate())
                .schedule();
    }
}
