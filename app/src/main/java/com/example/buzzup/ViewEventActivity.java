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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

public class ViewEventActivity extends AppCompatActivity
{
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        Intent intent = getIntent();
        int index = Integer.parseInt(intent.getStringExtra("index"));
        Log.d("View Event", "index is : "+index);

        TextView eventName = (TextView)findViewById(R.id.event_name);
        TextView eventDescription = (TextView)findViewById(R.id.event_description);
        TextView eventLikes = (TextView)findViewById(R.id.event_likes);
        TextView eventVenue = (TextView)findViewById(R.id.event_venue);

        db = FirebaseFirestore.getInstance();
        db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = 0;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (count==index)
                        {
                            //get the event details
                            Log.d("View Event", document.getId() + " => " + document.getData());
                            //update UI
                            eventName.setText("EVENT NAME : "+document.get("Name").toString());
                            eventDescription.setText("EVENT DESCRIPTION : "+document.get("Description").toString());
                            eventLikes.setText("EVENT LIKES : "+document.get("Likes").toString());
                            eventVenue.setText("EVENT VENUE : "+document.get("Venue").toString());
                            break;
                        }
                        else
                        {
                            count += 1;
                        }

                    }
                } else {
                    Log.d("View Event", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}