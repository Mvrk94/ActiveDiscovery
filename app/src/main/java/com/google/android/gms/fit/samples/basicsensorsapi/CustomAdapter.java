package com.google.android.gms.fit.samples.basicsensorsapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by user on 2016-07-10.
 */
public class CustomAdapter extends ArrayAdapter<String>
{
    private final Integer[] imageId;
    private final String[] distancesArr;
    private final Integer[] ratingArr;


    CustomAdapter(Context context,Integer[] imgID, String[] activitiesarr,String[] distancesArr,Integer[] ratingArr)
    {
        super(context,R.layout.custom_activity_row,activitiesarr);
        this.imageId=imgID;
        this.distancesArr=distancesArr;
        this.ratingArr=ratingArr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater infl = LayoutInflater.from(getContext());
        View customView = infl.inflate(R.layout.custom_activity_row,parent,false);

        String singleActivity = getItem(position);//reference to strings in array, position of each item in list

        //components of the custom row
        TextView mText = (TextView) customView.findViewById(R.id.textviewActivityName);
        TextView dText = (TextView) customView.findViewById(R.id.textviewDistance);
        RatingBar rBar = (RatingBar) customView.findViewById(R.id.ratingBarActivityList);
        ImageView imgView = (ImageView) customView.findViewById((R.id.imageViewActivity));

        mText.setText(singleActivity);
        dText.setText(distancesArr[position]);
        rBar.setRating(ratingArr[position]);
        imgView.setImageResource(imageId[position]);
        return customView;


    }
}
