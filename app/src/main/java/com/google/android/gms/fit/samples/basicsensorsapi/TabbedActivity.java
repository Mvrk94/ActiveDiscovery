package com.google.android.gms.fit.samples.basicsensorsapi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;

public class TabbedActivity extends AppCompatActivity
{
    public static ListView listview;
    public static Integer[] imageId = {R.drawable.morning_run_square,
            R.drawable.lake_city_run,
            R.drawable.cycle_square,
            R.drawable.pilates,
            R.drawable.zumba_dance_class,
            R.drawable.pilates2
    };

    public static String[] distancesArr = new String[]{"Distance from you: 3km","Distance from you: 5km","Distance from you: 2km","Distance from you: 3km","Distance from you: 1.5km","Distance from you: 4.5km",};
    public static Integer[] ratingsArr = new Integer[]{3,5,4,3,3,5};
    public static String[] activitiesarr =  new String[]{"Gem of Joburg Walk","Zoo Lake Trail","Center City Cycle","Outdoor Fitness Class","Zumba Dance Class"};



    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;



  //  private GoogleApiClient myGoogGoogleApiClient = SignIn.mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

//        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
//        {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//                switch (position)
//                {
//
//                    case-1:
//                        fab.hide();
//
//                        break;
//                    case 0:
//                        fab.hide();
//                        break;
//                    case 1:
//                        fab.show();
//                        break;
//                    case 2:
//                        fab.hide();
//                        break;
//                    default:
//                        fab.hide();
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        /**
         * set up Drawer Toggle of the Toolbar
         */



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tabbed, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                return true;

            case R.id.action_sign_out:
                Intent signOutIntent = new Intent(this, SignIn.class);
                startActivity(signOutIntent);

            case R.id.action_add_activity:
                Intent addIntent = new Intent(this, AddActivity.class);
                startActivity(addIntent);



        }

        return false;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragmentHome extends Fragment  implements OnMapReadyCallback
    {


        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private SupportMapFragment mapFrag;
        private GoogleMap mgglMap;

        public PlaceholderFragmentHome() {
        }

        public static PlaceholderFragmentHome newInstance(int sectionNumber) {
            PlaceholderFragmentHome fragment = new PlaceholderFragmentHome();

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_tabbed_home, container, false);

          TextView textView =(TextView)rootView.findViewById(R.id.textViewMore);
           textView.setClickable(true);
         textView.setMovementMethod(LinkMovementMethod.getInstance());
          String text = "<a href='https://www.discovery.co.za/portal/individual/vitality-team-vitality'> more... </a>";
          textView.setText(Html.fromHtml(text));

            return rootView;
        }
        @Override
        public void onActivityCreated( Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            FragmentManager fm = getChildFragmentManager();
            mapFrag = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
            if (mapFrag == null) {
                mapFrag = SupportMapFragment.newInstance();




                fm.beginTransaction().replace(R.id.map_container, mapFrag).commit();
            }

        }

        @Override
        public void onResume() {
            super.onResume();
            if (mgglMap == null) {
                mgglMap= mapFrag.getMap();

                mgglMap.setBuildingsEnabled(true);
                mgglMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-26.103657, 28.045737), 10));


                mgglMap.addCircle(new CircleOptions().center(new LatLng(-26.104331, 28.050218)).radius(2500));


                mgglMap.addMarker(new MarkerOptions().position(new LatLng(-26.101863, 28.075103)).title("Barlow and Innesfree Park walk"));

                mgglMap.addMarker(new MarkerOptions().position(new LatLng(-26.099101, 28.033607)).title("George Lea Park Community Movement "));

                mgglMap.addMarker(new MarkerOptions().position(new LatLng(-26.121532, 28.038740)).title("Hyde Park Trail"));



            }
        }

        @Override
        public void onMapReady(GoogleMap googleMap)
        {
            mgglMap =  googleMap;
            mgglMap.setBuildingsEnabled(true);
            mgglMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-26.103657, 28.045737),5));


            mgglMap.addCircle(new CircleOptions().center(new LatLng(-26.104331, 28.050218)).radius(2500));


            mgglMap.addMarker(new MarkerOptions().position(new LatLng(-26.101863, 28.075103)).title("Barlow and Innesfree Park walk"));

            mgglMap.addMarker(new MarkerOptions().position(new LatLng(-26.099101, 28.033607)).title("George Lea Park Community Movement "));

            mgglMap.addMarker(new MarkerOptions().position(new LatLng(-26.121532, 28.038740)).title("Hyde Park Trail"));




        }


        //        @Override
