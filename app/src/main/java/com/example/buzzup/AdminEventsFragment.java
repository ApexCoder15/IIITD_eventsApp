package com.example.buzzup;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminEventsFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    SearchView searchBar;
    ListView eventsListView;
    ArrayList<Event> events;
    ArrayList<Event> originalEvents;
    AdminEventAdapter adminEventAdapter;
    Button createEventButton;


    public AdminEventsFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_events, container, false);
    }

    @Override
    public void onViewCreated(View itemView, Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        if(user == null){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        searchBar = getActivity().findViewById(R.id.admin_events_search_bar);
        searchBar.clearFocus();
        eventsListView = getActivity().findViewById(R.id.admin_events_list);
        eventsListView.setTextFilterEnabled(true);
        searchBar.setSubmitButtonEnabled(true);
        createEventButton = getActivity().findViewById(R.id.btn_admin_create_event);

        events = new ArrayList<>();
        originalEvents = new ArrayList<>();
        adminEventAdapter = new AdminEventAdapter(getActivity(), R.layout.admin_event_row, events);
        eventsListView.setAdapter(adminEventAdapter);

        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    originalEvents.clear();
                    for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                        Event event = new Event();
                        // set each field manually
                        event.setName((String)document.get("Name"));
                        event.setDescription((String)document.get("Description"));
                        event.setLikes((Long)document.get("Likes"));
                        event.setParticipants((ArrayList<DocumentReference>)document.get("Participants"));
                        event.setTime(((Timestamp)document.get("Time")).toDate());
                        event.setVenue((String)document.get("Venue"));
                        event.setVenueCoordinates((GeoPoint)document.get("VenueCoordinates"));
                        event.setImageUrls((List<String>)document.get("ImageUrls"));

                        // TODO add filter that event created by ME
                        originalEvents.add(event);
                    }

                    events.clear();
                    events.addAll(originalEvents);
                    adminEventAdapter.notifyDataSetChanged();
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

        createEventButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AdminCreateNewEventActivity.class);
            startActivity(intent);
        });
    }
}
