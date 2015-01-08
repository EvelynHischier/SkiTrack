package technotracks.ch.common;

import android.app.Application;
import android.location.Location;

import ch.technotracks.backend.trackApi.model.Track;

public class Session extends Application {

    // ---------------------------------------------------
    // Session values - updated as the app runs
    // ---------------------------------------------------
    private static boolean gpsEnabled;
    private static boolean isStarted;
    private static Track currentTrack;
    private static boolean isUsingGps;
    private static int satellites;
    private static long startTimeStamp;
    private static long latestTimeStamp;
    private static Location currentLocationInfo;
    private static Location previousLocationInfo;
    private static double totalTravelled;
    private static int numPoints;
    private static boolean isBound;

    // ---------------------------------------------------

    /**
     * @return whether GPS (satellite) is enabled
     */
    public static boolean isGpsEnabled() {
        return gpsEnabled;
    }

    /**
     * @param gpsEnabled set whether GPS (satellite) is enabled
     */
    public static void setGpsEnabled(boolean gpsEnabled) {
        Session.gpsEnabled = gpsEnabled;
    }

    /**
     * @return whether logging has started
     */
    public static boolean isStarted() {
        return isStarted;
    }

    /**
     * @param isStarted set whether logging has started
     */
    public static void setStarted(boolean isStarted) {
        Session.isStarted = isStarted;
        if (isStarted) {
            Session.startTimeStamp = System.currentTimeMillis();
        }
    }

    /**
     * @return the isUsingGps
     */
    public static boolean isUsingGps() {
        return isUsingGps;
    }

    /**
     * @param isUsingGps the isUsingGps to set
     */
    public static void setUsingGps(boolean isUsingGps) {
        Session.isUsingGps = isUsingGps;
    }

    /**
     * @return the number of satellites visible
     */
    public static int getSatelliteCount() {
        return satellites;
    }

    /**
     * @param satellites sets the number of visible satellites
     */
    public static void setSatelliteCount(int satellites) {
        Session.satellites = satellites;
    }


    /**
     * @return the currentLatitude
     */
    public static double getCurrentLatitude() {
        if (getCurrentLocationInfo() != null) {
            return getCurrentLocationInfo().getLatitude();
        } else {
            return 0;
        }
    }

    public static double getPreviousLatitude() {
        Location loc = getPreviousLocationInfo();
        return loc != null ? loc.getLatitude() : 0;
    }

    public static double getPreviousLongitude() {
        Location loc = getPreviousLocationInfo();
        return loc != null ? loc.getLongitude() : 0;
    }

    public static double getTotalTravelled() {
        return totalTravelled;
    }

    public static void setTotalTravelled(double totalTravelled) {
        if (totalTravelled == 0) {
            Session.numPoints = 1;
        } else {
            Session.numPoints++;
        }
        Session.totalTravelled = totalTravelled;
    }

    public static Location getPreviousLocationInfo() {
        return previousLocationInfo;
    }

    public static void setPreviousLocationInfo(Location previousLocationInfo) {
        Session.previousLocationInfo = previousLocationInfo;
    }

    /**
     * Determines whether a valid location is available
     */
    public static boolean hasValidLocation() {
        return (getCurrentLocationInfo() != null && getCurrentLatitude() != 0 && getCurrentLongitude() != 0);
    }

    /**
     * @return the currentLongitude
     */
    public static double getCurrentLongitude() {
        if (getCurrentLocationInfo() != null) {
            return getCurrentLocationInfo().getLongitude();
        } else {
            return 0;
        }
    }

    /**
     * @return the latestTimeStamp (for location info)
     */
    public static long getLatestTimeStamp() {
        return latestTimeStamp;
    }

    /**
     * @return the timestamp when measuring was started
     */
    public static long getStartTimeStamp() {
        return startTimeStamp;
    }

    /**
     * @param latestTimeStamp the latestTimeStamp (for location info) to set
     */
    public static void setLatestTimeStamp(long latestTimeStamp) {
        Session.latestTimeStamp = latestTimeStamp;
    }

    /**
     * @param currentLocationInfo the latest Location class
     */
    public static void setCurrentLocationInfo(Location currentLocationInfo) {
        Session.currentLocationInfo = currentLocationInfo;
    }

    /**
     * @return the Location class containing latest lat-long information
     */
    public static Location getCurrentLocationInfo() {
        return currentLocationInfo;
    }

    public static int getNumPoints() {
        return numPoints;
    }

    /**
     * @param isBound set whether the activity is bound to the GpsLoggingService
     */
    public static void setBoundToService(boolean isBound) {
        Session.isBound = isBound;
    }

    /**
     * @return whether the activity is bound to the GpsLoggingService
     */
    public static boolean isBoundToService() {
        return isBound;
    }

    public static Track getCurrentTrack() {
        return currentTrack;
    }

    public static void setCurrentTrack(Track currentTrack) {
        Session.currentTrack = currentTrack;
    }

    public static void resetSession(){
        Session.numPoints = 0;
        Session.totalTravelled = 0d;
        Session.currentLocationInfo = null;
        Session.previousLocationInfo = null;
        Session.currentTrack = null;

    }
}