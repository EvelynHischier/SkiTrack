package technotracks.ch.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.util.DateTime;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.technotracks.backend.gPSDataApi.model.GPSData;
import ch.technotracks.backend.trackApi.model.Track;
import technotracks.ch.R;
import technotracks.ch.common.Session;
import technotracks.ch.constant.Constant;
import technotracks.ch.constant.NoGPSDialog;
import technotracks.ch.database.DatabaseAccess;
import technotracks.ch.gps.GpsLoggingService;
import technotracks.ch.gps.IGpsLoggingServiceClient;
import technotracks.ch.view.component.ToggleComponent;

public class RecordTrackActivity extends BaseActivity implements View.OnClickListener, IGpsLoggingServiceClient {
        //GoogleApiClient.ConnectionCallbacks,
        //GoogleApiClient.OnConnectionFailedListener, LocationListener,View.OnClickListener, {

    private static Intent serviceIntent;
    private GpsLoggingService loggingService;

    private final String STATE_RECORDING = "Recording";
    private final String STATE_CURRENT_TRACK = "Track";
    private final String STATE_POINTS = "Points";
    private int satelliteNumber;
    private Track currentTrack;
    private List<GPSData> points;

    private GoogleApiClient mLocationClient;
    private LocationRequest mLocationRequest;
    private boolean mUpdatesRequested;

    private ToggleComponent toggleComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mUpdatesRequested = savedInstanceState.getBoolean(STATE_RECORDING);
            // TODO
            //currentTrack = (Track) savedInstanceState.getSerializable(STATE_CURRENT_TRACK);

            points = (ArrayList<GPSData>) savedInstanceState
                    .getSerializable(STATE_POINTS);
        } else {
            mUpdatesRequested = false;
            savedInstanceState = new Bundle();
            savedInstanceState.putBoolean(STATE_RECORDING, mUpdatesRequested);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_track);

        String[] navMenuTitles = getResources().getStringArray(
                R.array.nav_drawer_items);

        TypedArray navMenuIcons = getResources().obtainTypedArray(
                R.array.nav_drawer_icons); // load icons from strings.xml

        set(navMenuTitles, navMenuIcons);

        setImageTooltips();

        if(!((LocationManager)getSystemService(LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            NoGPSDialog.showNoGPSDialog(this);
        }

        // Toggle the play and pause.
        toggleComponent = ToggleComponent.getBuilder()
                .addOnView(findViewById(R.id.simple_play))
                .addOffView(findViewById(R.id.simple_stop))
                .setDefaultState(!Session.isStarted())
                .addHandler(new ToggleComponent.ToggleHandler() {
                    @Override
                    public void onStatusChange(boolean status) {
                        if (status) {
                            //requestStartLogging();
                            //startCapture();
                            loggingService.StartLogging();

                        } else {
                            loggingService.StopLogging();
//                            stopCapture();
//                            requestStopLogging();
                        }
                    }
                })
                .build();

        if (Session.hasValidLocation()) {
            SetLocation(Session.getCurrentLocationInfo());
        }

        StartAndBindService();

//        /*
//		 * Create a new location client, using the enclosing class to handle
//		 * callbacks.
//		 */
//        mLocationClient = new GoogleApiClient.Builder(this)
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//
//        mLocationRequest = LocationRequest.create();
//        // Use high accuracy
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        // Set the update interval to 5 seconds
//        mLocationRequest.setInterval(Constant.UPDATE_INTERVAL);
//        // Set the fastest update interval to 1 second
//        mLocationRequest.setFastestInterval(Constant.FASTEST_INTERVAL);


    }

    @Override
    protected void onStart() {
        super.onStart();
        StartAndBindService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StartAndBindService();
    }

    @Override
    protected void onPause() {
        StopAndUnbindServiceIfRequired();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        StopAndUnbindServiceIfRequired();
        super.onDestroy();

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_RECORDING, mUpdatesRequested);
        // TODO COMMEEEENNNNNNNT
        // outState.putSerializable(STATE_CURRENT_TRACK, currentTrack);
        outState.putSerializable(STATE_POINTS, (Serializable) points);
        super.onSaveInstanceState(outState);
    }

//    @Override
//    protected void onStart() {
//        if (mUpdatesRequested)
//            startCapture();
//
//        super.onStart();
//    }


