package info.ankurpandya.localnotificaion.demo.fragments;

import android.content.Context;
import android.helper.entities.LocalNotification;
import android.helper.entities.LocalNotificationHandler;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import info.ankurpandya.localnotificaion.demo.R;
import info.ankurpandya.localnotificaion.demo.activities.MainActivity;
import info.ankurpandya.localnotificaion.demo.adapters.NotificationListAdapter;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class NotificationListFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private List<LocalNotification> allNotifications;
    private View btn_add_new;
    private SwipeRefreshLayout listContainer;
    private RecyclerView list;
    private View empty;

    private NotificationListAdapter adapter;

    public NotificationListFragment() {
    }

    public static NotificationListFragment newInstance() {
        NotificationListFragment fragment = new NotificationListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        allNotifications = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mynotification_list, container, false);
        listContainer = view.findViewById(R.id.listContainer);
        list = view.findViewById(R.id.list);
        btn_add_new = view.findViewById(R.id.btn_add_new);
        empty = view.findViewById(R.id.empty);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new NotificationListAdapter(allNotifications, mListener);
        list.setAdapter(adapter);

        btn_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onCreateNewNotificationRequested();
            }
        });

        listContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void refreshList() {
        showProgress();
        allNotifications.clear();
        mListener.getAllNotifications(new LocalNotificationHandler() {
            @Override
            public void onNotificationReceived(List<LocalNotification> notifications) {
                allNotifications.addAll(notifications);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                updateEmptyView();
                hideProgress();
            }
        });
    }

    private void updateEmptyView() {
        if (allNotifications == null || allNotifications.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.GONE);
        }
    }

    private void showProgress() {
        if (listContainer != null) {
            listContainer.setRefreshing(true);
        }
    }

    private void hideProgress() {
        if (listContainer != null) {
            listContainer.setRefreshing(false);
        }
    }

    public interface OnListFragmentInteractionListener {
        void getAllNotifications(LocalNotificationHandler callback);

        void onCreateNewNotificationRequested();

        void onModifyNotificationRequested(LocalNotification notification);

        void onCancelNotificationRequested(LocalNotification notification, MainActivity.UpdateTaskHandler handler);
    }
}
