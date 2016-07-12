package com.google.android.gms.fit.samples.basicsensorsapi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class ActivitiesDetailsActivity extends AppCompatActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_details);
        Bundle extras = getIntent().getExtras();

//        Toast.makeText(getApplicationContext(),extras.getString("position"),Toast.LENGTH_SHORT).show();

        TextView txtName = (TextView)findViewById(R.id.txtName);
        TextView txtDist = (TextView)findViewById(R.id.txtDistValue);
        ImageView img = (ImageView)findViewById(R.id.imgViewActivityImage);
        RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        int position = Integer.parseInt(extras.getString("position"));
        img.setImageResource(com.google.android.gms.fit.samples.basicsensorsapi.TabbedActivity.imageId[position]);
        txtName.setText(com.google.android.gms.fit.samples.basicsensorsapi.TabbedActivity.activitiesarr[position]);
        txtDist.setText(com.google.android.gms.fit.samples.basicsensorsapi.TabbedActivity.distancesArr[position]);
        ratingBar.setRating(com.google.android.gms.fit.samples.basicsensorsapi.TabbedActivity.ratingsArr[position]);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    public void btnStartActivityClicked(View v) {
        Intent myIntent = new Intent(getApplicationContext(),WorkoutActivity.class);
        startActivity(myIntent);
    }

    public void btnTakeMeThereClicked(View v) {
        Toast.makeText(getApplicationContext(),"LOLZ soz, take you there now now, but not now ;) ",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;


        Latitudes p  =  new Latitudes();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Latitudes.route.get(Latitudes.route.size()-1), 10));

        mMap.addPolyline((new PolylineOptions())
                .addAll(Latitudes.route)
                .width(15)
                .color(Color.GREEN)
                .geodesic(true));
        mMap.addMarker(new MarkerOptions().position(p.route.get(0)).title("start Activity"));
        mMap.addMarker(new MarkerOptions().position(p.route.get(Latitudes.route.size() - 1)).title("Get your points here"));
        mMap.setBuildingsEnabled(true);

        CameraPosition currentCameraPosition = mMap.getCameraPosition();

        float currentTilt = currentCameraPosition.tilt;

        float newTilt = currentTilt + 10;
        newTilt = (newTilt > 0) ? newTilt : 0;

        CameraPosition cameraPosition = new CameraPosition.Builder(currentCameraPosition)
                .tilt(newTilt).zoom(200).build();
        changeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }

    private void changeCamera(CameraUpdate update) {
        changeCamera(update, null);
    }

    /**
     * Change the camera position by moving or animating the camera depending on the state of the
     * animate toggle button.
     */
    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {


        int duration = 10;
        mMap.animateCamera(update, Math.max(duration, 1), callback);

        mMap.moveCamera(update);

    }




}
