package technotracks.ch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.api.client.util.DateTime;

import java.util.ArrayList;
import java.util.List;

import ch.technotracks.backend.gPSDataApi.model.GPSData;
import ch.technotracks.backend.trackApi.model.Track;

/**
 * Created by Evelyn on 15.12.2014.
 */
public class DatabaseAccess {

    private static SQLHelper helper;
    private static SQLiteDatabase database;

    // *************************************************************************
    //                      open connection
    // *************************************************************************
    public static void openConnection(Context context){
        helper = new SQLHelper(context, null);
        database = helper.getWritableDatabase();
    }
    // *************************************************************************
    //                      close connection
    // *************************************************************************
    public static void close(){
        helper.close();
    }

    // *************************************************************************
    //                      saving locally
    // *************************************************************************

    // Track
    public static long writeTrack(Track track) {

        ContentValues values = new ContentValues();

        values.put(SQLHelper.TRACK_CREATE, track.getCreate().toString());
        values.put(SQLHelper.TRACK_NAME, track.getName());
        values.put(SQLHelper.TRACK_SYNC, false);

        // if vehicle is not null
        return database.insert(SQLHelper.TABLE_NAME_TRACK, null, values);
    }

    public static long updateTrackToSynced(int trackID){
        ContentValues values = new ContentValues();

        String where = SQLHelper.TRACK_ID +" = "+trackID;

        values.put(SQLHelper.TRACK_SYNC, true);

        return database.update(SQLHelper.TABLE_NAME_TRACK, values, where, null);
    }

    public static List<Track> readTrack(){
        List<Track> tracks = new ArrayList<Track>();
        Track track;
        String dateText;
        Cursor cursor;

        String sql = "SELECT * FROM "+SQLHelper.TABLE_NAME_TRACK + " where "+SQLHelper.TRACK_SYNC+" = 0 ";   //AS _id necessary for the SimpleCursorAdapter ??
        cursor = database.rawQuery(sql, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            track = new Track();

            track.setIdLocal(cursor.getLong(cursor.getColumnIndex(SQLHelper.TRACK_ID)));
            track.setName(cursor.getString(cursor.getColumnIndex(SQLHelper.TRACK_NAME)));
            track.setSync(true);

            tracks.add(track);
            cursor.moveToNext();
        }
        cursor.close();

        return tracks;
    }

    public static long writeGPSData(GPSData point) {
        ContentValues values = new ContentValues();
        values.put(SQLHelper.GPSDATA_ACCURACY, point.getAccuracy());
        values.put(SQLHelper.GPSDATA_ALTITUDE, point.getAltitude());
        values.put(SQLHelper.GPSDATA_BEARING, point.getBearing());
        values.put(SQLHelper.GPSDATA_LATITUDE, point.getLatitude());
        values.put(SQLHelper.GPSDATA_LONGITUDE, point.getLongitude());
        values.put(SQLHelper.GPSDATA_SATELLITES, point.getSatellites());
        values.put(SQLHelper.GPSDATA_SPEED, point.getSpeed());
        values.put(SQLHelper.GPSDATA_TIMESTAMP, new DateTime(point
                .getTimestamp().getValue()).toString());
        return database.insert(SQLHelper.TABLE_NAME_GPSDATA, null, values);
    }
    public static List<GPSData> readGPSData() {
        List<GPSData> points = new ArrayList<GPSData>();
        GPSData point;
        Cursor cursor;
        String dateText;
        String sql = "SELECT * FROM " + SQLHelper.TABLE_NAME_GPSDATA; //AS _id necessary for the SimpleCursorAdapter ??
        cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            point = new GPSData();
            point.setAccuracy(cursor.getFloat(cursor
                    .getColumnIndex(SQLHelper.GPSDATA_ACCURACY)));
            point.setAltitude(cursor.getDouble(cursor
                    .getColumnIndex(SQLHelper.GPSDATA_ALTITUDE)));
            point.setBearing(cursor.getFloat(cursor
                    .getColumnIndex(SQLHelper.GPSDATA_BEARING)));
            point.setId(cursor.getLong(cursor
                    .getColumnIndex(SQLHelper.GPSDATA_ID)));
            point.setLatitude(cursor.getDouble(cursor
                    .getColumnIndex(SQLHelper.GPSDATA_LATITUDE)));
            point.setLongitude(cursor.getDouble(cursor
                    .getColumnIndex(SQLHelper.GPSDATA_LONGITUDE)));
            point.setSatellites(cursor.getInt(cursor
                    .getColumnIndex(SQLHelper.GPSDATA_SATELLITES)));
            dateText = cursor.getString(cursor
                    .getColumnIndex(SQLHelper.GPSDATA_TIMESTAMP));
            point.setTimestamp(new DateTime(dateText));
            points.add(point);
            cursor.moveToNext();
        }
        cursor.close();
        return points;
    }


}
