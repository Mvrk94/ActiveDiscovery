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
        int position = Integer.parseInt(extras.getString("position"));
        ImageView img = (ImageView)findViewById(R.id.imgViewActivityImage);
        img.setImageResource(com.google.android.gms.fit.samples.basicsensorsapi.TabbedActivity.imageId[position]);
    }
}
