package com.example.buzzup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Date;
import java.util.List;

public class EventsFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;

    SearchView searchBar;
    ListView eventsListView;

    ArrayList<Event> events;
    ArrayList<Event> originalEvents;
    ArrayList<String> eventIDs;
    EventAdapter eventAdapter;

    public EventsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_events, container, false);
    }

    @Override
    public void onViewCreated(View itemView, Bundle savedInstanceState) {
        auth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        if(user == null){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        searchBar = getActivity().findViewById(R.id.events_search_bar);
        searchBar.clearFocus();
        eventsListView = getActivity().findViewById(R.id.events_list);
        eventsListView.setTextFilterEnabled(true);
        searchBar.setSubmitButtonEnabled(true);

        events = new ArrayList<>();
        originalEvents =new ArrayList<>();
        eventIDs = new ArrayList<>();
        eventAdapter = new EventAdapter(getActivity(), R.layout.event_row, events, auth, user, db);
        eventsListView.setAdapter(eventAdapter);

        db.collection("events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    originalEvents.clear();
                    for(QueryDocumentSnapshot document: queryDocumentSnapshots){
//                        Event event = document.toObject(Event.class);

                        Event event = new Event();
                        // set each field manually
                        event.setName((String)document.get("Name"));
                        event.setDescription((String)document.get("Description"));
                        event.setLikes((Long)document.get("Likes"));
                        event.setParticipants((ArrayList<DocumentReference>)document.get("Participants"));
                        event.setTime(((Timestamp)document.get("Time")).toDate());
                        event.setVenue((String)document.get("Venue"));
                        event.setVenueCoordinates((GeoPoint)document.get("VenueCoordinates"));
//                        event.setImageUrls((List<String>)document.get("ImageUrls"));

                        originalEvents.add(event);
                        eventIDs.add(document.getId());
                    }

                    eventAdapter.setEventIDS(eventIDs);
                    events.clear();
                    events.addAll(originalEvents);
                    eventAdapter.notifyDataSetChanged();
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

    }
}
