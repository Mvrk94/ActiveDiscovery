package com.google.android.gms.fit.samples.basicsensorsapi;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.fit.samples.basicsensorsapi.TabbedActivity;

public class ActivitiesDetailsActivity extends AppCompatActivity
{

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

    }

    public void btnStartActivityClicked(View v) {
        Intent myIntent = new Intent(getApplicationContext(),WorkoutActivity.class);
        startActivity(myIntent);
    }

    public void btnTakeMeThereClicked(View v) {
        Toast.makeText(getApplicationContext(),"LOLZ soz, take you there now now, but not now ;) ",Toast.LENGTH_SHORT).show();
    }

}
