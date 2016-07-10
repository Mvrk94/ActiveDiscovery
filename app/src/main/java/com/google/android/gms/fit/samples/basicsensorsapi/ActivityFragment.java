package com.google.android.gms.fit.samples.basicsensorsapi;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by user on 2016-07-10.
 */
public class ActivityFragment extends Fragment

{

    public ActivityFragment()
    {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    //Links with fragment_tabbed_home
    public static ActivityFragment newInstance(int sectionNumber) {
        ActivityFragment fragment = new ActivityFragment();
        //Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        //fragment.setArguments(args);
        return fragment;
    }

    Button mainFragButton;
    TextView mText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_tabbed_activity, container, false);


        return rootView;
    }
}
