package info.ankurpandya.localnotificaion.demo.fragments.scheduling;

import android.LocalNotificationHelper;
import android.helper.entities.LocalNotification;
import android.view.View;

import info.ankurpandya.localnotificaion.demo.R;
import info.ankurpandya.localnotificaion.demo.utils.Constants;

public class QuickNotificationFragment extends BaseTimeSchedulerFragment {

    public QuickNotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_quick_notification;
    }

    @Override
    public void setupViews(View view) {
        //Nothing
    }

    @Override
    public void resetViews() {

    }

    @Override
    public LocalNotification onScheduleRequested() {
        return new LocalNotificationHelper.Scheduler(
                Constants.NOTIFICATION_ID_QUICK,
                getNotificationContent()
        ).schedule();
    }
}
