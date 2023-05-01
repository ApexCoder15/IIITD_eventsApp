package com.example.buzzup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

// TODO add logoout button and user profile button somewhere
public class BaseActivity extends AppCompatActivity implements BottomNavigationView
        .OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    MapFragment mapFragment = new MapFragment();
    EventsFragment eventsFragment = new EventsFragment();
    FeedFragment feedFragment = new FeedFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, homeFragment)
                        .commit();
                return true;
            case R.id.navigation_map:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, mapFragment)
                        .commit();
                return true;
            case R.id.navigation_events:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, eventsFragment)
                        .commit();
                return true;
            case R.id.navigation_feed:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, feedFragment)
                        .commit();
                return true;
            case R.id.navigation_profile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, profileFragment)
                        .commit();
                return true;
        }
        return false;
    }
}