package com.example.buzzup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class AdminEventsList extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    SearchView searchBar;
    ListView eventsListView;
    ArrayList<Event> events;
    ArrayList<Event> originalEvents;
    AdminEventAdapter adminEventAdapter;
    Button logoutButton;
    Button createEventButton;

    final String TAG = "AdminEventsList";

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
        }

        searchBar = findViewById(R.id.eventsSearchBarAdmin);
        searchBar.clearFocus();
        eventsListView = findViewById(R.id.eventsListAdmin);
        eventsListView.setTextFilterEnabled(true);
        searchBar.setSubmitButtonEnabled(true);
        createEventButton = findViewById(R.id.createEventButtonAdmin);
        createEventButton.setVisibility(View.VISIBLE);
        logoutButton = findViewById(R.id.eventsPageLogoutButtonAdmin);

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

        // Listen for changes to the event collection
        db.collection("events").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                originalEvents.clear();
                for (QueryDocumentSnapshot document : querySnapshot) {
                    Event event = document.toObject(Event.class);
                    originalEvents.add(event);
                    events.clear();
                    events.addAll(originalEvents);
                    adminEventAdapter.notifyDataSetChanged();
                }
            }
        });

        // search bar functionality
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
}