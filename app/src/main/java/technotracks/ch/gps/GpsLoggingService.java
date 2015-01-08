package technotracks.ch.gps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;

import com.google.api.client.util.DateTime;

import java.util.Date;

import ch.technotracks.backend.gPSDataApi.model.GPSData;
import ch.technotracks.backend.trackApi.model.Track;
import technotracks.ch.R;
import technotracks.ch.common.Session;
import technotracks.ch.common.Utilities;
import technotracks.ch.database.DatabaseAccess;

public class GpsLoggingService extends Service  {


    private static IGpsLoggingServiceClient mainServiceClient;
    private final IBinder binder = new GpsLoggingBinder();

    // ---------------------------------------------------
    // Helpers and managers
    // ---------------------------------------------------
    protected LocationManager gpsLocationManager;
    private MyLocationListener gpsLocationListener;

    /**
     * Sets the activity form for this service. The activity form needs to
     * implement IGpsLoggerServiceClient.
     *
     * @param mainForm The calling client
     */
    public static void SetServiceClient(IGpsLoggingServiceClient mainForm) {
        mainServiceClient = mainForm;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }

    @Override
    public void onCreate() {

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        HandleIntent(intent);
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        mainServiceClient = null;
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        System.out.println("Android is low on memory.");
        super.onLowMemory();
    }

    private void HandleIntent(Intent intent) {
        if (intent != null) {
            if (Session.isStarted())
                StartGpsManager();
        } else {
            // A null intent is passed in if the service has been killed and
            // restarted.
            System.out.println("Service restarted with null intent. Start logging.");
            StartLogging();
        }
    }

    /**
     * Asks the main service client to clear its form.
     */
    private void NotifyClientStarted() {
        if (IsMainFormVisible())
            mainServiceClient.OnStartLogging();
    }

    /**
     * Notifies main form that logging has stopped
     */
    void NotifyClientStopped() {
        if (IsMainFormVisible()) {
            mainServiceClient.OnStopLogging();
        }
    }
    /**
     * Notifies main form that logging has stopped
     */
    void NotifyClientGPSFix() {
        if (IsMainFormVisible()) {
            mainServiceClient.OnWaitingForLocation(false);
        }
    }

    /**
     * Resets the form and starts the GPS Manager
     */
    public void StartLogging() {
        if (Session.isStarted()) {
            System.out.println("Session already started, ignoring");
            return;
        }

        NotifyClientStarted(); //This resets also the Session, so call it first
        Session.setStarted(true);

        StartGpsManager();
    }

    /**
     * Stops logging, removes notification, stops GPS manager, stops email timer
     */
    public void StopLogging() {
        System.out.println("GpsLoggingService.StopLogging");

        Session.setStarted(false);
        Session.setCurrentLocationInfo(null);
        stopForeground(true);
        StopGpsManager();
        NotifyClientStopped();
    }

    /**
     * Starts the location manager. If GPS is enabled on the phone, this is used
     */
    private void StartGpsManager() {
        System.out.println("GpsLoggingService.StartGpsManager");

        if (gpsLocationListener == null)
            gpsLocationListener = new MyLocationListener(this);

        gpsLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (Session.isGpsEnabled()) {
            // gps satellite based
            gpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 0,
                    gpsLocationListener);

            gpsLocationManager.addGpsStatusListener(gpsLocationListener);

            Session.setUsingGps(true);

            createNewTrack();
        } else
            SetFatalMessage(R.string.noGps);

