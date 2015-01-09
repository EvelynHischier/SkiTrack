package technotracks.ch.gps;

import java.util.Iterator;


import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;

public class MyLocationListener implements LocationListener, GpsStatus.Listener {

    private static GpsLoggingService loggingService;

    public MyLocationListener(GpsLoggingService activity) {
        loggingService = activity;
    }

    /**
     * Event raised when a new fix is received.
     */
    public void onLocationChanged(Location loc) {

        try {
            if (loc != null)
                loggingService.OnLocationChanged(loc);

        } catch (Exception ex) {
            loggingService.SetFatalMessage(ex.getMessage());
        }

    }

    public void onProviderDisabled(String provider) {
        loggingService.RestartGpsManagers();
    }

    public void onProviderEnabled(String provider) {
        loggingService.RestartGpsManagers();
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (status == LocationProvider.OUT_OF_SERVICE) {
            System.out.println(provider + " is out of service");
        }

        if (status == LocationProvider.AVAILABLE) {
            System.out.println(provider + " is available");
        }

        if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
            System.out.println(provider + " is temporarily unavailable");
        }
    }

    public void onGpsStatusChanged(int event) {

        switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                System.out.println("GPS Event First Fix");
                loggingService.NotifyClientGPSFix();
                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                GpsStatus status = loggingService.gpsLocationManager.getGpsStatus(null);

                int maxSatellites = status.getMaxSatellites();

                Iterator<GpsSatellite> it = status.getSatellites().iterator();
                int count = 0;

                while (it.hasNext() && count <= maxSatellites) {
                    it.next();
                    count++;
                }

                System.out.println(String.valueOf(count) + " satellites");
                loggingService.SetSatelliteInfo(count);
                break;

            case GpsStatus.GPS_EVENT_STARTED:
                System.out.println("GPS started, waiting for fix");
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                System.out.println("GPS Event Stopped");
                break;

        }
    }
}
