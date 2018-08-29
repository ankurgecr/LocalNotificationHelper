package info.ankurpandya.localnotificaion.demo.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TimePicker;

import info.ankurpandya.localnotificaion.demo.R;

public class CreateNotificationFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private long delayHours = 0;
    private long delayMinutes = 0;
    private long delaySeconds = 0;
    //private long delay;

    private EditText edt_text;
    private CompoundButton toggle_repeat;
    private Button btn_time_picker;
    private Button btn_schedule;

    public CreateNotificationFragment() {
        // Required empty public constructor
    }

    public static CreateNotificationFragment newInstance() {
        CreateNotificationFragment fragment = new CreateNotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_notification, container, false);
        edt_text = rootView.findViewById(R.id.edt_text);
        toggle_repeat = rootView.findViewById(R.id.toggle_repeat);
        btn_time_picker = rootView.findViewById(R.id.btn_time_picker);
        btn_schedule = rootView.findViewById(R.id.btn_schedule);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidData()) {
                    createNotification();
                }
            }
        });

        btn_time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = 0;
                int minute = 0;
                new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                //delayHours = hour;
                                //delayMinutes = minute;
                                delayMinutes = hour;
                                delaySeconds = minute;
                                updateDelayButtonText();
                            }
                        },
                        hour,
                        minute,
                        true //DateFormat.is24HourFormat(getActivity())
                ).show();
            }
        });

        updateDelayButtonText();
    }

    private void createNotification() {
        long delay = 0;
        delay += delayHours * 60 * 60 * 1000L;
        delay += delayMinutes * 60 * 1000L;
        delay += delaySeconds * 1000L;

        mListener.createNotification(
                0,
                edt_text.getText().toString(),
                delay,
                toggle_repeat.isChecked()
        );

        notifyNotificationCreated();
    }

    private void notifyNotificationCreated() {
        edt_text.setText("");
        mListener.hideKeyboard();
        mListener.showToast(getString(R.string.msg_notification_schedule));
    }

    private void updateDelayButtonText() {
        btn_time_picker.setText(
                millsToTimeStamp(delayHours, delayMinutes, delaySeconds)
        );
    }

    private boolean isValidData() {
        if (
                edt_text.getText() == null || edt_text.getText().toString().trim().length() == 0
                ) {
            edt_text.setError(getString(R.string.msg_error_txt_notification));
            edt_text.requestFocus();
            return false;
        }
        return true;
    }

    private static String millsToTimeStamp(long delayHours, long delayMinutes, long delaySeconds) {
        String text = "";
        if (delayHours > 0) {
            text += delayHours + " h ";
        }
        if (delayMinutes > 0) {
            text += delayMinutes + " mins ";
        }
        if (delaySeconds > 0) {
            text += delaySeconds + " sec ";
        }

        text = text.trim();

        if (text.length() == 0) {
            text = "None";
        }

        return text;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void createNotification(int id, String content, long delay, boolean repeat);

        void showToast(String message);

        void hideKeyboard();
    }
}
