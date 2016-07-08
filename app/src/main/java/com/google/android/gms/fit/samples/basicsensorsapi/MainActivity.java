/*
 * Copyright (C) 2014 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.fit.samples.basicsensorsapi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.renderscript.Element;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fit.samples.common.logger.Log;
import com.google.android.gms.fit.samples.common.logger.LogView;
import com.google.android.gms.fit.samples.common.logger.LogWrapper;
import com.google.android.gms.fit.samples.common.logger.MessageOnlyLogFilter;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.SessionsApi;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.android.gms.fitness.result.ListSubscriptionsResult;
import com.google.android.gms.fitness.result.SessionReadResult;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * This sample demonstrates how to use the Sensors API of the Google Fit platform to find
 * available data sources and to register/unregister listeners to those sources. It also
 * demonstrates how to authenticate a user with Google Play Services.
 */
public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public Button btnSession;

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



    private boolean hasWorkoutStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeLogging();
        if (!checkPermissions()) {
            requestPermissions();
        }
    }

    public void btnWalkClicked(View v) {
       Button btnSession = (Button) v;
        if(hasWorkoutStarted == false){
            ((Button) v).setText("Session Started...");
            hasWorkoutStarted = true;
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);
            startTime = cal.getTimeInMillis();
//            new startDistList().execute();
//            new startwalkList().execute();



                  //  startDistanceListening();


                    startWalkListening();




            ((Button) v).setText("Stop Session");
        }else{
            ((Button) v).setText("Stopping Session...");
            hasWorkoutStarted = false;
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);
            stopTime = cal.getTimeInMillis();

            unregisterFitnessDataListener(mClient,walkListener);
            unregisterFitnessDataListener(mClient,distListener);
            //        Log.i(TAG, "Inserting Session to HistoryAPI");
//        SessionInsertRequest sessionReq = startSession();
//        Log.i(TAG, "Inserting Session to HistoryAPI");
//        Status insertStatus = Fitness.SessionsApi.insertSession(mClient,sessionReq).await(1, TimeUnit.MINUTES);
//        if(!insertStatus.isSuccess()){
//            Log.i(TAG, "There was a problem inserting the session: " + insertStatus.getStatusMessage());
//        }
//        Log.i(TAG, "Session insert was Successful");
//            cancelSubscription(DataType.TYPE_STEP_COUNT_DELTA);
            Log.i(TAG, "Total Steps: " + totalSteps + "");
            ((Button) v).setText("Start Session");
        }


    }

    private class startDistList extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            startDistanceListening();
            return null;
        }
    }

    private class startwalkList extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            startWalkListening();
            return null;
        }
    }




