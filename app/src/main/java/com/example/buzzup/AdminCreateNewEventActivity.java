package com.example.buzzup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AdminCreateNewEventActivity extends AppCompatActivity {
    private Button mSubmitBtn;
    private EditText mEventTitle;
    private EditText mEventDesc;

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;


    private DatePicker mEventDate;
    private TimePicker mEventTime;
    private EditText mEventLoc;
    private Button mEventPosterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_new_event);

        auth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        String email = user.getEmail();

        mSubmitBtn = findViewById(R.id.btn_admin_create_event_submit_btn);
        mEventTitle = findViewById(R.id.et_admin_create_event_title);
        mEventDesc = findViewById(R.id.et_admin_create_event_desc);
        mEventDate = findViewById(R.id.dp_admin_create_event_date);
        mEventTime = findViewById(R.id.tp_admin_create_event_time);
        mEventLoc = findViewById(R.id.et_admin_create_event_loc);
        mEventPosterBtn = findViewById(R.id.btn_admin_create_event_poster_upload_btn);

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
//            refer : https://cloud.google.com/firestore/docs/manage-data/add-data#javaandroid
            @Override
            public void onClick(View view){
                Map<String,Object> data = new HashMap<>();
                data.put("Name",mEventTitle.getText().toString());
                data.put("Description",mEventDesc.getText().toString());
                data.put("Venue", mEventLoc.getText().toString());
                data.put("Likes", 0);
                data.put("Participants", new ArrayList<>());
                data.put("VenueCoordinates", new GeoPoint(0,0));

                // refer : https://stackoverflow.com/questions/54927084/get-timestamp-from-datepicker-and-timepicker
                int year = mEventDate.getYear();
                int month = mEventDate.getMonth();
                int day = mEventDate.getDayOfMonth();
                int hour= mEventTime.getHour();
                int minute = mEventTime.getMinute();

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                Long timeInMillis = calendar.getTimeInMillis();
                // refer : https://stackoverflow.com/questions/63125153/kotlin-convert-date-or-calendar-to-firebase-timestamp
                Timestamp timestamp = new Timestamp(new Date(timeInMillis));
                data.put("Time", timestamp);

                String uuid = UUID.randomUUID().toString();

                db.collection("events").document(uuid.replace("-", "")).set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("AdminCreateNewEventActivity", "DocumentSnapshot successfully written!");
                            }

                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("AdminCreateNewEventActivity", "Error writing document", e);
                            }

                        });
                Toast.makeText(getApplicationContext(), "Uploaded data", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

//        TODO implement image poster upload
        mEventPosterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "TODO implement image poster upload", Toast.LENGTH_SHORT).show();
            }
        });
    }
}