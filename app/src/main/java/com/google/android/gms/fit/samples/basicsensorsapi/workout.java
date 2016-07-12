package com.google.android.gms.fit.samples.basicsensorsapi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class workout extends AppCompatActivity implements ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback
{
    public static final String TAG = "BasicSensorsApi";
    public static final String SAMPLE_SESSION_NAME = "Afternoon run";

    private static final String DATE_FORMAT ="yyyy.MM.dd HH:mm:ss";
    // [START auth_variable_references]
    public static GoogleApiClient mClient = null;

    // [END auth_variable_references]

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // [START mListener_variable_reference]
    // Need to hold a reference to this listener, as it's passed into the "unregister"
    // method in order to stop all sensors from sending data to this listener.
//    private static OnDataPointListener mListener;
    private static int totalSteps = 0;
    private static OnDataPointListener walkListener;
    private static OnDataPointListener distListener;
    private static DataSource walkDataSource;
    private static DataSource distDataSource;
    private ArrayList<OnDataPointListener> listeners = new ArrayList<>();
    private ArrayList<DataSource> dataSources = new ArrayList<>();
    private DataSet walkDataSet;
    private Session session;
    private long startTime;
    private long stopTime;
    private final double stepValue = 0.76;
    private static double totalDistance = 0;
    private boolean hasWorkoutStarted = false;
    TextView lblSteps;
    TextView lblDistance;
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_page);
//        initializeLogging();
        if (!checkPermissions()) {
            requestPermissions();
        }

        lblSteps = (TextView)findViewById(R.id.lblSteps);
        lblDistance = (TextView) findViewById(R.id.lblDistance);
        lblSteps.setText("0");
        lblDistance.setText("0m");


    }

    public void btnActivityClicked(View v) {
        Button btnSession = (Button) v;
        if(hasWorkoutStarted == false){
            ((Button) v).setText("Session Started...");
            hasWorkoutStarted = true;
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);
            startTime = cal.getTimeInMillis();
            startStepListening();
//            new stepListeningTask().execute();
            ((Button) v).setText("Stop Session");
        }else{
            ((Button) v).setText("Stopping Session...");
            hasWorkoutStarted = false;
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);
            stopTime = cal.getTimeInMillis();

            unregisterFitnessDataListener(mClient, walkListener);
//            Log.i(TAG, "Total Steps: " + totalSteps + "");
            lblSteps.setText(totalSteps + "");
            lblDistance.setText(String.format( "%.2f", totalDistance ));
//            Log.i(TAG, "Total Distance: " + totalDistance + "m");
//            Log.i(TAG, "Discovery Vitality Points: "  + calculateDiscoveryPoints(totalDistance)+"");
            ((Button) v).setText("Start Session");
        }


    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
    }


    private class stepListeningTask extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
