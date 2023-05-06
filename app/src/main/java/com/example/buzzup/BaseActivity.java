package com.example.buzzup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity implements BottomNavigationView
        .OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;


    HomeFragment homeFragment = new HomeFragment();
    MapFragment mapFragment = new MapFragment();
    EventsFragment eventsFragment = new EventsFragment();
    FeedFragment feedFragment = new FeedFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    CalendarFragment calendarFragment = new CalendarFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            int intentFragment = intent.getIntExtra("frgToLoad", FragmentConstants.DEFAULT_FRAGMENT);
            switch (intentFragment) {
                case FragmentConstants.MAP_FRAGMENT:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, mapFragment).commit();
                    break;
                default:
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                    break;
            }
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.navigation_profile:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, profileFragment)
                        .commit();
                return true;
        }
        return false;
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
            case R.id.cale:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, calendarFragment)
                        .commit();
                return true;
        }
        return false;
    }
}