package com.example.buzzup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AdminEventsList extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    SearchView searchBarEvents;
    ListView eventsListView;
    ArrayList<Event> events;
    ArrayList<Event> originalEvents;
    AdminEventAdapter adminEventAdapter;
    Button logoutButton;
    Button createEventButton;
    SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_events_list);

        auth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        if(user == null){
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        searchBarEvents = findViewById(R.id.eventsSearchBarAdmin);
        searchBarEvents.clearFocus();
        eventsListView = findViewById(R.id.eventsListAdmin);
        eventsListView.setTextFilterEnabled(true);
        searchBarEvents.setSubmitButtonEnabled(true);
        createEventButton = findViewById(R.id.createEventButtonAdmin);
        createEventButton.setVisibility(View.VISIBLE);
        logoutButton = findViewById(R.id.eventsPageLogoutButtonAdmin);

        Log.d("is_Admin", user.getEmail());

        events = new ArrayList<>();
        originalEvents =new ArrayList<>();
        adminEventAdapter = new AdminEventAdapter(this, R.layout.admin_event_row, events);
        eventsListView.setAdapter(adminEventAdapter);

        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    originalEvents.clear();
                    for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                        Event event = document.toObject(Event.class);
                        originalEvents.add(event);
                    }
                    events.clear();
                    events.addAll(originalEvents);
                    adminEventAdapter.notifyDataSetChanged();
                });


        pullToRefresh = findViewById(R.id.pullToRefreshAdminList);

        pullToRefresh= findViewById(R.id.pullToRefreshAdminList);
        pullToRefresh.setOnRefreshListener(() -> {
            refreshData(); // your code
            pullToRefresh.setRefreshing(false);
        });

        searchBarEvents.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adminEventAdapter.getFilter().filter(newText);
                adminEventAdapter.setOriginalEvents(originalEvents);
                return true;
            }
        });

        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });

        createEventButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AdminCreateNewEventActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    void refreshData() {
        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    originalEvents.clear();
                    for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                        Event event = document.toObject(Event.class);
                        originalEvents.add(event);
                    }
                    events.clear();
                    events.addAll(originalEvents);
                    adminEventAdapter.notifyDataSetChanged();
                });
    }
}