//            Toast.makeText(getApplicationContext(), "Alternate Thread Started", Toast.LENGTH_SHORT).show();
            startStepListening();
            return null;
        }
    }


    private void startStepListening(){
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
                .setDataSourceTypes(DataSource.TYPE_DERIVED)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
//                        Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
//                            Log.i(TAG, "Data source found: " + dataSource.toString());
//                            Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());
                            if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA) && walkListener == null) {
                                walkDataSource = dataSource;
//                                Log.i(TAG, "Data source for STEP found!  Registering.");
                                Toast.makeText(getApplicationContext(), "DataSource", Toast.LENGTH_SHORT).show();
                                walkListener = new OnDataPointListener() {
                                    @Override
                                    public void onDataPoint(DataPoint dataPoint) {
                                        for (Field field : dataPoint.getDataType().getFields()) {
                                            final Value val = dataPoint.getValue(field);
                                            totalSteps += val.asInt();
                                            totalDistance += (stepValue * val.asInt());
//                                            final DecimalFormat df = new DecimalFormat("#.00");
//                                            totalDistance = Double.valueOf(df.format(totalDistance));
//                                            Log.i(TAG, "Steps: " + val + " distance: " + (stepValue * val.asInt()) + "");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    lblSteps.setText(totalSteps + "");
                                                    lblDistance.setText(String.format( "%.2f", totalDistance ));
                                                }
                                            });

                                        }
                                    }
                                };
                                Fitness.SensorsApi.add(mClient,
                                        new SensorRequest.Builder()
                                                .setDataSource(walkDataSource)
                                                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                                                .setSamplingRate(5, TimeUnit.MINUTES)
                                                .build(), walkListener).setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {
                                        if (status.isSuccess()) {
//                                            Log.i(TAG, "WALK Listener registered!");
                                            Toast.makeText(getApplicationContext(), "Listeners initialized", Toast.LENGTH_SHORT).show();
                                        } else {
//                                            Log.i(TAG, "WALK Listener not registered");
                                            Toast.makeText(getApplicationContext(), "Listeners failed to register", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }   //END STEP COUNT DELTA
                        }
                    }
                });
    }

    public int calculateDiscoveryPoints(double dist){
        return (int)((dist *6) /100);
    }

    public int calculateSharePoints(double dist, int socialPlatforms){
        int vitalityPoints = calculateDiscoveryPoints(dist);
        return (int)(((vitalityPoints * 0.075) * socialPlatforms));
    }


    @Override
    protected void onResume() {
        super.onResume();


        // This ensures that if the user denies the permissions then uses Settings to re-enable
        // them, the app will start working.
        buildFitnessClient();


    }

    private void buildFitnessClient() {
        if (mClient == null && checkPermissions()) {
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(Fitness.SENSORS_API)
                    .addApi(Fitness.RECORDING_API)
                    .addApi(Fitness.HISTORY_API)
                    .addApi(Fitness.SESSIONS_API)
                    .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                    .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle)
                                {
                                    Toast.makeText(getApplicationContext(), "Client Connected", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onConnectionSuspended(int i)
                                {
                                    // If your connection to the sensor gets lost at some point,
                                    // you'll be able to determine the reason and react to it here.
                                    if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
//                                        Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                    } else if (i
                                            == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
//                                        Log.i(TAG,"Connection lost.  Reason: Service Disconnected");
                                    }
                                }
                            }
                    )
                    .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
//                            Log.i(TAG, "Google Play services connection failed. Cause: " +result.toString());
                            Snackbar.make(
                                    workout.this.findViewById(R.id.main_activity_view),
                                    "Exception while connecting to Google Play services: " +
                                            result.getErrorMessage(),
                                    Snackbar.LENGTH_INDEFINITE).show();
                        }
                    })
                    .build();
        }

    }

    private void unregisterFitnessDataListener(GoogleApiClient client,OnDataPointListener listener) {
        if (listener == null) {
            // This code only activates one listener at a time.  If there's no listener, there's
            // nothing to unregister.
            return;
        }

//         [START unregister_data_listener]
//         Waiting isn't actually necessary as the unregister call will complete regardless,
//         even if called from within onStop, but a callback can still be added in order to
//         inspect the results.
        Fitness.SensorsApi.remove(
                client,
                listener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
//                            Log.i(TAG, "Listener was removed!");
                        } else {
//                            Log.i(TAG, "Listener was not removed.");
                        }
                    }
                });
        // [END unregister_data_listener]
    }

//    private void initializeLogging() {
//        // Wraps Android's native log framework.
//        LogWrapper logWrapper = new LogWrapper();
//        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
//        Log.setLogNode(logWrapper);
//        // Filter strips out everything except the message text.
//        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
//        logWrapper.setNext(msgFilter);
//        // On screen logging via a customized TextView.
//        LogView logView = (LogView) findViewById(R.id.sample_logview);
//
//        // Fixing this lint errors adds logic without benefit.
//        //noinspection AndroidLintDeprecation
//        logView.setTextAppearance(this, R.style.Log);
//
//        logView.setBackgroundColor(Color.WHITE);
//        msgFilter.setNext(logView);
//        Log.i(TAG, "Ready");
//    }

    private boolean checkPermissions()
    {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
//            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.main_activity_view),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(workout.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
//            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(workout.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
//        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
//                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                buildFitnessClient();

            } else {
                // Permission denied.

                // In this Activity we've chosen to notify the user that they
                // have rejected a core permission for the app since it makes the Activity useless.
                // We're communicating this message in a Snackbar since this is a sample app, but
                // core permissions would typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                Snackbar.make(
                        findViewById(R.id.main_activity_view),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.settings, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText( workout.this , "Connected",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
