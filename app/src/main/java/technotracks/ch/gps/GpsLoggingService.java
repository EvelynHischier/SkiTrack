package technotracks.ch.gps;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import technotracks.ch.common.Session;
import technotracks.ch.common.Utilities;

public class GpsLoggingService extends Service  {


    private static IGpsLoggingServiceClient mainServiceClient;
    private final IBinder binder = new GpsLoggingBinder();

    // ---------------------------------------------------
    // Helpers and managers
    // ---------------------------------------------------
    protected LocationManager gpsLocationManager;
    private MyLocationListener gpsLocationListener;

    private Handler handler = new Handler();

    // ---------------------------------------------------

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
//            Bundle bundle = intent.getExtras();
//
//            if (bundle != null) {
//                boolean stopRightNow = bundle.getBoolean("immediatestop");
//                boolean startRightNow = bundle.getBoolean("immediatestart");
//
//                if (startRightNow) {
//                    System.out.println("Intent received - Start Logging Now");
//                    StartLogging();
//                }
//
//                if (stopRightNow) {
//                    System.out.println("Intent received - Stop logging now");
//                    StopLogging();
//                }
//            }

            if (Session.isStarted()) {
                StartGpsManager();
            }

        } else {
            // A null intent is passed in if the service has been killed and
            // restarted.
            System.out.println("Service restarted with null intent. Start logging.");
            StartLogging();

        }
    }

    /**
     * Resets the form, resets file name if required, reobtains preferences
     */
    public void StartLogging() {
        Session.setAddNewTrackSegment(true);

        if (Session.isStarted()) {
            System.out.println("Session already started, ignoring");
            return;
        }

        Session.setStarted(true);
        NotifyClientStarted();
        StartGpsManager();
    }

    /**
     * Asks the main service client to clear its form.
     */
    private void NotifyClientStarted() {
        if (IsMainFormVisible()) {
            mainServiceClient.OnStartLogging();
        }
    }

    /**
     * Stops logging, removes notification, stops GPS manager, stops email timer
     */
    public void StopLogging() {
        System.out.println("GpsLoggingService.StopLogging");
        Session.setAddNewTrackSegment(true);

        Session.setStarted(false);
        Session.setCurrentLocationInfo(null);
        stopForeground(true);
        StopGpsManager();
        NotifyClientStopped();
    }

    /**
     * Starts the location manager. There are two location managers - GPS and
     * Cell Tower. This code determines which manager to request updates from
     * based on user preference and whichever is enabled. If GPS is enabled on
     * the phone, that is used. But if the user has also specified that they
     * prefer cell towers, then cell towers are used. If neither is enabled,
     * then nothing is requested.
     */
    private void StartGpsManager() {
        System.out.println("GpsLoggingService.StartGpsManager");


        if (gpsLocationListener == null) {
            gpsLocationListener = new MyLocationListener(this, "GPS");
        }

        gpsLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // TODO CHECK IF GPS IS ENABLED
        if (Session.isGpsEnabled()) {

            // gps satellite based
            gpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 0,
                    gpsLocationListener);

            gpsLocationManager.addGpsStatusListener(gpsLocationListener);

            Session.setUsingGps(true);

        }

        if (mainServiceClient != null) {
            mainServiceClient.OnWaitingForLocation(true);
            Session.setWaitingForLocation(true);
        }

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
        }


        if (mainServiceClient != null) {
            Session.setWaitingForLocation(false);
            mainServiceClient.OnWaitingForLocation(false);
        }
    }

    /**
     * Gives a status message to the main service client to display
     *
     * @param status The status message
     */
    void SetStatus(String status) {
        if (IsMainFormVisible()) {
            mainServiceClient.OnStatusMessage(status);
        }
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

    /**
     * Gets string from given resource ID, passes to SetStatus(String)
     *
     * @param stringId ID of string to lookup
     */
    private void SetStatus(int stringId) {
        String s = getString(stringId);
        SetStatus(s);
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
        SetDistanceTraveled(loc);

        if (IsMainFormVisible()) {
            mainServiceClient.OnLocationUpdate(loc);
        }

    }

    private void SetDistanceTraveled(Location loc) {
        // Distance
        if (Session.getPreviousLocationInfo() == null) {
            Session.setPreviousLocationInfo(loc);
        }
        // Calculate this location and the previous location location and add to the current running total distance.
        // NOTE: Should be used in conjunction with 'distance required before logging' for more realistic values.
        double distance = Utilities.CalculateDistance(
                Session.getPreviousLatitude(),
                Session.getPreviousLongitude(),
                loc.getLatitude(),
                loc.getLongitude());
        Session.setPreviousLocationInfo(loc);
        Session.setTotalTravelled(Session.getTotalTravelled() + distance);
    }

    /**
     * Informs the main service client of the number of visible satellites.
     *
     * @param count Number of Satellites
     */
    void SetSatelliteInfo(int count) {
        Session.setSatelliteCount(count);
        if (IsMainFormVisible()) {
            mainServiceClient.OnSatelliteCount(count);
        }
    }

    private boolean IsMainFormVisible() {
        return mainServiceClient != null;
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