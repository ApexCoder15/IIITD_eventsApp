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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class EventsList extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    SearchView searchBar;
    ListView eventsListView;
    ArrayList<Event> events;
    ArrayList<Event> originalEvents;
    EventAdapter eventAdapter;
    Button logoutButton;
    Button createEventButton;

    final String TAG = "EventsList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        auth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        if(user == null){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        searchBar = findViewById(R.id.eventsSearchBar);
        searchBar.clearFocus();
        eventsListView = findViewById(R.id.eventsList);
        eventsListView.setTextFilterEnabled(true);
        searchBar.setSubmitButtonEnabled(true);

        createEventButton = findViewById(R.id.createEventButton);
        logoutButton = findViewById(R.id.eventsPageLogoutButton);

        events = new ArrayList<>();
        originalEvents =new ArrayList<>();
        eventAdapter = new EventAdapter(this, R.layout.event_row, events, auth, user, db);
        eventsListView.setAdapter(eventAdapter);

        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    originalEvents.clear();
                    ArrayList<String> eventIDS = new ArrayList<>();
                    for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                        Event event = document.toObject(Event.class);
                        originalEvents.add(event);
                        eventIDS.add(document.getId());
                    }
                    eventAdapter.setEventIDS(eventIDS);
                    events.clear();
                    events.addAll(originalEvents);
                    eventAdapter.notifyDataSetChanged();
                });


        // Listen for changes to the event collection
        db.collection("events").addSnapshotListener((querySnapshot, e) -> {
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
                eventAdapter.notifyDataSetChanged();
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
                eventAdapter.getFilter().filter(newText);
                eventAdapter.setOriginalEvents(originalEvents);
                return true;
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}