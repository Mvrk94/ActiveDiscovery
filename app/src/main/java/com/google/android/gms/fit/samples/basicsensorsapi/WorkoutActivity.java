package com.google.android.gms.fit.samples.basicsensorsapi;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

//import android.view.ViewGroup;

public class WorkoutActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private GoogleApiClient mClient = null;
    private OnDataPointListener stepsListener;
    private static int totalSteps = 0;
    private static OnDataPointListener walkListener;
    private static DataSource walkDataSource;
    private DataSet walkDataSet;
    private final double stepValue = 0.76;
    private static double totalDistance = 0;
    private boolean hasWorkoutStarted = false;
    Thread thread;
    Intent startIntent;
    public TextView lblSteps;
    TextView lblDistance;
    Handler stepsHandler;
    Handler distHandler;
    public static int discoveryPoints=0;
    public static int sharePoints =0;
    int x = 1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_page);

        if (!checkPermissions()) {
            requestPermissions();
        }
        startIntent = getIntent();
        lblSteps = (TextView)findViewById(R.id.lblSteps);
        lblDistance = (TextView) findViewById(R.id.lblDistance);
        lblSteps.setText("0");
        lblDistance.setText("0 m");
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
    }

    public void nullify(){
        stepsListener =null;
        walkListener= null;
        walkDataSource = null;
        walkDataSet =  null;
//        thread.stop();
        Intent startIntent;
        stepsHandler = null;
        distHandler = null;
    }


    @Override
    public void onMapReady(GoogleMap map)
    {
        mMap = map;

        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        Latitudes p  =  new Latitudes();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-26.103657, 28.045737), 10));

        map.addPolyline((new PolylineOptions())
                .addAll(Latitudes.route)
                .width(15)
                .color(Color.GREEN)
                .geodesic(true));
        mMap.addMarker(new MarkerOptions().position(p.route.get(0)).title("start Activity"));
        mMap.addMarker(new MarkerOptions().position(p.route.get(Latitudes.route.size() - 1)).title("Get your points here"));
        mMap.setBuildingsEnabled(true);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
//        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();


        Location location  = mMap.getMyLocation();
        //check distance from start check point
        //start camera location include the first location
        CameraPosition currentCameraPosition = mMap.getCameraPosition();

        float currentTilt = currentCameraPosition.tilt;

        float newTilt = currentTilt + 10;
        newTilt = (newTilt > 0) ? newTilt : 0;

        CameraPosition cameraPosition = new CameraPosition.Builder(currentCameraPosition)
                .tilt(newTilt).bearing(location.getBearing()).zoom(200).build();



        changeCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        //changeCamera(CameraUpdateFactory.zoomIn());

        return false;
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

    public void initWorkoutActivity(){
        stepsHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                String steps = msg.what+"";
                lblSteps.setText(steps);
                super.handleMessage(msg);
            }


        };

        distHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
//                double dist = msg.what;
                String d = String.format("%.2f",  msg.obj) + " m";
                lblDistance.setText(d);
                super.handleMessage(msg);
            }
        };
    }

    public void btnActivityClicked(View v) {
        if(hasWorkoutStarted == false){
            ((Button) v).setText("Session Started...");
            hasWorkoutStarted = true;
//            initWorkoutActivity();
//            startStepListening();  //TODO start listening for steps
            ((Button) v).setText("Stop");
        }else{
//            ((Button) v).setText("Stopping Session...");
            hasWorkoutStarted = false;
            unregisterFitnessDataListener();
            lblSteps.setText(totalSteps + "");
            lblDistance.setText(String.format("%.2f", totalDistance));
            ((Button) v).setText("Start");
//            thread.stop();
          //  unregisterFitnessDataListener();
            discoveryPoints = calculateDiscoveryPoints(totalDistance);
            sharePoints = calculateSharePoints(totalDistance);

            Intent workoutIntent = new Intent(this,ReviewShareActivity.class);
            startActivity(workoutIntent);

           // startActivity(new Intent(WorkoutActivity.this, ReviewShareActivity.class));
        }
    }


    public int calculateDiscoveryPoints(double dist){
        return (int)((dist *6) /100);
    }

    public int calculateSharePoints(double dist){
        int vitalityPoints = calculateDiscoveryPoints(dist);
        return (int)(((vitalityPoints * 0.075)   ));
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildFitnessClient();
    }


    private void buildFitnessClient() {
        if (mClient == null && checkPermissions()) {
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(Fitness.SENSORS_API)
                    .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
//                                    Toast.makeText(getApplicationContext(), "Conected!!!", Toast.LENGTH_SHORT).show();
                                    findFitnessDataSources();
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    // If your connection to the sensor gets lost at some point,
                                    // you'll be able to determine the reason and react to it here.
                                    if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
//                                        Toast.makeText(getApplicationContext(), "Connection lost.  Cause: Network Lost.", Toast.LENGTH_SHORT).show();
                                    } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
//                                        Toast.makeText(getApplicationContext(), "Connection lost.  Reason: Service Disconnected", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                    ).enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
//                            Toast.makeText(getApplicationContext(), "Google Play services connection failed. Cause: " + result.toString(), Toast.LENGTH_SHORT).show();
                            Snackbar.make(WorkoutActivity.this.findViewById(R.id.main_activity_view), "Exception while connecting to Google Play services: " +
                                            result.getErrorMessage(),Snackbar.LENGTH_INDEFINITE).show();
                        }
                    }).build();
        }
    }

    private void findFitnessDataSources() {
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                .setDataSourceTypes(DataSource.TYPE_RAW, DataSource.TYPE_DERIVED)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
//                        Toast.makeText(getApplicationContext(), "Result: " + dataSourcesResult.getStatus().toString() , Toast.LENGTH_SHORT).show();
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            walkDataSource = dataSource;
                            final DataSource ds = dataSource;
                            runOnUiThread(new Runnable() {
                                public void run() {
//                                    Toast.makeText(getApplicationContext(), "Data source found: " + ds.toString(), Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(getApplicationContext(), "Data Source type: " + ds.getDataType().getName(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA) && stepsListener == null) {
//                                Toast.makeText(getApplicationContext(), "Data source for steps found!  Registering.", Toast.LENGTH_SHORT).show();
                                registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_DELTA);
                            }
                        }
                    }
                });
    }



    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        lblSteps.setText(String.valueOf(totalSteps));
        final DataSource ds = dataSource;
        initWorkoutActivity();
