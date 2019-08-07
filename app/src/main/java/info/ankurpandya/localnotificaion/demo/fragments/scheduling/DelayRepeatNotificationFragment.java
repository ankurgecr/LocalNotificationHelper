package info.ankurpandya.localnotificaion.demo.fragments.scheduling;

import android.LocalNotificationHelper;
import android.helper.DateTimePickerEditText;
import android.helper.entities.LocalNotification;
import android.view.View;

import info.ankurpandya.localnotificaion.demo.R;
import info.ankurpandya.localnotificaion.demo.utils.Constants;

public class DelayRepeatNotificationFragment extends BaseTimeSchedulerFragment {

    private DateTimePickerEditText edt_delay;
    private DateTimePickerEditText edt_repeat;

    public DelayRepeatNotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_delay_repeat_notification;
    }

    @Override
    public void setupViews(View view) {
        edt_delay = view.findViewById(R.id.edt_delay);
        edt_repeat = view.findViewById(R.id.edt_repeat);
    }

    @Override
    public void resetViews() {
        edt_delay.setDelay(0);
        edt_repeat.setDelay(0);
    }

    @Override
    public LocalNotification onScheduleRequested() {
        return new LocalNotificationHelper.Scheduler(
                Constants.NOTIFICATION_ID_DELAY_REPEAT,
                getNotificationContent()
        )
                .setTriggerDelay(edt_delay.getDelayMillis())
                .setRepeatDelay(edt_repeat.getDelayMillis())
                .schedule();
    }
}
