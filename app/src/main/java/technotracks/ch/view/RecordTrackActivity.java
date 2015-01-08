package technotracks.ch.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import technotracks.ch.R;
import technotracks.ch.common.Session;
import technotracks.ch.constant.NoGPSDialog;
import technotracks.ch.gps.GpsLoggingService;
import technotracks.ch.gps.IGpsLoggingServiceClient;
import technotracks.ch.view.component.ToggleComponent;

public class RecordTrackActivity extends BaseActivity implements View.OnClickListener, IGpsLoggingServiceClient {

    private static Intent serviceIntent;
    private GpsLoggingService loggingService;

    private ToggleComponent toggleComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_track);

        String[] navMenuTitles = getResources().getStringArray(
                R.array.nav_drawer_items);

        TypedArray navMenuIcons = getResources().obtainTypedArray(
                R.array.nav_drawer_icons); // load icons from strings.xml

        set(navMenuTitles, navMenuIcons);

        setImageTooltips();

        if (!((LocationManager) getSystemService(LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)){
            NoGPSDialog.showNoGPSDialog(this);
            Session.setGpsEnabled(false);
        }else
            Session.setGpsEnabled(true);

        // Toggle the play and pause.
        toggleComponent = ToggleComponent.getBuilder()
                .addOnView(findViewById(R.id.simple_play))
                .addOffView(findViewById(R.id.simple_stop))
                .setDefaultState(!Session.isStarted())
                .addHandler(new ToggleComponent.ToggleHandler() {
                    @Override
                    public void onStatusChange(boolean status) {
                        if (status) {
                            loggingService.StartLogging();

                        } else {
                            loggingService.StopLogging();
                        }
                    }
                })
                .build();

        if (Session.hasValidLocation()) {
            SetLocation(Session.getCurrentLocationInfo());
        }

        StartAndBindService();
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


            txtAccuracy.setTextColor(getResources().getColor(android.R.color.black));

            if (accuracy > 10)
                txtAccuracy.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));

            if (accuracy > 20)
                txtAccuracy.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

        }

        if (locationInfo.hasAltitude()) {

            TextView txtAltitude = (TextView) findViewById(R.id.simpleview_txtAltitude);
            txtAltitude.setText(nf.format(locationInfo.getAltitude()) + getString(R.string.meters));

        }

        if (locationInfo.hasSpeed()) {
            System.out.println(locationInfo.getSpeed());
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

        } else {
            TextView txtSpeed = (TextView) findViewById(R.id.simpleview_txtSpeed);
            txtSpeed.setText("-");
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
        txtPoints.setText(Session.getNumPoints() + " " + getString(R.string.points));

        String providerName = locationInfo.getProvider();
        TextView txtSatelliteCount = (TextView) findViewById(R.id.simpleview_txtSatelliteCount);
        if (!providerName.equalsIgnoreCase("gps")) {
            txtSatelliteCount.setText("-");
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

        if (serviceIntent == null) {
            serviceIntent = new Intent(this, GpsLoggingService.class);
            // Start the service in case it isn't already running
            startService(serviceIntent);
        }
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
    public void OnFatalMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnLocationUpdate(Location location) {
        // Report to the UI that the location was updated
        System.out.println("MainForm.Location received");
        SetLocation(location);
    }

    @Override
    public void OnSatelliteCount() {
        TextView txtSatelliteCount = (TextView) findViewById(R.id.simpleview_txtSatelliteCount);
        txtSatelliteCount.setText(String.valueOf(Session.getSatelliteCount()));
    }

    @Override
    public void OnStartLogging() {
        Toast.makeText(this, "Started Logging", Toast.LENGTH_SHORT).show();

        Session.resetSession();
        EditText txtLatitude = (EditText) findViewById(R.id.simple_lat_text);
        txtLatitude.setText(" ");
        EditText txtLongitude = (EditText) findViewById(R.id.simple_lon_text);
        txtLongitude.setText(" ");

        TextView txtSatelliteCount = (TextView) findViewById(R.id.simpleview_txtSatelliteCount);
        txtSatelliteCount.setText("-");
        TextView txtAccuracy = (TextView) findViewById(R.id.simpleview_txtAccuracy);
        txtAccuracy.setText("-");

        TextView txtAltitude = (TextView) findViewById(R.id.simpleview_txtAltitude);
        txtAltitude.setText("-");
        ImageView imgDirection = (ImageView) findViewById(R.id.simpleview_imgDirection);
        imgDirection.setRotation(0);
        TextView txtDirection = (TextView) findViewById(R.id.simpleview_txtDirection);
        txtDirection.setText("-");

        TextView txtDuration = (TextView) findViewById(R.id.simpleview_txtDuration);
        txtDuration.setText("-");
        TextView txtSpeed = (TextView) findViewById(R.id.simpleview_txtSpeed);
        txtSpeed.setText("-");

        TextView txtTravelled = (TextView) findViewById(R.id.simpleview_txtDistance);
        txtTravelled.setText("-");
        TextView txtPoints = (TextView) findViewById(R.id.simpleview_txtPoints);
        txtPoints.setText("-");

    }

    @Override
    public void OnStopLogging() {
        Toast.makeText(this, "Stopped Logging", Toast.LENGTH_SHORT).show();

        TextView txtSatelliteCount = (TextView) findViewById(R.id.simpleview_txtSatelliteCount);
        txtSatelliteCount.setText("-");
        TextView txtAccuracy = (TextView) findViewById(R.id.simpleview_txtAccuracy);
        txtAccuracy.setText("-");
        ImageView imgDirection = (ImageView) findViewById(R.id.simpleview_imgDirection);
        imgDirection.setRotation(0);
        TextView txtDirection = (TextView) findViewById(R.id.simpleview_txtDirection);
        txtDirection.setText("-");
        TextView txtSpeed = (TextView) findViewById(R.id.simpleview_txtSpeed);
        txtSpeed.setText("0 " + getString(R.string.meters_per_second));
    }

    @Override
    public void OnWaitingForLocation(boolean inProgress) {
        ProgressBar spinner;
        spinner = (ProgressBar)findViewById(R.id.progressWait);
        if (inProgress)
            spinner.setVisibility(View.VISIBLE);
        else
            spinner.setVisibility(View.GONE);

    }

}
