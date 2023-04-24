package com.example.buzzup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public class ViewEventActivity extends AppCompatActivity
{
    FirebaseFirestore db;
    final String TAG = "ViewEventActivity";

    TextView eventNameTV;
    TextView eventTagsTV;
    ImageView eventImagesIV;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        int index = Integer.parseInt(intent.getStringExtra("index"));

        eventNameTV = (TextView)findViewById(R.id.event_name);
        eventTagsTV = (TextView)findViewById(R.id.event_tags);
        eventImagesIV = (ImageView)findViewById(R.id.iv_event_images);

        db.collection("events").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int count = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (count==index)
                    {
                        // get the event details and update UI
                        eventNameTV.setText(document.get("Name").toString());
                        List<String> eventTags = (List<String>) document.get("Tags");
                        String tags = String.join(", ", eventTags);
                        eventTagsTV.setText(tags);
                        List<String> imageUrls = (List<String>) document.get("ImageUrls");
                        String imageUrl = imageUrls.get(0);
                        Picasso.get().load(imageUrl).into(eventImagesIV);
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