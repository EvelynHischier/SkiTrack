package ch.technotracks.export;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import ch.technotracks.backend.GPSData;

/**
 * Created by Evelyn on 05.01.2015.
 */
public class CSVFile {

    public static String generateCSVFile(Collection<GPSData> gpss) {

            StringBuffer buffer = new StringBuffer();

            // title
            // trackID, gpsID, accuracy, altitude, bearing, latitude, longitude, satellites, speed, timestamp
            buffer.append("trackID");
            buffer.append(',');
            buffer.append("gpsID");
            buffer.append(',');
            buffer.append("accuracy");
            buffer.append(',');
            buffer.append("altitude");
            buffer.append(',');
            buffer.append("bearing");
            buffer.append(',');
            buffer.append("latitude");
            buffer.append(',');
            buffer.append("longitude");
            buffer.append(',');
            buffer.append("satellites");
            buffer.append(',');
            buffer.append("speed");
            buffer.append(',');
            buffer.append("timestamp");
            buffer.append("#");

            // appending data to string buffer
            for (GPSData gps : gpss){

                buffer.append(gps.getTrackID()+",");            // trackID,
                buffer.append(gps.getId()+",");                 // gpsID,
                buffer.append(gps.getAccuracy()+",");           // accuracy,
                buffer.append(gps.getAltitude()+",");           // altitude,
                buffer.append(gps.getBearing()+",");            // bearing,
                buffer.append(gps.getLatitude()+",");           // latitude,
                buffer.append(gps.getLongitude()+",");          // longitude,
                buffer.append(gps.getSatellites()+",");         // satellites,
                buffer.append(gps.getSpeed()+",");              // speed
                buffer.append(gps.getTimestamp().toString());  // timestamp
                buffer.append("#");                            // new line
            }
        return buffer.toString();
    }
}
