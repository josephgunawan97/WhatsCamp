package com.projects.whatscamp;

import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.application.GlobalApplication;
import com.config.Config;
import com.facebook.login.widget.ToolTipPopup;
import com.github.xizzhu.simpletooltip.ToolTip;
import com.github.xizzhu.simpletooltip.ToolTipView;
import com.google.android.gms.ads.AdView;
import com.libraries.usersession.UserAccessSession;
import com.libraries.usersession.UserSession;
import com.projects.activities.SearchActivity;
import com.projects.activities.SettingsActivity;
import com.projects.events.MyEventsActivity;
import com.projects.fragments.AboutUsFragment;
import com.projects.fragments.CategoryFragment;
import com.projects.fragments.FavoritesFragment;
import com.projects.fragments.HomeFragment;
import com.projects.fragments.TermsConditionFragment;
import com.projects.users.LoginActivity;
import com.projects.users.ProfileActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Fragment currFragment;
    boolean isLoggedPrev, isLoggedCurrent;
    NavigationView navigationView;
    boolean doubleBackToExitPressedOnce = false;
    public static int offsetY = 0;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (UserAccessSession.getInstance(MainActivity.this).getUserSession() != null) {
                    isLoggedCurrent = true;
                } else {
                    isLoggedCurrent = false;
                }

                if (isLoggedCurrent != isLoggedPrev) {
                    isLoggedPrev = isLoggedCurrent;
                    checkLoginNavigation();
                }
            }
        };


        drawer.setDrawerListener(toggle);
        toggle.syncState();

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            offsetY = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            Config.OFFSET_Y = offsetY;
        }
        showFragment(new HomeFragment());
        showAds();
    }

    public void checkLoginNavigation() {
        navigationView.getMenu().clear();
        UserSession userSession = UserAccessSession.getInstance(this).getUserSession();
        if (userSession != null) {
            navigationView.inflateMenu(R.menu.activity_main_drawer_logged);
        } else {
            navigationView.inflateMenu(R.menu.activity_main_drawer_not_logged);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.tap_back_again_to_exit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
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
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent i;
        int id = item.getItemId();
        if(id == R.id.nav_home) {
            showFragment(new HomeFragment());
            setTitle(R.string.app_name);
        }
        if(id == R.id.nav_categories) {
            showFragment(new CategoryFragment());
            setTitle(R.string.categories);
        }
        if(id == R.id.nav_favorites) {
            showFragment(new FavoritesFragment());
            setTitle(R.string.favorites);
        }
        if(id == R.id.nav_search) {
            i = new Intent(this, SearchActivity.class);
            startActivity(i);
        }

        if(id == R.id.nav_about_us) {
            showFragment(new AboutUsFragment());
            setTitle(R.string.about_us);
        }
        if(id == R.id.nav_terms_condition) {
            showFragment(new TermsConditionFragment());
            setTitle(R.string.terms_and_conditions);
        }
        if(id == R.id.nav_settings) {
            i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        if(id == R.id.nav_login) {
            i = new Intent(this, LoginActivity.class);
            startActivity(i);
        }
        if(id == R.id.nav_profile) {
            i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        }
        if(id == R.id.nav_my_events) {
            i = new Intent(this, MyEventsActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showFragment(Fragment fragment) {
        if (currFragment != null && fragment.getClass().equals(currFragment.getClass()))
            return;

        currFragment = fragment;
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        if (fragmentManager == null)
            return;

        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (ft == null)
            return;

        ft.replace(R.id.content_frame, fragment).commitAllowingStateLoss();
    }

    public void showAds() {

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        GlobalApplication app = (GlobalApplication)getApplication();
        if(app.getMGLocationManager() != null)
            app.getMGLocationManager().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
