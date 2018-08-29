package info.ankurpandya.localnotificaion.demo.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import info.ankurpandya.localnotificaion.demo.fragments.CancelNotificationFragment;
import info.ankurpandya.localnotificaion.demo.fragments.CreateNotificationFragment;
import info.ankurpandya.localnotificaion.demo.utils.NotificationHelper;
import info.ankurpandya.localnotificaion.demo.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        CreateNotificationFragment.OnFragmentInteractionListener,
        CancelNotificationFragment.OnFragmentInteractionListener {

    private View container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);

        NotificationHelper.init(
                MainActivity.class,
                this,
                getString(R.string.app_name),
                R.drawable.app_icon
        );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showCreateNotificationFragment();
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
            showCreateNotificationFragment();
        } else if (id == R.id.nav_gallery) {
            showCancelNotificationFragment();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void showCreateNotificationFragment() {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.content, CreateNotificationFragment.newInstance()).
                commitAllowingStateLoss();
    }

    void showCancelNotificationFragment() {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.content, CancelNotificationFragment.newInstance()).
                commitAllowingStateLoss();
    }

    void showResetNotificationFragment() {

    }

    void deleteNotificationFragment() {

    }

    @Override
    public void createNotification(int id, String content, long delay, boolean repeat) {
        NotificationHelper.createNotification(0, content, delay, repeat);
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
    public void cancelNotification(int notificationId) {
        NotificationHelper.cancelNotification(notificationId);
    }

    public void cancelAllNotifications() {

    }

    public void modifyNotification(int id, String content, long delay, boolean repeat) {

    }

    public void getAllNotifications() {

    }

}
