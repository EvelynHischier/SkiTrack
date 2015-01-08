package technotracks.ch.gps;

import android.location.Location;

public interface IGpsLoggingServiceClient {

    /**
     * New message from the service to be displayed on the activity form.
     *
     * @param message
     */
    public void OnStatusMessage(String message);

    /**
     * Indicates that a fatal error has occurred, logging will stop.
     *
     * @param message
     */
    public void OnFatalMessage(String message);

    /**
     * A new location fix has been obtained.
     *
     * @param location
     */
    public void OnLocationUpdate(Location location);

    /**
     * New satellite count has been obtained.
     *
     * @param count
     */
    public void OnSatelliteCount(int count);

    /**
     * Asking the calling activity form to clear itself.
     */
    public void OnStartLogging();

    /**
     * Asking the calling activity form to indicate that logging has stopped
     */
    public void OnStopLogging();

    /**
     * Indicates that the location manager has started waiting for its next location
     */
    public void OnWaitingForLocation(boolean inProgress);

}
