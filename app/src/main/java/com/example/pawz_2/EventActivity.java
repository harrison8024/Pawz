package com.example.pawz_2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class EventActivity extends AppCompatActivity implements OnMapReadyCallback {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allUserRef = database.getReference("Users");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    private Double latitude;
    private Double longitude;
    private String locationName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Toolbar eventToolBar = (Toolbar) findViewById(R.id.event_toolbar);
        setSupportActionBar(eventToolBar);
        Intent intent = getIntent();

        longitude = intent.getDoubleExtra("Longitude", 0);
        latitude = intent.getDoubleExtra("Latitude", 0);
        locationName = intent.getStringExtra("Location");

        TextView titleText = findViewById(R.id.event_title);
        titleText.setText(intent.getStringExtra("Title"));
        TextView dateText = findViewById(R.id.event_date);
        dateText.setText(intent.getStringExtra("Date"));
        TextView timeText = findViewById(R.id.event_time);
        timeText.setText(intent.getStringExtra("Time"));
        TextView locationText = findViewById(R.id.event_location);
        locationText.setText(intent.getStringExtra("Location"));
        TextView detailText = findViewById(R.id.event_detail);
        detailText.setText(intent.getStringExtra("Detail"));
        String uid = intent.getStringExtra("Uid");
        ImageView profileImg = findViewById(R.id.profile_picture);
        // get host displayname
        TextView hostText = findViewById(R.id.host_name);

        // get displayname with host id
        allUserRef.child(uid).child("displayname").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    hostText.setText("@" + task.getResult().getValue().toString());
                }
            }
        });

        // get profile picture with host id
        allUserRef.child(uid).child("profile_picture").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    if(task.getResult().exists()) {
                        Picasso.get().load(task.getResult().getValue().toString()).transform(new CircleTransform()).into(profileImg);
                    }
                }
            }
        });

        if(currentUser.getUid().equals(uid)){
            Button attendBtn = findViewById(R.id.attend_btn);
            Button followBtn = findViewById(R.id.follow_btn);
            attendBtn.setVisibility(View.INVISIBLE);
            followBtn.setVisibility(View.INVISIBLE);
        }


        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng location = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title(locationName));
        float zoomLevel = 13.0f;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));

    }


    public void goBack(View view) {
        finish();
    }

    public void followEvent(View view) {

    }

    public void attendEvent(View view) {
    }
}