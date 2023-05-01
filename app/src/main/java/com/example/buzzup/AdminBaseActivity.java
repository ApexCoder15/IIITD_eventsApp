package com.example.buzzup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminBaseActivity extends AppCompatActivity implements BottomNavigationView
        .OnNavigationItemSelectedListener {

    BottomNavigationView bottomNavigationView;

    AdminEventsFragment eventsFragment = new AdminEventsFragment();
    AdminFeedFragment feedFragment = new AdminFeedFragment();
    AdminProfileFragment profileFragment = new AdminProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_base);


        bottomNavigationView = findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.admin_container, eventsFragment).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.admin_navigation_events:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.admin_container, eventsFragment)
                        .commit();
                return true;
            case R.id.admin_navigation_feed:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.admin_container, feedFragment)
                        .commit();
                return true;
            case R.id.admin_navigation_profile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.admin_container, profileFragment)
                        .commit();
                return true;
        }
        return false;
    }
}