//        Toast.makeText(getApplicationContext(), "GETTING TO CODE" , Toast.LENGTH_SHORT).show();
        thread = new Thread(new Runnable() {
            public void run() {
                stepsListener = new OnDataPointListener() {
                    @Override
                    public void onDataPoint(DataPoint dataPoint) {
                        for (Field field : dataPoint.getDataType().getFields()) {
//                            Looper.prepare();
//                            Value val = dataPoint.getValue(field);
//                            Toast.makeText(getApplicationContext(), "Detected DataPoint field: " + field.getName(), Toast.LENGTH_SHORT).show();
//                            Toast.makeText(getApplicationContext(), "Detected DataPoint value: " + val, Toast.LENGTH_SHORT).show();
//                            totalSteps += val.asInt();
                            Message stepsMessage = new Message();
                            Message distMessage = new Message();
//                            if(val.asInt() >= 0){

//                                totalSteps += val.asInt();
//                            try {
//                                thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                            totalSteps += x;
                            stepsMessage.what = totalSteps;
//                                totalDistance += (stepValue * val.asInt());
                            totalDistance += (stepValue * 1);
                                distMessage.obj = totalDistance;
                                stepsHandler.sendMessage(stepsMessage);
                                distHandler.sendMessage(distMessage);
//                            }

                        }
                    }
                };

        Fitness.SensorsApi.add(mClient, new SensorRequest.Builder()
                .setDataSource(ds)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
//                .setSamplingRate(1, TimeUnit.SECONDS)
                .build(), stepsListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
//                            Toast.makeText(getApplicationContext(), "Ready. set. GO!! ", Toast.LENGTH_SHORT).show();
                        } else {
//                            Toast.makeText(getApplicationContext(), "Fatal Error... Im Dying :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        thread.start();
    }

    private void unregisterFitnessDataListener() {
        if (stepsListener == null) {
            return;
        }
        Fitness.SensorsApi.remove(mClient, stepsListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
//                            Toast.makeText(getApplicationContext(), "Listener was removed!", Toast.LENGTH_SHORT).show();
                        } else {
//                            Toast.makeText(getApplicationContext(), "Listener was not removed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }


    private void requestPermissions() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (shouldProvideRationale) {
//            Toast.makeText(getApplicationContext(), "Displaying permission rationale to provide additional context.", Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(R.id.main_activity_view), R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(WorkoutActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
//            Toast.makeText(getApplicationContext(), "Requesting permission", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(WorkoutActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Toast.makeText(getApplicationContext(), "onRequestPermissionResult", Toast.LENGTH_SHORT).show();

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE)
        {
            if (grantResults.length <= 0) {
//                Toast.makeText(getApplicationContext(), "User interaction was cancelled.", Toast.LENGTH_SHORT).show();

            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                buildFitnessClient();
            } else {
                Snackbar.make(findViewById(R.id.main_activity_view), R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).show();
            }
            return;
        }

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }


    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }


} // CLASS END