package info.ankurpandya.localnotificaion.demo.fragments;

import android.helper.entities.LocalNotification;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import info.ankurpandya.localnotificaion.demo.R;
import info.ankurpandya.localnotificaion.demo.activities.MainActivity;
import info.ankurpandya.localnotificaion.demo.fragments.NotificationListFragment.OnListFragmentInteractionListener;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {

    private final List<LocalNotification> mValues;
    private final OnListFragmentInteractionListener mListener;

    public NotificationListAdapter(List<LocalNotification> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mynotification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        public final TextView txt_id;
        public final TextView txt_title;
        public final TextView txt_time;
        public final TextView txt_content;
        public final View img_repeat;
        public final View btn_change;
        public final View btn_cancel;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            txt_id = (TextView) view.findViewById(R.id.txt_id);
            txt_title = (TextView) view.findViewById(R.id.txt_title);
            txt_time = (TextView) view.findViewById(R.id.txt_time);
            txt_content = (TextView) view.findViewById(R.id.txt_content);
            img_repeat = view.findViewById(R.id.img_repeat);
            btn_change = view.findViewById(R.id.btn_change);
            btn_cancel = view.findViewById(R.id.btn_cancel);
        }

        public void bind(final LocalNotification notification) {
            txt_id.setText(notification.notificationId + "");
            txt_title.setText(notification.textTitle);
            txt_content.setText(notification.textContent);
            txt_time.setText(formatNotificationDate(notification.triggerTime));
            img_repeat.setVisibility(notification.isRepeat ? View.VISIBLE : View.INVISIBLE);
            btn_change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onModifyNotificationRequested(notification);
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onCancelNotificationRequested(notification, new MainActivity.UpdateTaskHandler() {
                        @Override
                        public void onItemUpdated() {
                            int index = mValues.indexOf(notification);
                            if (index >= 0) {
                                mValues.remove(index);
                                notifyItemRemoved(index);
                            }
                        }
                    });
                }
            });
        }
    }

    private static String formatNotificationDate(long timeMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        SimpleDateFormat format = new SimpleDateFormat("hh:MM:ss");
        return format.format(calendar.getTime());
    }
}
