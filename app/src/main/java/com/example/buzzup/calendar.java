package com.example.buzzup;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class calendar extends AppCompatActivity {

    CalendarView calendar;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;
    ArrayList<Event> events;
    ListView eventsListView;
    EventAdapter eventAdapter;
    TextView test_tv;
    Context ct;
    ArrayList<Event> originalEvents;
    ArrayList<String> eventIDS;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        test_tv = findViewById(R.id.test_tv);
        ct = this;

        eventsListView = findViewById(R.id.eventsList);
        eventsListView.setTextFilterEnabled(true);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();
        if(user == null){
            Intent it = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(it);
            finish();
        }
        events = new ArrayList<>();
        originalEvents = new ArrayList<>();
        eventIDS = new ArrayList<>();
        calendar = (CalendarView) findViewById(R.id.cal);
        eventAdapter = new EventAdapter(ct, R.layout.event_row, events, auth, user, db);
        eventsListView.setAdapter(eventAdapter);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override   //i-year, i1-month, i2-day
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
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
                                    //do stuff
                                    Event event = doc.toObject(Event.class);
                                    events.add(event);
                                    originalEvents.add(event);
                                    eventIDS.add(doc.getId());
                                }
                            }
                            eventAdapter.setEventIDS(eventIDS);
                            eventAdapter.notifyDataSetChanged();
                        }
                        else{
                            Toast.makeText(calendar.this, "Error getting data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}