//    /**
//     * Stop capturing and upload data if possible
//     */
//    private void stopCapture() {
//        System.out.println("STOP");
//        mUpdatesRequested = false;
//        Session.setStarted(false);
//        // If the client is connected
//        if (mLocationClient.isConnected()) {
//			/*
//			 * Remove location updates for a listener. The current Activity is
//			 * the listener, so the argument is "this".
//			 */
//            // mLocationClient.removeLocationUpdates(this);
//            Session.setCurrentLocationInfo(null);
//            LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient,this);
//
//            /*
//             * After disconnect() is called, the client is considered "dead".
//             */
//            mLocationClient.disconnect();
//        }
//
//
//    }

//    /**
//     * Start capturing data
//     */
//    private void startCapture() {
//        mUpdatesRequested = true;
//        if (Session.isStarted())
//            return;
//
//        // Connect the client.
//
//        mLocationClient.connect();
//        Session.setStarted(true);
//    }
//
//    public int getSatelliteNumber() {
//        return satelliteNumber;
//    }
//
//    /*
//     * Called when the Activity is no longer visible at all. Stop updates and
//     * disconnect.
//     */
//    @Override
//    protected void onStop() {
//        // stopCapture();
//
//        super.onStop();
//    }

//    @Override
//    public void onLocationChanged(Location location) {
//        // Report to the UI that the location was updated
//        GPSData point = new GPSData();
//
//        point.setLatitude(location.getLatitude());
//        point.setLongitude(location.getLongitude());
//        point.setAltitude(location.getAltitude());
//        point.setSatellites(getSatelliteNumber());
//        point.setAccuracy(location.getAccuracy());
//        point.setTimestamp(new DateTime(location.getTime()));
//        point.setSpeed(location.getSpeed());
//        point.setBearing(location.getBearing());
//
//        // if the track is not stored yet (asynchronus)
//        // use -> getIdTrack
//        point.setTrackID(currentTrack.getId() == null ? DatabaseAccess.getIdTrack(): this.currentTrack.getId());
//        points.add(point);
//        SetLocation(location);
//        DatabaseAccess.writeGPSData(this, point);
//    }
//
//    /*
//     * Called by Location Services if the attempt to Location Services fails.
//     */
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//		/*
//		 * Google Play services can resolve some errors it detects. If the error
//		 * has a resolution, try sending an Intent to start a Google Play
//		 * services activity that can resolve error.
//		 */
//        if (connectionResult.hasResolution()) {
//            try {
//                // Start an Activity that tries to resolve the error
//                connectionResult.startResolutionForResult(this,
//                        BaseActivity.CONNECTION_FAILURE_RESOLUTION_REQUEST);
//				/*
//				 * Thrown if Google Play services canceled the original
//				 * PendingIntent
//				 */
//            } catch (IntentSender.SendIntentException e) {
//                // Log the error
//                e.printStackTrace();
//            }
//        } else {
//			/*
//			 * If no resolution is available, display a dialog to the user with
//			 * the error.
//			 */
//            // showErrorDialog(connectionResult.getErrorCode());
//            System.out.println(connectionResult.getErrorCode());
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public void onConnected(Bundle dataBundle) {
//        // Display the connection status
//
//        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
//
//        if (mUpdatesRequested) {
//            if (currentTrack == null) {
//                currentTrack = new Track();
//                currentTrack.setName("testTrack");
//                currentTrack.setCreate(new DateTime(new Date()));
//                currentTrack.setId(DatabaseAccess
//                        .writeTrack(this, currentTrack));
//            }
//
//            if (points == null)
//                points = new ArrayList<GPSData>();
//
//            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient,mLocationRequest,this);
//        }
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//        // TODO yolo
//    }

    public void SetLocation(Location locationInfo) {

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(6);

        EditText txtLatitude = (EditText) findViewById(R.id.simple_lat_text);
        txtLatitude.setText(String.valueOf(nf.format(locationInfo.getLatitude())));

        EditText txtLongitude = (EditText) findViewById(R.id.simple_lon_text);
        txtLongitude.setText(String.valueOf(nf.format(locationInfo.getLongitude())));

        nf.setMaximumFractionDigits(3);

        if (locationInfo.hasAccuracy()) {

            TextView txtAccuracy = (TextView) findViewById(R.id.simpleview_txtAccuracy);
            float accuracy = locationInfo.getAccuracy();

            txtAccuracy.setText(nf.format(accuracy) + getString(R.string.meters));

            if (accuracy > 500) {
                txtAccuracy.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            }

            if (accuracy > 900) {
                txtAccuracy.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                txtAccuracy.setTextColor(getResources().getColor(android.R.color.black));
            }

        }

        if (locationInfo.hasAltitude()) {

            TextView txtAltitude = (TextView) findViewById(R.id.simpleview_txtAltitude);
            txtAltitude.setText(nf.format(locationInfo.getAltitude()) + getString(R.string.meters));

        }

        if (locationInfo.hasSpeed()) {

            float speed = locationInfo.getSpeed();
            String unit;

            if (speed > 0.277) {
                speed = speed * 3.6f;
                unit = getString(R.string.kilometers_per_hour);
            } else {
                unit = getString(R.string.meters_per_second);
            }

            TextView txtSpeed = (TextView) findViewById(R.id.simpleview_txtSpeed);
            txtSpeed.setText(String.valueOf(nf.format(speed)) + unit);

        }

        if (locationInfo.hasBearing()) {

            ImageView imgDirection = (ImageView) findViewById(R.id.simpleview_imgDirection);
            imgDirection.setRotation(locationInfo.getBearing());

            TextView txtDirection = (TextView) findViewById(R.id.simpleview_txtDirection);
            txtDirection.setText(String.valueOf(Math.round(locationInfo.getBearing())) + getString(R.string.degree_symbol));

        }

        TextView txtDuration = (TextView) findViewById(R.id.simpleview_txtDuration);

        long startTime = Session.getStartTimeStamp();
        long currentTime = System.currentTimeMillis();
        String duration = getInterval(startTime, currentTime);

        txtDuration.setText(duration);

        String distanceUnit;

        double distanceValue = Session.getTotalTravelled();
        distanceUnit = getString(R.string.meters);
        if (distanceValue > 1000) {
            distanceUnit = getString(R.string.kilometers);
            distanceValue = distanceValue / 1000;
        }

        TextView txtPoints = (TextView) findViewById(R.id.simpleview_txtPoints);
        TextView txtTravelled = (TextView) findViewById(R.id.simpleview_txtDistance);

        nf.setMaximumFractionDigits(1);
        txtTravelled.setText(nf.format(distanceValue) + " " + distanceUnit);
        txtPoints.setText(Session.getNumLegs() + " " + getString(R.string.points));

        String providerName = locationInfo.getProvider();
        TextView txtSatelliteCount = (TextView) findViewById(R.id.simpleview_txtSatelliteCount);
        if (!providerName.equalsIgnoreCase("gps")) {
            txtSatelliteCount.setText("-");
        } else{
            //txtSatelliteCount.setText(String.valueOf(getSatelliteNumber()));
        }

    }

    private void setImageTooltips() {
        ImageView imgSatellites = (ImageView) findViewById(R.id.simpleview_imgSatelliteCount);
        imgSatellites.setOnClickListener(this);

        TextView txtAccuracyIcon = (TextView) findViewById(R.id.simpleview_txtAccuracyIcon);
        txtAccuracyIcon.setOnClickListener(this);

        ImageView imgElevation = (ImageView) findViewById(R.id.simpleview_imgAltitude);
        imgElevation.setOnClickListener(this);

        ImageView imgBearing = (ImageView) findViewById(R.id.simpleview_imgDirection);
        imgBearing.setOnClickListener(this);

        ImageView imgDuration = (ImageView) findViewById(R.id.simpleview_imgDuration);
        imgDuration.setOnClickListener(this);

        ImageView imgSpeed = (ImageView) findViewById(R.id.simpleview_imgSpeed);
        imgSpeed.setOnClickListener(this);

        ImageView imgDistance = (ImageView) findViewById(R.id.simpleview_distance);
        imgDistance.setOnClickListener(this);

        ImageView imgPoints = (ImageView) findViewById(R.id.simpleview_points);
        imgPoints.setOnClickListener(this);

    }

    private String getInterval(long startTime, long endTime) {
        StringBuffer sb = new StringBuffer();
        long diff = endTime - startTime;
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        if (diffDays > 0) {
            sb.append(diffDays + " days ");
        }
        if (diffHours > 0) {
            sb.append(String.format("%02d", diffHours) + ":");
        }
        sb.append(String.format("%02d", diffMinutes) + ":");
        sb.append(String.format("%02d", diffSeconds));
        return sb.toString();
    }

    @Override
    public void onClick(View view) {
        Toast toast = null;
        switch (view.getId()) {
            case R.id.simpleview_imgSatelliteCount:
                toast = Toast.makeText(this, R.string.txt_satellites, Toast.LENGTH_SHORT);
                break;
            case R.id.simpleview_txtAccuracyIcon:
                toast = Toast.makeText(this, R.string.txt_accuracy, Toast.LENGTH_SHORT);
                break;

            case R.id.simpleview_imgAltitude:
                toast = Toast.makeText(this, R.string.txt_altitude, Toast.LENGTH_SHORT);
                break;

            case R.id.simpleview_imgDirection:
                toast = Toast.makeText(this, R.string.txt_direction, Toast.LENGTH_SHORT);
                break;

            case R.id.simpleview_imgDuration:
                toast = Toast.makeText(this, R.string.txt_travel_duration, Toast.LENGTH_SHORT);
                break;

            case R.id.simpleview_imgSpeed:
                toast = Toast.makeText(this, R.string.txt_speed, Toast.LENGTH_SHORT);
                break;

            case R.id.simpleview_distance:
                toast = Toast.makeText(this, R.string.txt_travel_distance, Toast.LENGTH_SHORT);
                break;

            case R.id.simpleview_points:
                toast = Toast.makeText(this, R.string.txt_number_of_points, Toast.LENGTH_SHORT);
                break;
        }

        int location[] = new int[2];
        view.getLocationOnScreen(location);

        if (toast != null) {
            toast.setGravity(Gravity.TOP | Gravity.LEFT, location[0], location[1]);
            toast.show();
        }

    }

    /**
     * Provides a connection to the GPS Logging Service
     */
    private final ServiceConnection gpsServiceConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            loggingService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            loggingService = ((GpsLoggingService.GpsLoggingBinder) service).getService();
            GpsLoggingService.SetServiceClient(RecordTrackActivity.this);
        }
    };

    /**
     * Starts the service and binds the activity to it.
     */
    private void StartAndBindService() {

        serviceIntent = new Intent(this, GpsLoggingService.class);
        // Start the service in case it isn't already running
        startService(serviceIntent);
        // Now bind to service
        bindService(serviceIntent, gpsServiceConnection, Context.BIND_AUTO_CREATE);
        Session.setBoundToService(true);
    }

    /**
     * Stops the service if it isn't logging. Also unbinds.
     */
    private void StopAndUnbindServiceIfRequired() {

        if (Session.isBoundToService()) {

            try {
                unbindService(gpsServiceConnection);
                Session.setBoundToService(false);
            } catch (Exception e) {
            }
        }

        if (!Session.isStarted()) {
            //serviceIntent = new Intent(this, GpsLoggingService.class);
            try {
                stopService(serviceIntent);
            } catch (Exception e) {
            }

        }

    }

    @Override
    public void OnStatusMessage(String message) {

    }

    @Override
    public void OnFatalMessage(String message) {

    }

    @Override
    public void OnLocationUpdate(Location loc) {

    }

    @Override
    public void OnSatelliteCount(int count) {

    }

    @Override
    public void OnStartLogging() {

    }

    @Override
    public void OnStopLogging() {

    }

    @Override
    public void OnWaitingForLocation(boolean inProgress) {

    }

    /**
     * A custom gps status listener. Update the satellite number
     * @author Joel
     *
     */
//    private class MyGpsStatusListener implements GpsStatus.Listener
//    {
//        /**
//         * Called when the gps status change (typically when the number of satellites change)
//         */
//        @Override
//        public void onGpsStatusChanged(int event)
//        {
//            if(event == GpsStatus.GPS_EVENT_SATELLITE_STATUS)
//            {
//                int currentSatelliteNumber = getSatelliteNumber();
//
//				/* if we can update display we do it
//				   update only if satellite number change */
//                if(currentSatelliteNumber != satelliteNumber)
//                {
//                    satelliteNumber = currentSatelliteNumber;
//                }
//            }
//        }

        /**
         * Give the number of satellite currently locked
         * @return
         * The number of satellites
         */
        /*private int getSatelliteNumber()
        {
            int satNumber = 0;

			/* Count the number of satellites */
/*
            GpsStatus gpsStatus = manager.getGpsStatus(null);
            for (GpsSatellite ignored : gpsStatus.getSatellites())
            {
                satNumber++;
            }

            return gpsStatus.getMaxSatellites();
        }*/
    //}
}
