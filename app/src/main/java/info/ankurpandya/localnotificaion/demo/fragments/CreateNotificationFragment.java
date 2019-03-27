package info.ankurpandya.localnotificaion.demo.fragments;

import android.app.TimePickerDialog;
import android.content.Context;
import android.helper.entities.LocalNotification;
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

    private static final String ARG_NOTIFICATION = "ARG_NOTIFICATION";

    private OnFragmentInteractionListener mListener;

    private long delayHours = 0;
    private long delayMinutes = 0;
    private long delaySeconds = 0;
    //private long delay;

    private long repeatHours = 0;
    private long repeatMinutes = 0;
    private long repeatSeconds = 0;
    //private long delay;

    private EditText edt_id;
    private EditText edt_title;
    private EditText edt_text;
    private CompoundButton toggle_repeat;
    private Button btn_time_picker;
    private Button btn_repeat_time_picker;
    private Button btn_schedule;

    private View section_repeat;

    private LocalNotification localNotification;

    public CreateNotificationFragment() {
        // Required empty public constructor
    }

    public static CreateNotificationFragment newInstance() {
        CreateNotificationFragment fragment = new CreateNotificationFragment();
        return fragment;
    }

    public static CreateNotificationFragment newInstance(LocalNotification localNotification) {
        CreateNotificationFragment fragment = new CreateNotificationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTIFICATION, localNotification);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            localNotification = (LocalNotification) getArguments().getSerializable(ARG_NOTIFICATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_notification, container, false);
        edt_id = rootView.findViewById(R.id.edt_id);
        edt_title = rootView.findViewById(R.id.edt_title);
        edt_text = rootView.findViewById(R.id.edt_text);
        toggle_repeat = rootView.findViewById(R.id.toggle_repeat);
        section_repeat = rootView.findViewById(R.id.section_repeat);
        btn_time_picker = rootView.findViewById(R.id.btn_time_picker);
        btn_repeat_time_picker = rootView.findViewById(R.id.btn_repeat_time_picker);
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

        toggle_repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                section_repeat.setVisibility(isChecked ? View.VISIBLE : View.GONE);
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

        btn_repeat_time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = 0;
                int minute = 0;
                TimePickerDialog dialog = new TimePickerDialog(
                        getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                //repeatHours = hour;
                                //repeatMinutes = minute;
                                repeatHours = hour;
                                repeatMinutes = minute;
                                repeatSeconds = 0;
                                updateRepeatButtonText();
                            }
                        },
                        hour,
                        minute,
                        true //DateFormat.is24HourFormat(getActivity())
                );
                dialog.show();
            }
        });

        if (localNotification != null) {
            edt_id.setText(localNotification.notificationId + "");
//            edt_id.setFocusable(false);
//            edt_id.setFocusableInTouchMode(false);
//            edt_id.setClickable(false);

            edt_title.setText(localNotification.textTitle);
            edt_text.setText(localNotification.textContent);
            toggle_repeat.setChecked(localNotification.repeatDelay > 0);
        } else {
            edt_id.setFocusable(true);
            edt_id.setFocusableInTouchMode(true);
            edt_id.setClickable(true);
            edt_title.setText(R.string.app_name);
        }

        updateDelayButtonText();
    }

    private void createNotification() {
        long triggerDelay = 0;
        triggerDelay += delayHours * 60 * 60 * 1000L;
        triggerDelay += delayMinutes * 60 * 1000L;
        triggerDelay += delaySeconds * 1000L;
        long repeatDelay = 0;

        if (toggle_repeat.isChecked()) {
            repeatDelay += repeatHours * 60 * 60 * 1000L;
            repeatDelay += repeatMinutes * 60 * 1000L;
            repeatDelay += repeatSeconds * 1000L;
        }

        mListener.createNotification(
                Integer.parseInt(edt_id.getText().toString()),
                edt_title.getText().toString(),
                edt_text.getText().toString(),
                triggerDelay,
                repeatDelay
        );

        notifyNotificationCreated();
    }

    private void notifyNotificationCreated() {
        edt_text.setText("");
        mListener.hideKeyboard();
    }

    private void updateDelayButtonText() {
        btn_time_picker.setText(
                millsToTimeStamp(delayHours, delayMinutes, delaySeconds)
        );
    }

    private void updateRepeatButtonText() {
        btn_repeat_time_picker.setText(
                millsToTimeStamp(repeatHours, repeatMinutes, repeatSeconds)
        );
    }

    private boolean isValidData() {
        if (!isValid(edt_id)) {
            edt_id.setError(getString(R.string.msg_error_id_notification));
            edt_id.requestFocus();
            return false;
        } else if (!isValid(edt_title)) {
            edt_title.setError(getString(R.string.msg_error_title_notification));
            edt_title.requestFocus();
            return false;
        } else if (!isValid(edt_text)) {
            edt_text.setError(getString(R.string.msg_error_txt_notification));
            edt_text.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isValid(EditText text) {
        return text.getText() != null && text.getText().toString().trim().length() > 0;
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
        void createNotification(int id, String title, String content, long triggerDelay, long repeatDelay);

        void showToast(String message);

        void hideKeyboard();

        void cancelAllNotifications();
    }
}
