package info.ankurpandya.localnotificaion.demo.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import info.ankurpandya.localnotificaion.demo.R;

public class CancelNotificationFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private EditText edt_id;
    private Button btn_cancel;
    private View btn_cancel_all;

    public CancelNotificationFragment() {
        // Required empty public constructor
    }

    public static CancelNotificationFragment newInstance() {
        CancelNotificationFragment fragment = new CancelNotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cancel_notification, container, false);
        edt_id = rootView.findViewById(R.id.edt_id);
        btn_cancel = rootView.findViewById(R.id.btn_cancel);
        btn_cancel_all = rootView.findViewById(R.id.btn_cancel_all);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidData()) {
                    cancelNotification();
                }
            }
        });
        btn_cancel_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAllNotifications();
            }
        });
    }

    private void cancelAllNotifications() {
        mListener.cancelAllNotifications();
    }

    private void cancelNotification() {
        String strId = edt_id.getText().toString();
        int notificationId = Integer.parseInt(strId);
        mListener.cancelNotification(
                notificationId
        );
        notifyNotificationCancelled();
    }

    private void notifyNotificationCancelled() {
        edt_id.setText("");
        mListener.hideKeyboard();
    }

    private boolean isValidData() {
        if (
                edt_id.getText() == null || edt_id.getText().toString().trim().length() == 0
                ) {
            edt_id.setError(getString(R.string.msg_error_txt_id));
            edt_id.requestFocus();
            return false;
        }
        return true;
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
        void cancelNotification(int notificationId);

        boolean isScheduled(int notificationId);

        void showToast(String message);

        void hideKeyboard();

        void cancelAllNotifications();
    }
}