        if (mainServiceClient != null)
            mainServiceClient.OnWaitingForLocation(true);
    }

    /**
     * Stops the location managers
     */
    private void StopGpsManager() {
        System.out.println("GpsLoggingService.StopGpsManager");

        if (gpsLocationListener != null) {
            System.out.println("Removing gpsLocationManager updates");
            gpsLocationManager.removeUpdates(gpsLocationListener);
            gpsLocationManager.removeGpsStatusListener(gpsLocationListener);
            Session.setUsingGps(false);
        }


        if (mainServiceClient != null)
            mainServiceClient.OnWaitingForLocation(false);
    }

    /**
     * Gives an error message to the main service client to display
     *
     * @param messageId ID of string to lookup
     */
    void SetFatalMessage(int messageId) {
        System.out.println(getString(messageId));
        if (IsMainFormVisible()) {
            mainServiceClient.OnFatalMessage(getString(messageId));
        }
    }

    void SetFatalMessage(String message) {
        if (IsMainFormVisible()) {
            mainServiceClient.OnFatalMessage(message);
        }
    }

    /**
     * Stops location manager, then starts it.
     */
    void RestartGpsManagers() {
        System.out.println("GpsLoggingService.RestartGpsManagers");
        StopGpsManager();
        StartGpsManager();
    }

    /**
     * This event is raised when the GeneralLocationListener has a new location.
     * This method in turn updates notification, writes to file, reobtains
     * preferences, notifies main service client and resets location managers.
     *
     * @param loc Location object
     */
    void OnLocationChanged(Location loc) {
        if (!Session.isStarted()) {
            System.out.println("OnLocationChanged called, but Session.isStarted is false");
            StopLogging();
            return;
        }

        long currentTimeStamp = System.currentTimeMillis();

        Session.setLatestTimeStamp(currentTimeStamp);
        Session.setCurrentLocationInfo(loc);
        if (loc.hasAccuracy()){
            if (loc.getAccuracy() < 20)
                SetDistanceTraveled(loc);
        }

        if (IsMainFormVisible())
            mainServiceClient.OnLocationUpdate(loc);
    }

    private boolean SetDistanceTraveled(Location loc) {
        // Distance
        if (Session.getPreviousLocationInfo() == null) {
            Session.setPreviousLocationInfo(loc);
        }
        // Calculate this location and the previous location and add to the current running total distance.
        // NOTE: Should be used in conjunction with 'distance required before logging' for more realistic values.
        double distance = Utilities.CalculateDistance(
                Session.getPreviousLatitude(),
                Session.getPreviousLongitude(),
                loc.getLatitude(),
                loc.getLongitude());
        if (distance > 5){
            Session.setPreviousLocationInfo(loc);
            Session.setTotalTravelled(Session.getTotalTravelled() + distance);
            createNewPoint(loc);
            return true;
        }

        return false;

    }

    /**
     * Informs the main service client of the number of visible satellites.
     *
     * @param count Number of Satellites
     */
    void SetSatelliteInfo(int count) {
        Session.setSatelliteCount(count);
        if (IsMainFormVisible()) {
            mainServiceClient.OnSatelliteCount();
        }
    }

    private boolean IsMainFormVisible() {
        return mainServiceClient != null;
    }

    private void createNewTrack(){
        System.out.println("Created new Track");
        Track newTrack = new Track();
        newTrack.setCreate(new DateTime(new Date()));
        newTrack.setName("New Track");

        newTrack.setId(DatabaseAccess.writeTrack(this, newTrack));

        Session.setCurrentTrack(newTrack);
    }

    private void createNewPoint(Location location){
        GPSData point = new GPSData();

        point.setLatitude(location.getLatitude());
        point.setLongitude(location.getLongitude());
        point.setAltitude(location.getAltitude());
        point.setSatellites(Session.getSatelliteCount());
        point.setAccuracy(location.getAccuracy());
        point.setTimestamp(new DateTime(location.getTime()));
        point.setSpeed(location.getSpeed());
        point.setBearing(location.getBearing());

        // if the track is not stored yet (asynchronus)
        // use -> getIdTrack
        if (Session.getCurrentTrack() == null)
            createNewTrack();
        point.setTrackID(Session.getCurrentTrack() == null ? DatabaseAccess.getIdTrack(): Session.getCurrentTrack().getId());

        DatabaseAccess.writeGPSData(this, point);
    }

    /**
     * Can be used from calling classes as the go-between for methods and
     * properties.
     */
    public class GpsLoggingBinder extends Binder {
        public GpsLoggingService getService() {
            System.out.println("GpsLoggingBinder.getService");
            return GpsLoggingService.this;
        }
    }

}