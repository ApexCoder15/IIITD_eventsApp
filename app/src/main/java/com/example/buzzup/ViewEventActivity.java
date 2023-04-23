package com.example.buzzup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public class ViewEventActivity extends AppCompatActivity
{
    FirebaseFirestore db;
    final String TAG = "ViewEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        int index = Integer.parseInt(intent.getStringExtra("index"));

        TextView eventName = (TextView)findViewById(R.id.event_name);
        TextView eventDescription = (TextView)findViewById(R.id.event_description);
        TextView eventVenue = (TextView)findViewById(R.id.event_venue);
        TextView eventDateTime = (TextView)findViewById(R.id.event_datetime);
        TextView eventTagsTV = (TextView)findViewById(R.id.event_tags);

        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int count = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (count==index)
                    {
                        // get the event details and update UI
                        eventName.setText(document.get("Name").toString());
                        eventDescription.setText(document.get("Description").toString());
                        eventVenue.setText(document.get("Venue").toString());
                        Timestamp ts = (Timestamp) document.get("Time");
                        eventDateTime.setText(ts.toDate().toString());
                        List<String> eventTags = (List<String>) document.get("Tags");
                        String tags = String.join(", ", eventTags);
                        eventTagsTV.setText(tags);
                        break;
                    }
                    else
                    {
                        count += 1;
                    }

                }
            } else {
                Log.e(TAG, "Error getting documents: ", task.getException());
            }
        });
    }
}