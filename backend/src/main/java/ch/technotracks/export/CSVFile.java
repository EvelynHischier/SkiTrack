package ch.technotracks.export;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import ch.technotracks.backend.GPSData;

/**
 * Created by Evelyn on 05.01.2015.
 */
public class CSVFile {

    private static void generateCSVFile(String fileName, List<GPSData> gpss) {
        try {
            FileWriter writer = new FileWriter(fileName);

            // title
            // trackID, gpsID, accuracy, altitude, bearing, latitude, longitude, satellites, speed, timestamp
            writer.append("trackID");
            writer.append(',');
            writer.append("gpsID");
            writer.append(',');
            writer.append("accuracy");
            writer.append(',');
            writer.append("altitude");
            writer.append(',');
            writer.append("bearing");
            writer.append(',');
            writer.append("latitude");
            writer.append(',');
            writer.append("longitude");
            writer.append(',');
            writer.append("satellites");
            writer.append(',');
            writer.append("speed");
            writer.append(',');
            writer.append("timestamp");
            writer.append('\n');

            // inserting data here
            for (GPSData gps : gpss){

                writer.append(gps.getTrackID()+",");            // trackID,
                writer.append(gps.getId()+",");                 // gpsID,
                writer.append(gps.getAccuracy()+",");           // accuracy,
                writer.append(gps.getAltitude()+",");           // altitude,
                writer.append(gps.getBearing()+",");            // bearing,
                writer.append(gps.getLatitude()+",");           // latitude,
                writer.append(gps.getLongitude()+",");          // longitude,
                writer.append(gps.getSatellites()+",");         // satellites,
                writer.append(gps.getTimestamp().toString()+",");  // timestamp
                writer.append('\n');                            // new line
            }
            // send data
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