//    public SessionInsertRequest startSession(){
//
//        session = new Session.Builder()
//                .setName("WalkSession")
//                .setDescription("Walking Workout - walking around the block")
//                .setIdentifier("1668521")
//                .setActivity(FitnessActivities.WALKING)
//                .setStartTime(startTime, TimeUnit.MILLISECONDS)
//                .setEndTime(stopTime, TimeUnit.MILLISECONDS)
//                .build();
//
//        SessionInsertRequest sessionReq = new SessionInsertRequest.Builder()
//                .setSession(session)
//                .addDataSet(walkDataSet)
//                .build();
//
//        return sessionReq;
//    }

    public void startWalkListening(){
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE,DataType.TYPE_DISTANCE_CUMULATIVE)
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                        listeners.clear();
                        dataSources.clear();
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            dataSources.add(dataSource);
                            String fields = dataSource.getDataType().getFields().toString();
                            Log.i(TAG, "Fields: " + fields);
                            Log.i(TAG, "Data source found: " + dataSource.toString());
                            Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());

                            final DataType dataType = dataSource.getDataType();

                            if (dataType.equals(DataType.TYPE_STEP_COUNT_DELTA)||(dataType.equals(DataType.TYPE_DISTANCE_DELTA))) {
//                                walkDataSource = dataSource;
//
//                                subscribe(mClient, walkDataSource);
//                                walkDataSet = DataSet.create(walkDataSource);
//                                Log.i(TAG, "Data source for TYPE_STEP_COUNT_DELTA found!  Registering.");
                                final OnDataPointListener dpListener = new OnDataPointListener() {
                                    @Override
                                    public void onDataPoint(DataPoint dataPoint) {
                                        for (Field field : dataPoint.getDataType().getFields()){
                                            Value value = dataPoint.getValue(field);
                                            Log.i(TAG,"Field: "+field +", Value: "+value);
                                    }
                                }




//                                walkListener = new OnDataPointListener() {
//                                    @Override
//                                    public void onDataPoint(DataPoint dataPoint) {
//                                        for (Field field : dataPoint.getDataType().getFields()) {
//                                            final Value val = dataPoint.getValue(field);
////                                            walkDataSet.add(dataPoint);
//                                            totalSteps += val.asInt();
//                                            Log.i(TAG, "Steps Registered " + val + "");
//                                        }
//                                    }
//                                };


//                                Fitness.SensorsApi.add(mClient,
//                                        new SensorRequest.Builder()
//                                                .setDataSource(walkDataSource)
//                                                .setDataType(DataType.AGGREGATE_STEP_COUNT_DELTA)
//                                                .build(), walkListener).setResultCallback(new ResultCallback<Status>() {
//                                    @Override
//                                    public void onResult(Status status) {
//                                        if (status.isSuccess()) {
//                                            Log.i(TAG, "Listener registered! (NEW)");
//                                        } else {
//                                            Log.i(TAG, "Listener not registered.(NEW)");
//                                        }
//                                    }
//                                });

                            } ;  //END STEP COUNT DELTA
                                Fitness.SensorsApi.add(mClient,new SensorRequest.Builder()
                                        .setDataSource(dataSource)
                                        .setDataType(dataType)
                                        .build(),dpListener)
                                .setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(@NonNull Status status) {
                                        if(status.isSuccess()){
                                            listeners.add(dpListener);
                                            Log.i(TAG,"new Listener "+dataType.getName());
                                        }else{
                                            Log.i(TAG,"Failed to register listener for "+dataType.getName());
                                        }
                                    }
                                });

                    }


                }
        }
    });}


    private void startDistanceListening(){
        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_DISTANCE_DELTA)
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build())
                .setResultCallback(new ResultCallback<DataSourcesResult>() {
                    @Override
                    public void onResult(DataSourcesResult dataSourcesResult) {
                        Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
                            Log.i(TAG, "Data source found: " + dataSource.toString());
                            Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());
                            if (dataSource.getDataType().equals(DataType.TYPE_DISTANCE_DELTA) && distListener == null) {
                                distDataSource = dataSource;

                                subscribe(mClient,distDataSource);
                                Log.i(TAG, "Data source for DISTANCE found!  Registering.");
                                distListener = new OnDataPointListener() {
                                    @Override
                                    public void onDataPoint(DataPoint dataPoint) {
                                        for (Field field : dataPoint.getDataType().getFields()) {
                                            final Value val = dataPoint.getValue(field);
                                            Log.i(TAG, "DISTANCE VALUE (!!!) " + val + "");
                                        }
                                    }
                                };
                                Fitness.SensorsApi.add(mClient,
                                        new SensorRequest.Builder()
                                                .setDataSource(distDataSource)
                                                .setDataType(DataType.TYPE_DISTANCE_DELTA)
                                                .build(), distListener).setResultCallback(new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {
                                        if (status.isSuccess()) {
                                            Log.i(TAG, "DISTANCE Listener registered!");
                                        } else {
                                            Log.i(TAG, "Distance Listener not registered.(NEW)");
                                        }
                                    }
                                });

                            }   //END STEP COUNT DELTA
                        }
                    }
                });
    }




    public void subscribe(GoogleApiClient client,DataSource dataSource){
        Fitness.RecordingApi.subscribe(client, dataSource.getDataType())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected");
                            } else {
                                Log.i(TAG, "Successfully subscribed!");
                            }

                        } else {
                            Log.i(TAG, "There was a problem subscribing.");
                        }
                    }
                });
    }

    private void dumpSubscriptionList(){
        Fitness.RecordingApi.listSubscriptions(mClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<ListSubscriptionsResult>() {
                    @Override
                    public void onResult(@NonNull ListSubscriptionsResult listSubscriptionsResult) {
                        for (Subscription sc : listSubscriptionsResult.getSubscriptions()) {
                            DataType dt = sc.getDataType();
                            Log.i(TAG, "Active Subscriptions for data type: " + dt.getName());
                        }
                    }
                });
    }

    private void cancelSubscription(GoogleApiClient client,DataType type){
        final DataType t = type;
        Log.i(TAG,"Unsubscribing from data type: "+t);

        Fitness.RecordingApi.unsubscribe(client, type)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if(status.isSuccess()){
                            Log.i(TAG,"Successfully unsubscribed for data type: "+t);
                        }else{
                            Log.i(TAG,"Failed to unsubscribe for data type: "+t);
                        }
                    }
                });
    }


    private class InsertAndVerifySessionTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            //First, create a new session and an insertion request.



            SessionInsertRequest insertRequest = insertFitnessSession();

            // [START insert_session]
            // Then, invoke the Sessions API to insert the session and await the result,
            // which is possible here because of the AsyncTask. Always include a timeout when
            // calling await() to avoid hanging that can occur from the service being shutdown
            // because of low memory or other conditions.
            Log.i(TAG, "Inserting the session in the History API");
            com.google.android.gms.common.api.Status insertStatus =
                    Fitness.SessionsApi.insertSession(mClient, insertRequest)
                            .await(1, TimeUnit.MINUTES);

            // Before querying the session, check to see if the insertion succeeded.
            if (!insertStatus.isSuccess()) {
                Log.i(TAG, "There was a problem inserting the session: " +
                        insertStatus.getStatusMessage());
                return null;
            }

            // At this point, the session has been inserted and can be read.
            Log.i(TAG, "Session insert was successful!");
            // [END insert_session]

            // Begin by creating the query.

            SessionReadRequest readRequest = readFitnessSession();

            // [START read_session]
            // Invoke the Sessions API to fetch the session with the query and wait for the result
            // of the read request. Note: Fitness.SessionsApi.readSession() requires the
            // ACCESS_FINE_LOCATION permission.

            SessionReadResult sessionReadResult =
                    Fitness.SessionsApi.readSession(mClient, readRequest)
                            .await(1, TimeUnit.MINUTES);

            // Get a list of the sessions that match the criteria to check the result.
            Log.i(TAG, "Session read was successful. Number of returned sessions is: "
                    + sessionReadResult.getSessions().size());

            for (Session session : sessionReadResult.getSessions()) {
                // Process the session
                dumpSession(session);

                // Process the data sets for this session
                List<DataSet> dataSets = sessionReadResult.getDataSet(session);
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
            }
            // [END read_session]

            return null;
        }
    }

    private SessionInsertRequest insertFitnessSession() {
//        Log.i(TAG, "Creating a new session for an afternoon run");
//        // Setting start and end times for our run.
//        Calendar cal = Calendar.getInstance();
//        Date now = new Date();
//        cal.setTime(now);
//        // Set a range of the run, using a start time of 30 minutes before this moment,
//        // with a 10-minute walk in the middle.
//        long endTime = cal.getTimeInMillis();
//        cal.add(Calendar.MINUTE, -10);
//        long endWalkTime = cal.getTimeInMillis();
//        cal.add(Calendar.MINUTE, -10);
//        long startWalkTime = cal.getTimeInMillis();
//        cal.add(Calendar.MINUTE, -10);
//        long startTime = cal.getTimeInMillis();
//
//        // Create a data source
//        DataSource speedDataSource = new DataSource.Builder()
//                .setAppPackageName(this.getPackageName())
//                .setDataType(DataType.TYPE_SPEED)
//                .setName(SAMPLE_SESSION_NAME + "- speed")
//                .setType(DataSource.TYPE_RAW)
//                .build();
//
//        float runSpeedMps = 10;
//        float walkSpeedMps = 3;
//        // Create a data set of the run speeds to include in the session.
//        DataSet speedDataSet = DataSet.create(speedDataSource);
//
//        DataPoint firstRunSpeed = speedDataSet.createDataPoint()
//                .setTimeInterval(startTime, startWalkTime, TimeUnit.MILLISECONDS);
//        firstRunSpeed.getValue(Field.FIELD_SPEED).setFloat(runSpeedMps);
//        speedDataSet.add(firstRunSpeed);
//
//        DataPoint walkSpeed = speedDataSet.createDataPoint()
//                .setTimeInterval(startWalkTime, endWalkTime, TimeUnit.MILLISECONDS);
//        walkSpeed.getValue(Field.FIELD_SPEED).setFloat(walkSpeedMps);
//        speedDataSet.add(walkSpeed);
//
//        DataPoint secondRunSpeed = speedDataSet.createDataPoint()
//                .setTimeInterval(endWalkTime, endTime, TimeUnit.MILLISECONDS);
//        secondRunSpeed.getValue(Field.FIELD_SPEED).setFloat(runSpeedMps);
//        speedDataSet.add(secondRunSpeed);
//
//        // [START build_insert_session_request_with_activity_segments]
//        // Create a second DataSet of ActivitySegments to indicate the runner took a 10-minute walk
//        // in the middle of the run.
//        DataSource activitySegmentDataSource = new DataSource.Builder()
//                .setAppPackageName(this.getPackageName())
//                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
//                .setName(SAMPLE_SESSION_NAME + "-activity segments")
//                .setType(DataSource.TYPE_RAW)
//                .build();
//        DataSet activitySegments = DataSet.create(activitySegmentDataSource);
//
//        DataPoint firstRunningDp = activitySegments.createDataPoint()
//                .setTimeInterval(startTime, startWalkTime, TimeUnit.MILLISECONDS);
//        firstRunningDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.RUNNING);
//        activitySegments.add(firstRunningDp);
//
//        DataPoint walkingDp = activitySegments.createDataPoint()
//                .setTimeInterval(startWalkTime, endWalkTime, TimeUnit.MILLISECONDS);
//        walkingDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.WALKING);
//        activitySegments.add(walkingDp);
//
//        DataPoint secondRunningDp = activitySegments.createDataPoint()
//                .setTimeInterval(endWalkTime, endTime, TimeUnit.MILLISECONDS);
//        secondRunningDp.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.RUNNING);
//        activitySegments.add(secondRunningDp);
//
//        // [START build_insert_session_request]
//        // Create a session with metadata about the activity.
//        Session session = new Session.Builder()
//                .setName(SAMPLE_SESSION_NAME)
//                .setDescription("Long run around Shoreline Park")
//                .setIdentifier("UniqueIdentifierHere")
//                .setActivity(FitnessActivities.RUNNING)
//                .setStartTime(startTime, TimeUnit.MILLISECONDS)
//                .setEndTime(endTime, TimeUnit.MILLISECONDS)
//                .build();
//
//
//        // Build a session insert request
//        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
//                .setSession(session)
//                .addDataSet(speedDataSet)
//                .addDataSet(activitySegments)
//                .build();
//        // [END build_insert_session_request]
//        // [END build_insert_session_request_with_activity_segments]
//
//        return insertRequest;
        return null;
    }

    private DataSet insertFitnessData(){
//        Log.i(TAG, "Creating a new data insert request.");
//
//        Calendar cal = Calendar.getInstance();
//        Date now = new Date();
//        cal.setTime(now);
//        long endTime = cal.getTimeInMillis();
//        cal.add(Calendar.HOUR_OF_DAY, -1);
//        long startTime = cal.getTimeInMillis();
//
//        DataSource dataSource = new DataSource.Builder()
//                .setAppPackageName(this)
//                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
//                .setStreamName(TAG + "-step count")
//                .setType(DataSource.TYPE_RAW)
//                .build();
//
//        int stepCountDelta = 950;
//        DataSet dataSet = DataSet.create(dataSource);
//
//        DataPoint dataPoint = dataSet.createDataPoint()
//                .setTimeInterval(startTime,endTime,TimeUnit.MILLISECONDS);
//        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
//
//        dataSet.add(dataPoint);
//
//        return dataSet;
        return null;

    }

    public static DataReadRequest queryFitnessData(){
//        Calendar cal = Calendar.getInstance();
//        Date now = new Date();
//        cal.setTime(now);
//        long endTime = cal.getTimeInMillis();
//        cal.add(Calendar.WEEK_OF_YEAR,-1);
//        long startTime = cal.getTimeInMillis();
//
//        java.text.DateFormat dateFormat = DateFormat.getDateInstance();
//        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
//        Log.i(TAG, "Range End: " + dateFormat.format(endTime));
//
//        DataReadRequest readRequest = new DataReadRequest.Builder()
//                .aggregate(DataType.TYPE_STEP_COUNT_DELTA,DataType.AGGREGATE_STEP_COUNT_DELTA)
//                .bucketByTime(1,TimeUnit.DAYS)
//                .setTimeRange(startTime,endTime,TimeUnit.MILLISECONDS)
//                .build();
//
//        return readRequest;
        return null;

    }

    public static void printData(DataReadResult dataReadResult){
        if(dataReadResult.getBuckets().size() >0){
            Log.i(TAG,"Number of returned buckets of Dataset is: "
            + dataReadResult.getBuckets().size());
            for(Bucket bucket:dataReadResult.getBuckets()){
                List<DataSet> dataSets = bucket.getDataSets();
                for(DataSet dataSet:dataSets){
                    dumpDataSet(dataSet);
                }
            }
        }else if
            (dataReadResult.getDataSets().size() >0){
            Log.i(TAG,"Number of returned DataSets is: "+dataReadResult.getDataSets().size());
            for(DataSet dataSet: dataReadResult.getDataSets()){
                dumpDataSet(dataSet);
            }
        }
        }
    private SessionReadRequest readFitnessSession() {
        Log.i(TAG, "Reading History API results for session: " + SAMPLE_SESSION_NAME);
        // [START build_read_session_request]
        // Set a start and end time for our query, using a start time of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        // Build a session read request
        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_SPEED)
                .setSessionName(SAMPLE_SESSION_NAME)
                .build();
        // [END build_read_session_request]

        return readRequest;
    }

    private static void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        for (DataPoint dp : dataSet.getDataPoints()) {
            DateFormat dateFormat = DateFormat.getTimeInstance();
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            }
        }
    }

    private void deleteSession() {
        Log.i(TAG, "Deleting today's session data for speed");

        // Set a start and end time for our data, using a start time of 1 day before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        // Create a delete request object, providing a data type and a time interval
        DataDeleteRequest request = new DataDeleteRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .deleteAllSessions() // Or specify a particular session here
                .build();

        // Invoke the History API with the Google API client object and the delete request and
        // specify a callback that will check the result.
        Fitness.HistoryApi.deleteData(mClient, request)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Successfully deleted today's sessions");
                        } else {
                            // The deletion will fail if the requesting app tries to delete data
                            // that it did not insert.
                            Log.i(TAG, "Failed to delete today's sessions");
                        }
                    }
                });
    }

    private void dumpSession(Session session) {
        DateFormat dateFormat = DateFormat.getTimeInstance();
        Log.i(TAG, "Data returned for Session: " + session.getName()
                + "\n\tDescription: " + session.getDescription()
                + "\n\tStart: " + dateFormat.format(session.getStartTime(TimeUnit.MILLISECONDS))
                + "\n\tEnd: " + dateFormat.format(session.getEndTime(TimeUnit.MILLISECONDS)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // This ensures that if the user denies the permissions then uses Settings to re-enable
        // them, the app will start working.
        buildFitnessClient();


    }
    // [END auth_oncreate_setup]

    // [START auth_build_googleapiclient_beginning]
    /**
     *  Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     *  to connect to Fitness APIs. The scopes included should match the scopes your app needs
     *  (see documentation for details). Authentication will occasionally fail intentionally,
     *  and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     *  can address. Examples of this include the user never having signed in before, or having
     *  multiple accounts on the device and needing to specify which account to use, etc.
     */
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
                                public void onConnected(Bundle bundle) {
                                    Log.i(TAG, "Connected!!!");
                                    // Now you can make calls to the Fitness APIs.
//                                    findFitnessDataSources();
//                                    subscribe();
//                                    new InsertAndVerifySessionTask().execute();
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    // If your connection to the sensor gets lost at some point,
                                    // you'll be able to determine the reason and react to it here.
                                    if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                    } else if (i
                                            == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        Log.i(TAG,
                                                "Connection lost.  Reason: Service Disconnected");
                                    }
                                }
                            }
                    )
                    .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            Log.i(TAG, "Google Play services connection failed. Cause: " +
                                    result.toString());
                            Snackbar.make(
                                    MainActivity.this.findViewById(R.id.main_activity_view),
                                    "Exception while connecting to Google Play services: " +
                                            result.getErrorMessage(),
                                    Snackbar.LENGTH_INDEFINITE).show();
                        }
                    })
                    .build();
        }

    }
    // [END auth_build_googleapiclient_beginning]

    /**
     * Find available data sources and attempt to register on a specific {@link DataType}.
     * If the application cares about a data type but doesn't care about the source of the data,
     * this can be skipped entirely, instead calling
     *     {@link com.google.android.gms.fitness.SensorsApi
     *     #register(GoogleApiClient, SensorRequest, DataSourceListener)},
     * where the {@link SensorRequest} contains the desired data type.
     */



    private void findFitnessDataSources() {
        // [START find_data_sources]
        // Note: Fitness.SensorsApi.findDataSources() requires the ACCESS_FINE_LOCATION permission.
//        Fitness.SensorsApi.findDataSources(mClient, new DataSourcesRequest.Builder()
//                .setDataTypes(DataType.TYPE_STEP_COUNT_CADENCE)
//                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
//                .setDataTypes(DataType.TYPE_STEP_COUNT_DELTA)
//                        // Can specify whether data type is raw or derived.
//                .setDataSourceTypes(DataSource.TYPE_DERIVED)
//                .build())
//                .setResultCallback(new ResultCallback<DataSourcesResult>() {
//                    @Override
//                    public void onResult(DataSourcesResult dataSourcesResult) {
//                        Log.i(TAG, "Result: " + dataSourcesResult.getStatus().toString());
//                        for (DataSource dataSource : dataSourcesResult.getDataSources()) {
//                            Log.i(TAG, "Data source found: " + dataSource.toString());
//                            Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());
//
//                            //Let's register a listener to receive Activity data!
//                            if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_CADENCE) && mListener == null) {
//                                Log.i(TAG, "Data source for TYPE_STEP_COUNT_CADENCE found!  Registering.");
//                                registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CADENCE);
//                            } else if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_CUMULATIVE) && mListener == null) {
//                                Log.i(TAG, "Data source for TYPE_STEP_COUNT_CUMULATIVE found!  Registering.");
//                                registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
//                            }  else if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA) && mListener == null) {
//
//                                Log.i(TAG, "Data source for TYPE_STEP_COUNT_DELTA found!  Registering.");
//                                registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_DELTA);
//
//                            }   //END STEP COUNT DELTA
//                        }
//                    }
//                });

    }





    /**
     * Register a listener with the Sensors API for the provided {@link DataSource} and
     * {@link DataType} combo.
     */
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        // [START register_data_listener]
//        mListener = new OnDataPointListener() {
//            @Override
//            public void onDataPoint(DataPoint dataPoint) {
//                for (Field field : dataPoint.getDataType().getFields()) {
//                    Value val = dataPoint.getValue(field);
//                    Log.i(TAG, "Detected DataPoint field: " + field.getName());
//                    Log.i(TAG, "Detected DataPoint value: " + val);
//                }
//            }
//        };

