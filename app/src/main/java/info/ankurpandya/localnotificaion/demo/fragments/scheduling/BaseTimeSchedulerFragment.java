package info.ankurpandya.localnotificaion.demo.fragments.scheduling;

import android.content.Context;
import android.helper.entities.LocalNotification;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import info.ankurpandya.localnotificaion.demo.R;
import info.ankurpandya.localnotificaion.demo.utils.ToastHelper;

public abstract class BaseTimeSchedulerFragment extends Fragment {

    EditText txt_content;

    ToastHelper toastHelper;

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState) {
        return inflater.inflate(getLayout(), container, false);
    }

    @Override
    public final void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txt_content = view.findViewById(R.id.txt_content);
        view.findViewById(R.id.btn_schedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleScheduleClick();
            }
        });
        setupExpandableView(view, R.id.txt_title, R.id.section_content);
        setupViews(view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ToastHelper) {
            toastHelper = (ToastHelper) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ToastHelper");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        toastHelper = null;
    }

    private void handleScheduleClick() {
        LocalNotification result = onScheduleRequested();
        handleSchedulingResult(result);
    }

    public String getNotificationContent() {
        return txt_content.getText().toString();
    }

    public final void resetAllViews() {
        txt_content.setText("");
        resetViews();
    }

    public abstract int getLayout();

    public abstract void setupViews(View view);

    public abstract void resetViews();

    public abstract LocalNotification onScheduleRequested();

    public static final void setupExpandableView(final View parent, int idHeader, final int idContent) {
        parent.findViewById(idHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View content = parent.findViewById(idContent);
                content.setVisibility(content.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            }
        });
        parent.findViewById(idContent).setVisibility(View.GONE);
    }

    public final void handleSchedulingResult(LocalNotification notification) {
        if (notification != null) {
            toastHelper.showToast(R.string.msg_notification_schedule);
            resetAllViews();
        } else {
            toastHelper.showToast(R.string.msg_notification_schedule_error);
        }
    }

}
