package com.example.buzzup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class EventsList extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    EditText searchBarEvents;
    boolean userIsApproved = false;
    boolean userIsAdmin = false;
    ListView eventsListView;
    ArrayList<Event> events;
    EventAdapter eventAdapter;
    Button logoutButton;
    Button createEventButton;
    Button likeButton;

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
        eventsListView = findViewById(R.id.eventsList);
        createEventButton = findViewById(R.id.createEventButton);
        logoutButton = findViewById(R.id.eventsPageLogoutButton);

        Log.d("is_Admin", user.getEmail());

        db.collection("user").document(user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d("is_Admin", "is_admin");
                        if (task.isSuccessful()) {
                            Log.d("is_Admin", "is_admin");
                            DocumentSnapshot document = task.getResult();
                            Log.d("is_Admin", document.toString());
                            if (document.exists()) {
                                Log.d("is_Admin", document.getData().get("is_admin") + " " );
                                Log.d("is_Admin", document.getData().get("is_approved") + " " );

                                userIsAdmin = Boolean.valueOf(document.getData().get("is_admin").toString());
                                userIsApproved = Boolean.valueOf(document.getData().get("is_approved").toString());
                            }

                            if (userIsAdmin && userIsApproved){
                                createEventButton.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

        eventAdapter = new EventAdapter(this, R.layout.event_row, new ArrayList<Event>());
        eventsListView.setAdapter(eventAdapter);

        db.collection("events")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        events = new ArrayList<Event>();
                        for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                            Event event = document.toObject(Event.class);
                            events.add(event);
                        }

                        eventAdapter.clear();
                        eventAdapter.addAll(events);
//                        eventAdapter.addAllAgain(events);
                    }
                });

//        searchBarEvents.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                (EventsList.this).eventAdapter.getFilter().filter(charSequence);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

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