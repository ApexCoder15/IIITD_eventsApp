package com.example.buzzup;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AdminCreateNewEventActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE = 100;

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

    Uri image_uri = null;

    ImageView imgIv;

    String[] cameraPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_new_event);

        auth = FirebaseAuth.getInstance();
        db =  FirebaseFirestore.getInstance();
        user = auth.getCurrentUser();

        cameraPermissions = new String[]{Manifest.permission.CAMERA};

        imgIv = findViewById(R.id.selected_image_iv);
        mSubmitBtn = findViewById(R.id.btn_admin_create_event_submit_btn);
        mEventTitle = findViewById(R.id.et_admin_create_event_title);
        mEventDesc = findViewById(R.id.et_admin_create_event_desc);
        mEventLoc = findViewById(R.id.et_admin_create_event_loc);
        mEventCategory = findViewById(R.id.et_admin_create_event_category);
        selectDateText = findViewById(R.id.te_admin_selectDateText);
        selectTimeText = findViewById(R.id.te_admin_selectTimeText);
        selectEventPosterText = findViewById(R.id.te_admin_selectEventPoster);
        selectMapCoordsText = findViewById(R.id.te_admin_selectVenueCoordinates);

        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
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

        selectEventPosterText.setOnClickListener(view->{
            showImagePickDialog();
        });

        //refer : https://cloud.google.com/firestore/docs/manage-data/add-data#javaandroid
        mSubmitBtn.setOnClickListener(view -> {
            if (image_uri == null) {
                uploadData("noImage");
            } else {
                uploadData(String.valueOf(image_uri));
            }

        });
    }

    private void uploadData(String uri) {

        String uuid = UUID.randomUUID().toString();
        String filePathAndName = new StringBuilder("").append("Events/").append("event_poster_").append(uuid).toString();
        if (!uri.equals("noImage")) {
            // post with selected image
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri)).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());

                String downloadUri = uriTask.getResult().toString();

                if (uriTask.isSuccessful()) {
                    // url is received. now upload stuff to DB

                    // event category
                    String eventCategory = mEventCategory.getText().toString();
                    List<String> eventTags = new ArrayList<>();
                    eventTags.add(eventCategory);

                    List<String> eventImageUrls = new ArrayList<>();
                    eventImageUrls.add(downloadUri);

                    Map<String,Object> data = new HashMap<>();
                    data.put("Name",mEventTitle.getText().toString());
                    data.put("Description",mEventDesc.getText().toString());
                    data.put("Venue", mEventLoc.getText().toString());
                    data.put("Likes", 0);
                    data.put("Participants", new ArrayList<>());
                    data.put("VenueCoordinates", new GeoPoint(latitude,longitude));
                    data.put("Tags", eventTags);
                    data.put("ImageUrls", eventImageUrls);

                    Long timeInMillis = myCalendar.getTimeInMillis();
                    Timestamp timestamp = new Timestamp(new Date(timeInMillis));
                    data.put("Time", timestamp);

                    // refer : https://stackoverflow.com/questions/63125153/kotlin-convert-date-or-calendar-to-firebase-timestamp

                    db.collection("events").document(uuid.replace("-", "")).set(data);
                    Toast.makeText(getApplicationContext(), "New Event Created", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }).addOnFailureListener(e->{
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            // no image upload
            // use a default image

            String defaultImageLink = "https://i.insider.com/602ee9ced3ad27001837f2ac?width=700";
            // You have been rick rolled :)

            // event category
            String eventCategory = mEventCategory.getText().toString();
            List<String> eventTags = new ArrayList<>();
            eventTags.add(eventCategory);

            List<String> eventImageUrls = new ArrayList<>();
            eventImageUrls.add(defaultImageLink);

            Map<String,Object> data = new HashMap<>();
            data.put("Name",mEventTitle.getText().toString());
            data.put("Description",mEventDesc.getText().toString());
            data.put("Venue", mEventLoc.getText().toString());
            data.put("Likes", 0);
            data.put("Participants", new ArrayList<>());
            data.put("VenueCoordinates", new GeoPoint(latitude,longitude));
            data.put("Tags", eventTags);
            data.put("ImageUrls", eventImageUrls);

            Long timeInMillis = myCalendar.getTimeInMillis();
            Timestamp timestamp = new Timestamp(new Date(timeInMillis));
            data.put("Time", timestamp);

            // refer : https://stackoverflow.com/questions/63125153/kotlin-convert-date-or-calendar-to-firebase-timestamp

            db.collection("events").document(uuid.replace("-", "")).set(data);
            Toast.makeText(getApplicationContext(), "New Event Created", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showImagePickDialog() {
        String[] options = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image from");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    // camera clicked
                    // check permission
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }else{
                        pickFromCamera();
                    }

                }
                if(i==1){
                    // gallery clicked
                    pickFromGallery();

                }
            }

        }).show();
    }

    private void pickFromCamera() {
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"temp pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"temp description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);

        cameraActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    imgIv.setImageURI(image_uri);
                }
            });

    private void pickFromGallery() {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    image_uri = data.getData();
                    imgIv.setImageURI(image_uri);
                }
            });

    private boolean checkCameraPermission(){
        boolean result1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(this, "camera  permissions is necessary", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
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