//        Fitness.SensorsApi.add(
//                mClient,
//                new SensorRequest.Builder()
//                        .setDataSource(dataSource) // Optional but recommended for custom data sets.
//                        .setDataType(dataType) // Can't be omitted.
//                        .setSamplingRate(10, TimeUnit.SECONDS)
//                        .build(),
//                mListener)
//                .setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        if (status.isSuccess()) {
//                            Log.i(TAG, "Listener registered!");
//                        } else {
//                            Log.i(TAG, "Listener not registered.");
//                        }
//                    }
//                });
        // [END register_data_listener]
    }

    /**
     * Unregister the listener with the Sensors API.
     */
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
                            Log.i(TAG, "Listener was removed!");
                        } else {
                            Log.i(TAG, "Listener was not removed.");
                        }
                    }
                });
        // [END unregister_data_listener]
    }

    private void deleteData(){
//        Log.i(TAG, "Deleting today's step count data.");
//
//        // [START delete_dataset]
//        // Set a start and end time for our data, using a start time of 1 day before this moment.
//        Calendar cal = Calendar.getInstance();
//        Date now = new Date();
//        cal.setTime(now);
//        long endTime = cal.getTimeInMillis();
//        cal.add(Calendar.DAY_OF_YEAR, -1);
//        long startTime = cal.getTimeInMillis();
//
//        //  Create a delete request object, providing a data type and a time interval
//        DataDeleteRequest request = new DataDeleteRequest.Builder()
//                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
//                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
//                .build();
//
//        // Invoke the History API with the Google API client object and delete request, and then
//        // specify a callback that will check the result.
//        Fitness.HistoryApi.deleteData(mClient, request)
//                .setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        if (status.isSuccess()) {
//                            Log.i(TAG, "Successfully deleted today's step count data.");
//                        } else {
//                            // The deletion will fail if the requesting app tries to delete data
//                            // that it did not insert.
//                            Log.i(TAG, "Failed to delete today's step count data.");
//                        }
//                    }
//                });
        // [END delete_dataset]
    }

    private void dumpSubscriptionsList() {
        // [START list_current_subscriptions]
        Fitness.RecordingApi.listSubscriptions(mClient, DataType.TYPE_STEP_COUNT_DELTA)
                // Create the callback to retrieve the list of subscriptions asynchronously.
                .setResultCallback(new ResultCallback<ListSubscriptionsResult>() {
                    @Override
                    public void onResult(ListSubscriptionsResult listSubscriptionsResult) {
                        for (Subscription sc : listSubscriptionsResult.getSubscriptions()) {
                            DataType dt = sc.getDataType();
                            Log.i(TAG, "Active subscription for data type: " + dt.getName());
                        }
                    }
                });
        // [END list_current_subscriptions]
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_unregister_listener) {
            unregisterFitnessDataListener(mClient,walkListener);
            return true;
        }
        if(id == R.id.action_update_data){
            Intent intent = new Intent(MainActivity.this,MainActivity2.class);
            MainActivity.this.startActivity(intent);
        }

        if(id== R.id.action_delete_data){
            deleteData();
            return true;
        }
        if (id == R.id.action_delete_session) {
            deleteSession();
            return true;
        }

        if (id == R.id.action_cancel_subs) {
            cancelSubscription(mClient,DataType.AGGREGATE_STEP_COUNT_DELTA);
            cancelSubscription(mClient,DataType.AGGREGATE_DISTANCE_DELTA);
            return true;
        }

        if (id == R.id.action_dump_subs) {
            dumpSubscriptionsList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     *  Initialize a custom log class that outputs both to in-app targets and logcat.
     */
    private void initializeLogging() {
        // Wraps Android's native log framework.
        LogWrapper logWrapper = new LogWrapper();
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper);
        // Filter strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);
        // On screen logging via a customized TextView.
        LogView logView = (LogView) findViewById(R.id.sample_logview);

        // Fixing this lint errors adds logic without benefit.
        //noinspection AndroidLintDeprecation
        logView.setTextAppearance(this, R.style.Log);

        logView.setBackgroundColor(Color.WHITE);
        msgFilter.setNext(logView);
        Log.i(TAG, "Ready");
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
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
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.main_activity_view),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
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
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
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
        Toast.makeText( MainActivity.this , "Connected2",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
