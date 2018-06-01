package com.getomnify.hackernews.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.getomnify.hackernews.R;
import com.getomnify.hackernews.fragments.LoginFragment;
import com.getomnify.hackernews.fragments.StoriesFragment;
import com.getomnify.hackernews.helpers.GlideApp;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "MainActivity";

    public FragmentManager fragmentManager;

    public GoogleSignInAccount currentAccount;

    public Toolbar actionBar;
    public DrawerLayout drawer;
    public NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);


        fragmentManager = getSupportFragmentManager();


        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.activity_main_nav_view);

        preLoginSetup();

        //Checking login
        currentAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (currentAccount != null) {
            Log.i(TAG, "Signed in");
            postLoginSetup();
        } else {
            Log.w(TAG, "No account found, trying to sign in...");
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            replaceFragment(new LoginFragment(), LoginFragment.TAG);
        }
    }

    private void preLoginSetup()
    {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, actionBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    public void postLoginSetup()
    {
        currentAccount = GoogleSignIn.getLastSignedInAccount(this);

        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeader = navigationView.getHeaderView(0);

        ImageView dp = navHeader.findViewById(R.id.drawer_header_image);
        TextView name = navHeader.findViewById(R.id.drawer_header_name);
        TextView email = navHeader.findViewById(R.id.drawer_header_email);

        GlideApp.with(this)
                .load(currentAccount.getPhotoUrl())
                .circleCrop()
                .into(dp);

        name.setText(currentAccount.getDisplayName());
        email.setText(currentAccount.getEmail());

        replaceFragment(new StoriesFragment(), StoriesFragment.TAG);
    }


    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return id == R.id.action_settings || super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_stories:

                replaceFragment(new StoriesFragment(), StoriesFragment.TAG);
                break;


            case R.id.nav_logout:

                PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
                finish();

                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void replaceFragment(@NonNull Fragment newFragment, @NonNull String newFragmentTAG)
    {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry lastFragment = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1);

            //If last fragment in backStack is not same as newFragment
            if (!newFragmentTAG.equals(lastFragment.getName())) {
                FragmentTransaction fragmentTransaction;
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.replace(R.id.activity_main_fragment_container, newFragment);

                //If it is StoriesFragment or LoginFragment, remove all backStack except last one
                if (newFragmentTAG.equals(StoriesFragment.TAG) || newFragmentTAG.equals(LoginFragment.TAG)) {
                    for (int i = 0; i < fragmentManager.getBackStackEntryCount() - 1; i++)
                        fragmentManager.popBackStack();
                    if (lastFragment.getName().equals(StoriesFragment.TAG) && newFragmentTAG.equals(LoginFragment.TAG))
                        fragmentManager.popBackStack();
                } else
                    fragmentTransaction.addToBackStack(newFragmentTAG);
                fragmentTransaction.commit();
            }
        } else {

            if (GoogleSignIn.getLastSignedInAccount(this) == null)
                fragmentManager.beginTransaction()
                        .replace(R.id.activity_main_fragment_container, new LoginFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            else
                fragmentManager.beginTransaction()
                        .replace(R.id.activity_main_fragment_container, new StoriesFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(StoriesFragment.TAG)
                        .commit();

        }
    }
}
