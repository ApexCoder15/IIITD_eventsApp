package com.example.buzzup;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminCreateNewEventActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;
    private Button mSubmitBtn;
    private EditText mEventTitle;
    private EditText mEventDesc;
    private EditText mEventLoc;
    private TextView selectDateText;
    private TextView selectTimeText;
    private TextView mEventCategory;
    private TextView selectEventPosterText;
    private TextView selectMapCoordsText;

    private double latitude = 0;
    private double longitude = 0;

    final Calendar myCalendar= Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_new_event);

        auth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        mSubmitBtn = findViewById(R.id.btn_admin_create_event_submit_btn);
        mEventTitle = findViewById(R.id.et_admin_create_event_title);
        mEventDesc = findViewById(R.id.et_admin_create_event_desc);
        mEventLoc = findViewById(R.id.et_admin_create_event_loc);
        mEventCategory = findViewById(R.id.et_admin_create_event_category);
        selectDateText = findViewById(R.id.te_admin_selectDateText);
        selectTimeText = findViewById(R.id.te_admin_selectTimeText);
        selectEventPosterText = findViewById(R.id.te_admin_selectEventPoster);
        selectMapCoordsText = findViewById(R.id.te_admin_selectVenueCoordinates);



        DatePickerDialog.OnDateSetListener dateListener =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);

                SimpleDateFormat dateFormat=new SimpleDateFormat("dd/mm/yyyy");
                selectDateText.setText(dateFormat.format(myCalendar.getTime()));
            }
        };
        selectDateText.setOnClickListener( view -> {
                new DatePickerDialog(this,dateListener,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        TimePickerDialog.OnTimeSetListener timeListener =new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hour);
                myCalendar.set(Calendar.MINUTE,minute);

                selectTimeText.setText(myCalendar.get(Calendar.HOUR_OF_DAY) + ": " + myCalendar.get(Calendar.MINUTE) + " hours");
            }
        };
        selectTimeText.setOnClickListener( view -> {
            new TimePickerDialog(this,timeListener,myCalendar.get(Calendar.HOUR_OF_DAY),myCalendar.get(Calendar.MINUTE), true).show();
        });

        selectMapCoordsText.setOnClickListener(view->{
            Intent intent = new Intent(getApplicationContext(), GetLocationFromMap.class);
            startActivityForResult(intent, RequestCodes.MAP_REQUEST_CODE);
        });

        //refer : https://cloud.google.com/firestore/docs/manage-data/add-data#javaandroid
        mSubmitBtn.setOnClickListener(view -> {

            String eventCategory = mEventCategory.getText().toString();
            List<String> eventTags = new ArrayList<>();
            eventTags.add(eventCategory);

            String defaultImageLink = "https://i.insider.com/602ee9ced3ad27001837f2ac?width=700";
            List<String> eventImageUrls = new ArrayList<>();
            eventImageUrls.add(defaultImageLink);

            Map<String,Object> data = new HashMap<>();
            data.put("Name",mEventTitle.getText().toString());
            data.put("Description",mEventDesc.getText().toString());
            data.put("Venue", mEventLoc.getText().toString());
            data.put("Likes", 0);
            data.put("Participants", new ArrayList<>());
            data.put("VenueCoordinates", new GeoPoint(latitude,longitude)); // TODO
            data.put("Tags", eventTags);
            data.put("ImageUrls", eventImageUrls); // TODO

            Long timeInMillis = myCalendar.getTimeInMillis();
            Timestamp timestamp = new Timestamp(new Date(timeInMillis));
            data.put("Time", timestamp);
            // refer : https://stackoverflow.com/questions/63125153/kotlin-convert-date-or-calendar-to-firebase-timestamp

            String uuid = UUID.randomUUID().toString();

            db.collection("events").document(uuid.replace("-", "")).set(data);
            Toast.makeText(getApplicationContext(), "New Event Created", Toast.LENGTH_SHORT).show();
            finish();
        });

//        TODO implement image poster upload
//        mEventPosterBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "Image", Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestCodes.MAP_REQUEST_CODE && resultCode == RESULT_OK) {
            double lat = data.getDoubleExtra("latitude", 0.0);
            double lon = data.getDoubleExtra("longitude", 0.0);

            // display the coordinates on screen and
            this.latitude = lat;
            this.longitude = lon;
            selectMapCoordsText.setText(new StringBuilder("").append(this.latitude).append(",\n").append(this.longitude).toString());
        }
    }
}