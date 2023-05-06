package com.example.buzzup;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class CalendarFragment extends Fragment {
    CalendarView calendar;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    ArrayList<Event> events;
    ListView eventsListView;
    EventAdapter eventAdapter;
    Context ct;
    ArrayList<Event> originalEvents;
    ArrayList<String> eventIDS;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ct = getActivity();

        eventsListView = view.findViewById(R.id.eventsList);
        eventsListView.setTextFilterEnabled(true);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        if(user == null){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
        events = new ArrayList<>();
        originalEvents = new ArrayList<>();
        eventIDS = new ArrayList<>();
        eventAdapter = new EventAdapter(ct, R.layout.event_row, events, auth, user, db);
        eventsListView.setAdapter(eventAdapter);
        calendar = (CalendarView) view.findViewById(R.id.cal);


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override   //i-year, i1-month, i2-day
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                originalEvents.clear();
                i1 += 1;
                String i_st = String.valueOf(i);
                String i1_st = String.valueOf(i1);
                String i2_st = String.valueOf(i2);
                if(i < 10){
                    i_st = "0"+i_st;
                }
                if(i1 < 10){
                    i1_st = "0"+i1_st;
                }
                if(i2 < 10){
                    i2_st = "0"+i2_st;
                }
                String selected_date = i2_st+"/"+i1_st+"/"+i_st;
                db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc: task.getResult()){
                                Timestamp ts =(Timestamp) doc.get("Time");
                                Date dt = new Date(ts.getSeconds() *1000);
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                String formatted_date = formatter.format(dt);
                                if(selected_date.equals(formatted_date)){
                                    // do stuff
                                    Event event = new Event();
                                    // set each field manually
                                    event.setName((String)doc.get("Name"));
                                    event.setDescription((String)doc.get("Description"));
                                    event.setLikes((Long)doc.get("Likes"));
                                    event.setParticipants((ArrayList<DocumentReference>)doc.get("Participants"));
                                    event.setTime(((Timestamp)doc.get("Time")).toDate());
                                    event.setVenue((String)doc.get("Venue"));
                                    event.setVenueCoordinates((GeoPoint)doc.get("VenueCoordinates"));

                                    originalEvents.add(event);
                                    eventIDS.add(doc.getId());
                                }
                            }
                            eventAdapter.setEventIDS(eventIDS);
                            events.clear();
                            events.addAll(originalEvents);
                            eventAdapter.notifyDataSetChanged();
                        }
                        else{
                            Toast.makeText(getActivity(), "Error getting data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}