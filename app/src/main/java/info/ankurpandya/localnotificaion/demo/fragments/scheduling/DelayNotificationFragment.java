package info.ankurpandya.localnotificaion.demo.fragments.scheduling;

import android.LocalNotificationHelper;
import android.helper.DateTimePickerEditText;
import android.helper.entities.LocalNotification;
import android.view.View;

import info.ankurpandya.localnotificaion.demo.R;
import info.ankurpandya.localnotificaion.demo.utils.Constants;

public class DelayNotificationFragment extends BaseTimeSchedulerFragment {

    private DateTimePickerEditText edt_delay;

    public DelayNotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_delay_notification;
    }

    @Override
    public void setupViews(View view) {
        edt_delay = view.findViewById(R.id.edt_delay);
    }

    @Override
    public void resetViews() {
        edt_delay.setDelay(0);
    }

    @Override
    public LocalNotification onScheduleRequested() {
        return new LocalNotificationHelper.Scheduler(
                Constants.NOTIFICATION_ID_DELAY,
                getNotificationContent()
        )
                .setTriggerDelay(edt_delay.getDelayMillis())
                .schedule();
    }
}
