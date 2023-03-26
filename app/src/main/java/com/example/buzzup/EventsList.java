package com.example.buzzup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

import javax.annotation.Nullable;

public class EventsList extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    SearchView searchBarEvents;
    ListView eventsListView;
    ArrayList<Event> events;
    ArrayList<Event> originalEvents;
    EventAdapter eventAdapter;
    Button logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_list);

        auth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        if(user == null){
            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        searchBarEvents = findViewById(R.id.eventsSearchBar);
        searchBarEvents.clearFocus();
        eventsListView = findViewById(R.id.eventsList);
        eventsListView.setTextFilterEnabled(true);
        searchBarEvents.setSubmitButtonEnabled(true);
        logoutButton = findViewById(R.id.eventsPageLogoutButton);

        Log.d("is_Admin", user.getEmail());

        events = new ArrayList<>();
        originalEvents =new ArrayList<>();
        eventAdapter = new EventAdapter(this, R.layout.event_row, events);
        eventsListView.setAdapter(eventAdapter);

        db.collection("events")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        originalEvents.clear();
                        for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                            Event event = document.toObject(Event.class);
                            originalEvents.add(event);
                        }
                        events.clear();
                        events.addAll(originalEvents);
                        eventAdapter.notifyDataSetChanged();
                    }
                });

        // Listen for changes to the event collection
        db.collection("events").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("EventsList", "Listen failed.", e);
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
            }
        });

        searchBarEvents.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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