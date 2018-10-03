package info.ankurpandya.localnotificaion.demo.activities;

import android.LocalNotificationHelper;
import android.app.Activity;
import android.content.DialogInterface;
import android.helper.entities.LocalNotification;
import android.helper.entities.LocalNotificationHandler;
import android.helper.entities.LocalNotificationStatusHandler;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.List;

import info.ankurpandya.localnotificaion.demo.R;
import info.ankurpandya.localnotificaion.demo.fragments.CancelNotificationFragment;
import info.ankurpandya.localnotificaion.demo.fragments.CreateNotificationFragment;
import info.ankurpandya.localnotificaion.demo.fragments.NotificationListFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CreateNotificationFragment.OnFragmentInteractionListener,
        CancelNotificationFragment.OnFragmentInteractionListener,
        NotificationListFragment.OnListFragmentInteractionListener {

    private View container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);

        LocalNotificationHelper.init(
                getString(R.string.app_name),
                R.drawable.app_icon
        );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        showNotificationListFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            showNotificationListFragment();
        } else if (id == R.id.nav_gallery) {
            showCreateNotificationFragment();
        } else if (id == R.id.nav_slideshow) {
            showCancelNotificationFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void showNotificationListFragment() {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.content, NotificationListFragment.newInstance()).
                commitAllowingStateLoss();
    }

    void showCreateNotificationFragment() {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.content, CreateNotificationFragment.newInstance()).
                addToBackStack(CreateNotificationFragment.class.getName()).
                commitAllowingStateLoss();
    }

    void showCreateNotificationFragment(LocalNotification notification) {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.content, CreateNotificationFragment.newInstance(notification)).
                addToBackStack(CreateNotificationFragment.class.getName()).
                commitAllowingStateLoss();
    }

    void showCancelNotificationFragment() {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.content, CancelNotificationFragment.newInstance()).
                addToBackStack(CancelNotificationFragment.class.getName()).
                commitAllowingStateLoss();
    }

    void showResetNotificationFragment() {

    }

    void deleteNotificationFragment() {

    }

    @Override
    public void createNotification(final int id, final String title, final String content, final long delay, final boolean repeat) {
        LocalNotificationHelper.isScheduled(id, new LocalNotificationStatusHandler() {
            @Override
            public void onNotificationStatusReceived(boolean scheduled) {
                if (scheduled) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    createNotificationWithConfirmation(id, title, content, delay, repeat);
                                    break;
                            }
                        }
                    };
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.confirm_override_notification_title)
                            .setMessage(R.string.confirm_override_notification_desc)
                            .setPositiveButton(android.R.string.yes, dialogClickListener)
                            .setNegativeButton(android.R.string.no, dialogClickListener)
                            .show();
                } else {
                    createNotificationWithConfirmation(id, title, content, delay, repeat);
                }
            }
        });
    }

    private void createNotificationWithConfirmation(int id, String title, String content, long delay, boolean repeat) {
        //LocalNotificationHelper.schedule(id, content, delay, repeat);
        if (title == null) {
            title = getString(R.string.app_name);
        }
        LocalNotificationHelper.schedule(
                id,
                "",
                R.drawable.ic_stat,
                R.drawable.app_icon,
                title,
                content,
                delay,
                repeat
        );
        showToast(getString(R.string.msg_notification_schedule));
        showNotificationListFragment();
        //refreshCurrentFragment();
    }

    @Override
    public void showToast(String message) {
        Snackbar.make(
                container,
                message,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(container.getWindowToken(), 0);
    }

    @Override
    public void cancelNotification(final int notificationId) {
        LocalNotificationHelper.isScheduled(notificationId, new LocalNotificationStatusHandler() {
            @Override
            public void onNotificationStatusReceived(boolean scheduled) {
                if (scheduled) {
                    LocalNotificationHelper.cancel(notificationId);
                    showToast(getString(R.string.msg_notification_cancelled));
                } else {
                    showToast(getString(R.string.msg_notification_not_scheduled_id));
                }
            }
        });
    }

    public void refreshCurrentFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content);
        if (fragment != null) {
            if (fragment instanceof NotificationListFragment) {
                ((NotificationListFragment) fragment).refreshList();
            }
        }
    }

    @Override
    public void isScheduled(int notificationId, LocalNotificationStatusHandler callback) {
        LocalNotificationHelper.isScheduled(notificationId, callback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //LocalNotificationHelper.destroy();
    }

    @Override
    public void cancelAllNotifications() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        cancelAllNotificationWithConfirmation();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_all_title)
                .setMessage(R.string.confirm_delete_all_desc)
                .setPositiveButton(android.R.string.yes, dialogClickListener)
                .setNegativeButton(android.R.string.no, dialogClickListener)
                .show();
    }

    private void cancelAllNotificationWithConfirmation() {
        LocalNotificationHelper.cancelAll();
        showToast(getString(R.string.msg_all_notification_cancelled));
    }

    private void cancelNotificationWithConfirmation(LocalNotification notification, UpdateTaskHandler handler) {
        LocalNotificationHelper.cancel(notification);
        showToast(getString(R.string.msg_notification_cancelled));
        if (handler != null) {
            handler.onItemUpdated();
        }
    }

    @Override
    public void getAllNotifications(final LocalNotificationHandler callback) {
        //LocalNotificationHelper.getAll(callback);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<LocalNotification> notificationList = LocalNotificationHelper.getAllSync();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onNotificationReceived(notificationList);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onModifyNotificationRequested(LocalNotification notification) {
        showCreateNotificationFragment(notification);
    }

    @Override
    public void onCreateNewNotificationRequested() {
        showCreateNotificationFragment();
    }

    @Override
    public void onCancelNotificationRequested(final LocalNotification notification, final UpdateTaskHandler handler) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        cancelNotificationWithConfirmation(notification, handler);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_cancel_notification_title)
                .setMessage(R.string.confirm_delete_all_desc)
                .setPositiveButton(android.R.string.yes, dialogClickListener)
                .setNegativeButton(android.R.string.no, dialogClickListener)
                .show();
    }

    public interface UpdateTaskHandler {
        void onItemUpdated();
    }
}
