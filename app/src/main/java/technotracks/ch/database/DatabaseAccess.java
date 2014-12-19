package technotracks.ch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.api.client.util.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ch.technotracks.backend.gPSDataApi.model.GPSData;
import ch.technotracks.backend.trackApi.model.Track;
import ch.technotracks.backend.userApi.model.User;

/**
 * Created by Evelyn on 15.12.2014.
 */
public class DatabaseAccess {

    private static SQLHelper helper;
    private static SQLiteDatabase database;

    // *************************************************************************
    //                      open connection
    // *************************************************************************
    private static void openConnection(Context context){
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

    public static long updateToSynced(Context context, long id, String table, String titleID){
        openConnection(context);

        ContentValues values = new ContentValues();
        String where = titleID +" = "+id;
        values.put(SQLHelper.SYNC, true);

        return database.update(table, values, where, null);
    }
    // *************************************************************************
    //                      Track
    // *************************************************************************
    public static long writeTrack(Context context, Track track) {
        openConnection(context);

        ContentValues values = new ContentValues();

        values.put(SQLHelper.TRACK_CREATE, track.getCreate().toString());
        values.put(SQLHelper.TRACK_NAME, track.getName());
        values.put(SQLHelper.SYNC, false);

        return database.insert(SQLHelper.TABLE_NAME_TRACK, null, values);
    }

    public static long getIdTrack(){
        String sql = "SELECT MAX("+SQLHelper.TRACK_ID+") FROM "+SQLHelper.TABLE_NAME_TRACK;

        Cursor cursor = database.rawQuery(sql, null);

        if(!cursor.moveToFirst())
            return 1;

        return cursor.getInt(0) +1;
    }

    public static List<Track> readTrack(Context context){
        List<Track> tracks = new ArrayList<Track>();
        Track track;
        String dateText;
        Cursor cursor;

        openConnection(context);

        String sql = "SELECT * FROM "+SQLHelper.TABLE_NAME_TRACK + " where "+SQLHelper.SYNC +" = 0 ";
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

    // *************************************************************************
    //                      GPS
    // *************************************************************************
    public static long writeGPSData(Context context, GPSData point) {
        openConnection(context);
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
        values.put(SQLHelper.TRACK_ID, point.getTrackID());

        return database.insert(SQLHelper.TABLE_NAME_GPSDATA, null, values);
    }
    public static List<GPSData> readGPSData(Context context, long trackIdOld) {
        List<GPSData> points = new ArrayList<GPSData>();
        GPSData point;
        Cursor cursor;
        String dateText;

        openConnection(context);
        String sql = "SELECT * FROM "+SQLHelper.TABLE_NAME_GPSDATA + " where "+SQLHelper.TRACK_ID +" = "+trackIdOld+" ";
        cursor = database.rawQuery(sql, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            point = new GPSData();
            point.setAccuracy(cursor.getFloat(cursor.getColumnIndex(SQLHelper.GPSDATA_ACCURACY)));
            point.setAltitude(cursor.getDouble(cursor.getColumnIndex(SQLHelper.GPSDATA_ALTITUDE)));
            point.setBearing(cursor.getFloat(cursor.getColumnIndex(SQLHelper.GPSDATA_BEARING)));
//            point.setId(cursor.getLong(cursor.getColumnIndex(SQLHelper.GPSDATA_ID)));
            point.setLatitude(cursor.getDouble(cursor.getColumnIndex(SQLHelper.GPSDATA_LATITUDE)));
            point.setLongitude(cursor.getDouble(cursor.getColumnIndex(SQLHelper.GPSDATA_LONGITUDE)));
            point.setSatellites(cursor.getInt(cursor.getColumnIndex(SQLHelper.GPSDATA_SATELLITES)));

            dateText = cursor.getString(cursor.getColumnIndex(SQLHelper.GPSDATA_TIMESTAMP));
            point.setTimestamp(new DateTime(dateText));

            points.add(point);
            cursor.moveToNext();
        }
        cursor.close();
        return points;
    }

    // *************************************************************************
    //                      User
    // *************************************************************************
    public static long writeUser(Context context, User user) {
        ContentValues values = new ContentValues();
        openConnection(context);

        values.put(SQLHelper.USER_FIRSTNAME, user.getFirstname());
        values.put(SQLHelper.USER_LASTNAME, user.getLastname());
        values.put(SQLHelper.USER_PASSWORD, user.getPassword());
        values.put(SQLHelper.USER_EMAIL, user.getEMail());
        values.put(SQLHelper.USER_PHONENUMBER, user.getPhoneNumber());
        values.put(SQLHelper.USER_TAKE_PART_CHAMPIONSHIP, false);

        return database.insert(SQLHelper.TABLE_NAME_USER, null, values);
    }

    public static List<User> readUser(Context context){
        List<User> users = new ArrayList<User>();
        User user;
        Cursor cursor;
        int championship;

        openConnection(context);

        String sql = "SELECT * FROM "+SQLHelper.TABLE_NAME_USER + " where "+SQLHelper.SYNC +" = 0 ";
        cursor = database.rawQuery(sql, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            user = new User();

            championship = cursor.getInt(cursor.getColumnIndex(SQLHelper.USER_TAKE_PART_CHAMPIONSHIP));
            user.setChampionship((championship == 1 ? true : false));

            user.setFirstname(cursor.getString(cursor.getColumnIndex(SQLHelper.USER_FIRSTNAME)));
            user.setLastname(cursor.getString(cursor.getColumnIndex(SQLHelper.USER_LASTNAME)));
            user.setEMail(cursor.getString(cursor.getColumnIndex(SQLHelper.USER_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(SQLHelper.USER_PASSWORD)));
            user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(SQLHelper.USER_PHONENUMBER)));

            users.add(user);
            cursor.moveToNext();
        }
        cursor.close();

        return users;
    }

}