//        public void onViewCreated(View view, Bundle savedInstanceState)
//        {
//            super.onViewCreated(view, savedInstanceState);
//            //get a reference to the small map fragment
//            MapFragment mapfrag = (MapFragment)getChildFragmentManager().findFragmentById(R.id.fragment_map);
//            mapfrag.getMapAsync(this);
//        }


    }


    public static class PlaceholderFragmentActivities extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragmentActivities() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragmentActivities newInstance(int sectionNumber) {
            PlaceholderFragmentActivities fragment = new PlaceholderFragmentActivities();
           // Bundle args = new Bundle();
            //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            //fragment.setArguments(args);
            return fragment;
        }

        Button mainFragButton;
        TextView mText;



        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState)
        {

           //******HAD BEFORE:
            View rootView = inflater.inflate(R.layout.fragment_tabbed_activity, container, false);
            //Initialize a listview
            listview = (ListView) rootView.findViewById(R.id.listViewActivities);
            listview.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

//            imageId = {R.drawable.morning_run_square,
//                                    R.drawable.lake_city_run,
//                                    R.drawable.cycle_square,
//                         R.drawable.pilates,
//                    R.drawable.zumba_dance_class,
//                    R.drawable.pilates2
//            };

             //String [] activitiesarr = new String[] {"Gem of Joburg Walk","Zoo Lake Trail","Center City Cycle","Outdoor Fitness Class","Zumba Dance Class"};


            ListAdapter listadpter = new CustomAdapter(getActivity(),imageId,activitiesarr,distancesArr,ratingsArr);

            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, activitiesarr);

            listview.setAdapter(listadpter);

            //listview.setOnItemClickListener(new ItemClickListener());


             listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   // String activity = activitiesarr[position];
                    Intent myIntent = new Intent(getActivity(),ActivitiesDetailsActivity.class);
                    myIntent.putExtra("position", position+"");
//                    Bundle myBundle = new Bundle();
//                    myBundle.putInt("position", position);
//                    myIntent.putExtras(myBundle);
                    startActivity(myIntent);
                    //Toast.makeText(getActivity(),"Clicked me", Toast.LENGTH_LONG).show();
                }
            });
            //listview.setAdapter(new CustomAdapter(getActivity(),activitiesarr));

            return rootView;
            //**************

        }


    }

    public static class PlaceholderFragmentDelight extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        Button btnInvite;

        private static final String ARG_SECTION_NUMBER = "section_number";



        public PlaceholderFragmentDelight()
        {}

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        //Links with fragment_tabbed_home
        public static PlaceholderFragmentDelight newInstance(int sectionNumber) {
            PlaceholderFragmentDelight fragment = new PlaceholderFragmentDelight();
            //Bundle args = new Bundle();
            //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            //fragment.setArguments(args);
            return fragment;
        }




        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            int currentPoints = 37324;
            int reqPoints = 7676;
          Button btnSendInvite;

            View rootView = inflater.inflate(R.layout.fragment_tabbed_delight, container, false);
            if(ReviewShareActivity.hasDoneWorkout == true){
                currentPoints += ReviewShareActivity.points ;
                reqPoints -= ReviewShareActivity.points ;
                TextView txtCurrentPoints = (TextView) rootView.findViewById(R.id.lblPoints);
                txtCurrentPoints.setText(String.valueOf(currentPoints));
                TextView txtReqPoints = (TextView) rootView.findViewById(R.id.lblRequiredPoints);
                txtReqPoints.setText(String.valueOf(reqPoints));
            }

            btnSendInvite = (Button) rootView.findViewById(R.id.buttonInvite);
            btnSendInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Toast.makeText(getContext(),"Invite sent.",Toast.LENGTH_SHORT).show();
                    }

            });



            btnInvite = (Button) rootView.findViewById(R.id.buttonInvite);

            btnInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


            return rootView;
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).


            switch (position) {
                case 0:
                    return PlaceholderFragmentHome.newInstance(position + 1);
                case 1:
                    return PlaceholderFragmentActivities.newInstance(position+1);

                case 2:
                    return PlaceholderFragmentDelight.newInstance(position+1);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Discover";
                case 1:
                    return "Active";
                case 2:
                    return "Delights";
            }
            return null;
        }
    }
}
