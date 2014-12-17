package technotracks.ch.skitrack_scrum;

import android.content.IntentSender;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.util.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ch.technotracks.backend.gPSDataApi.model.GPSData;
import ch.technotracks.backend.trackApi.model.Track;
import technotracks.ch.R;
import technotracks.ch.constant.Constant;
import technotracks.ch.database.DatabaseAccess;

public class RecordTrackActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final String STATE_RECORDING = "Recording";
    private final String STATE_CURRENT_TRACK = "Track";
    private final String STATE_POINTS = "Points";
    private int satelliteNumber;
    private Track currentTrack;
    private List<GPSData> points;

    private GoogleApiClient mLocationClient;
    private LocationRequest mLocationRequest;
    private boolean mUpdatesRequested;

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

        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(Constant.UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(Constant.FASTEST_INTERVAL);
		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_RECORDING, mUpdatesRequested);
        // TODO COMMEEEENNNNNNNT
        // outState.putSerializable(STATE_CURRENT_TRACK, currentTrack);
        outState.putSerializable(STATE_POINTS, (Serializable) points);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        if (mUpdatesRequested)
            startCapture();

        setButtonLabel();
        super.onStart();
    }

    public void btnStartStopClicked(View view) {
        if (mUpdatesRequested) {
            stopCapture();
        } else {
            startCapture();
        }
        setButtonLabel();
    }

    private void setButtonLabel() {
        Button btnStart = (Button) findViewById(R.id.btnStartStopRecording);
        btnStart.setText(mUpdatesRequested ? getString(R.string.stop)
                : getString(R.string.start));
    }

    /**
     * Stop capturing and upload data if possible
     */
    private void stopCapture() {
        mUpdatesRequested = false;

        // If the client is connected
        if (mLocationClient.isConnected()) {
			/*
			 * Remove location updates for a listener. The current Activity is
			 * the listener, so the argument is "this".
			 */
            // mLocationClient.removeLocationUpdates(this);
            LocationServices.FusedLocationApi.removeLocationUpdates(mLocationClient,this);
            mLocationClient.disconnect();
        }

		/*
		 * After disconnect() is called, the client is considered "dead".
		 */

    }

    /**
     * Start capturing data
     */
    private void startCapture() {
        mUpdatesRequested = true;

        // Connect the client.
        mLocationClient.connect();

    }

    public int getSatelliteNumber() {
        return satelliteNumber;
    }

    /*
     * Called when the Activity is no longer visible at all. Stop updates and
     * disconnect.
     */
    @Override
    protected void onStop() {
        // stopCapture();

        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        GPSData point = new GPSData();

        point.setLatitude(location.getLatitude());
        point.setLongitude(location.getLongitude());
        point.setAltitude(location.getAltitude());
        point.setSatellites(getSatelliteNumber());
        point.setAccuracy(location.getAccuracy());
        point.setTimestamp(new DateTime(location.getTime()));
        point.setSpeed(location.getSpeed());
        point.setBearing(location.getBearing());
//		point.setTrack(this.currentTrack);
        points.add(point);
        update(point); // update the display
        DatabaseAccess.writeGPSData(this, point);
    }

    private void update(GPSData point) {
        try {

            TextView txtLatitude = (TextView) findViewById(R.id.latitude);
            txtLatitude.setText(Double.toString(point.getLatitude()));
            TextView txtLongitude = (TextView) findViewById(R.id.longitude);
            txtLongitude.setText(Double.toString(point.getLongitude()));
            TextView txtAltitude = (TextView) findViewById(R.id.altitude);
            txtAltitude.setText(Double.toString(point.getAltitude()));
            TextView txtAccuracy = (TextView) findViewById(R.id.accuracy);
            txtAccuracy.setText(Double.toString(point.getAccuracy()));

            TextView txtSatellites = (TextView) findViewById(R.id.satellites);
            txtSatellites.setText(Integer.toString(0));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
		/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        BaseActivity.CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
			/*
			 * If no resolution is available, display a dialog to the user with
			 * the error.
			 */
            // showErrorDialog(connectionResult.getErrorCode());
            System.out.println(connectionResult.getErrorCode());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status

        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        if (mUpdatesRequested) {
            if (currentTrack == null) {
                currentTrack = new Track();
                currentTrack.setName("testTrack");
                currentTrack.setCreate(new DateTime(new Date()));
                currentTrack.setId(DatabaseAccess
                        .writeTrack(this, currentTrack));
            }

            if (points == null)
                points = new ArrayList<GPSData>();
            // TODO kaplan alda
            LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient,mLocationRequest,this);
            //mLocationClient.requestLocationUpdates(mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        // TODO yolo
    }

}
