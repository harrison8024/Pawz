package com.example.pawz_2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditEvent extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference eventRef;
    final Calendar calendar = Calendar.getInstance();
    final private static int AUTOCOMPLETE_REQUEST_CODE = 0011;

    private String key;
    private String title;
    private String date;
    private String time;
    private String location;
    private String detail;

    private EditText editTitle;
    private EditText editDate;
    private EditText editTime;
    private EditText editLocation;
    private EditText editDetail;

    private String locationID;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // get all UI reference
        editTitle = findViewById(R.id.edit_title);
        editDate = findViewById(R.id.edit_date);
        editTime = findViewById(R.id.edit_time);
        editLocation = findViewById(R.id.edit_location);
        editDetail = findViewById(R.id.edit_detail);

        // get all intent extra
        Intent intent = getIntent();
        title = intent.getStringExtra("Title");
        date = intent.getStringExtra("Date");
        time = intent.getStringExtra("Time");
        location = intent.getStringExtra("Location");
        detail = intent.getStringExtra("Detail");
        key = intent.getStringExtra("Key");

        // set text in UI
        editTitle.setText(title);
        editDate.setText(date);
        editTime.setText(time);
        editLocation.setText(location);
        editDetail.setText(detail);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditEvent.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
            }
        });
        editTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog time = new TimePickerDialog(EditEvent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        editTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                time.setTitle("Select Time");
                time.show();
            }
        });
        // Start Google Place
        String apiKey = getString(R.string.api_key);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        PlacesClient placesClient = Places.createClient(this);

        editLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(v.getContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.d("RESULT", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());
                editLocation.setText(place.getName());
                locationID = place.getId();
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                Log.d("RESULT", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.d("RESULT", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void updateLabel(){
        String format = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        editDate.setText(sdf.format(calendar.getTime()));
    }

    public void confirmEdit(View view) {
        database = FirebaseDatabase.getInstance();
        eventRef = database.getReference("events/" + key);

        //get data from UI
        title = editTitle.getText().toString();
        date = editDate.getText().toString();
        time = editTime.getText().toString();
        detail = editDetail.getText().toString();
        location = editLocation.getText().toString();

        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("title", title);
        updates.put("date", date);
        updates.put("time", time);
        updates.put("detail", detail);
        updates.put("location", location);
        updates.put("lat", latitude);
        updates.put("lon", longitude);
        eventRef.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(EditEvent.this, "Upload Success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditEvent.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }

    public void cancelEdit(View view) {
        finish();
    }
}