package technotracks.ch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.api.client.util.DateTime;

import java.util.ArrayList;
import java.util.List;

import ch.technotracks.backend.championshipApi.model.Championship;
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

    // set the sync boolean to true
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

    // get the last used id track + 1
    // to inserting a gpsdata if the track is currently not saved (asynchronous)
    public static long getIdTrack(){
        String sql = "SELECT MAX("+SQLHelper.TRACK_ID+") FROM "+SQLHelper.TABLE_NAME_TRACK;

        Cursor cursor = database.rawQuery(sql, null);

        if(!cursor.moveToFirst())
            return 1;

        return cursor.getInt(0) +1;
    }

    // reads every track which is not synchronized
    public static List<Track> readTrack(Context context){
        List<Track> tracks = new ArrayList<>();
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

            dateText = cursor.getString(cursor.getColumnIndex(SQLHelper.TRACK_CREATE));
            track.setCreate(new DateTime(dateText));

            tracks.add(track);
            cursor.moveToNext();
        }
        cursor.close();

        return tracks;
    }

    // get all stored tracks (used in history)
    public static List<Track> readTrackHistory(Context context){
        List<Track> tracks = new ArrayList<>();
        Track track;
        String dateText;
        Cursor cursor;
        int sync;

        openConnection(context);

        String sql = "SELECT * FROM "+SQLHelper.TABLE_NAME_TRACK;
        cursor = database.rawQuery(sql, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            track = new Track();

            track.setIdLocal(cursor.getLong(cursor.getColumnIndex(SQLHelper.TRACK_ID)));
            track.setName(cursor.getString(cursor.getColumnIndex(SQLHelper.TRACK_NAME)));

            sync = cursor.getInt(cursor.getColumnIndex(SQLHelper.SYNC));
            track.setSync(sync != 0);

            dateText = cursor.getString(cursor.getColumnIndex(SQLHelper.TRACK_CREATE));
            track.setCreate(new DateTime(dateText));

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
        List<GPSData> points = new ArrayList<>();
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

    // replaces the local trackId with the id of app engine
    public static List<GPSData> readGPSDataToUpload(Context context, long trackIdOld, long trackIdNEw) {
        List<GPSData> points = new ArrayList<>();
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
            point.setLatitude(cursor.getDouble(cursor.getColumnIndex(SQLHelper.GPSDATA_LATITUDE)));
            point.setLongitude(cursor.getDouble(cursor.getColumnIndex(SQLHelper.GPSDATA_LONGITUDE)));
            point.setSatellites(cursor.getInt(cursor.getColumnIndex(SQLHelper.GPSDATA_SATELLITES)));

            dateText = cursor.getString(cursor.getColumnIndex(SQLHelper.GPSDATA_TIMESTAMP));
            point.setTimestamp(new DateTime(dateText));

            point.setTrackID(trackIdNEw);

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

    // *************************************************************************
    //                        Championship
    // *************************************************************************
    public static long writeChampion(Context context, Championship championship) {
        ContentValues values = new ContentValues();
        openConnection(context);

        values.put(SQLHelper.CHAMPIONSHIP_END, new DateTime(championship.getEnd().getValue()).toString());
        values.put(SQLHelper.CHAMPIONSHIP_START, new DateTime(championship.getStart().getValue()).toString()); championship.getStart();
        values.put(SQLHelper.CHAMPIONSHIP_NAME, championship.getName());

        return database.insert(SQLHelper.TABLE_NAME_CHAMPIONSHIP, null, values);
    }

    public static List<Championship> readChampionship(Context context){
        List<Championship> championships = new ArrayList<Championship>();
        Championship championship;
        Cursor cursor;

        openConnection(context);

        String sql = "SELECT * FROM "+SQLHelper.TABLE_NAME_CHAMPIONSHIP ;
        cursor = database.rawQuery(sql, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            championship = new Championship();

//            championship = cursor.getInt(cursor.getColumnIndex(SQLHelper.USER_TAKE_PART_CHAMPIONSHIP));
//            user.setChampionship((championship == 1 ? true : false));
//
//            user.setFirstname(cursor.getString(cursor.getColumnIndex(SQLHelper.USER_FIRSTNAME)));
//            user.setLastname(cursor.getString(cursor.getColumnIndex(SQLHelper.USER_LASTNAME)));
//            user.setEMail(cursor.getString(cursor.getColumnIndex(SQLHelper.USER_EMAIL)));
//            user.setPassword(cursor.getString(cursor.getColumnIndex(SQLHelper.USER_PASSWORD)));
//            user.setPhoneNumber(cursor.getString(cursor.getColumnIndex(SQLHelper.USER_PHONENUMBER)));

            championships.add(championship);
            cursor.moveToNext();
        }
        cursor.close();

        return championships;
    }

}
