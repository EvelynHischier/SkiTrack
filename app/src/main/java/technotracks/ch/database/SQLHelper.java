package technotracks.ch.database;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Evelyn on 12.12.2014.
 */
public class SQLHelper extends SQLiteOpenHelper {

    // DB settings
    private static final String DATABASE_NAME = "skiTrack.db";
    private static final int DATABASE_VERSION = 1;

    // titles of the table
    public static final String TABLE_NAME_GPSDATA = "GPSData";
    public static final String TABLE_NAME_CHAMPIONSHIP = "Championship";
    public static final String TABLE_NAME_TRACK = "Track";
    public static final String TABLE_NAME_USER = "User";
    public static final String TABLE_NAME_VEHICLE = "Vehicle";

    public static final String TABLE_NAME_TRACK_USER = "Track_User";
    public static final String TABLE_NAME_CHAMPIONSHIP_USER = "Championship_User";

    // Title of Columns of GPSData
    public static final String GPSDATA_ID = "id_gpsdata";
    public static final String GPSDATA_LONGITUDE = "longitude";
    public static final String GPSDATA_LATITUDE = "latitude";
    public static final String GPSDATA_ALTITUDE = "altitude";
    public static final String GPSDATA_ACCURACY = "accuracy";
    public static final String GPSDATA_SATELLITES = "satellites";
    public static final String GPSDATA_TIMESTAMP = "timestamp";
    public static final String GPSDATA_SPEED = "speed";
    public static final String GPSDATA_BEARING = "bearing";

    // Title of Columns of chamiponship
    public static final String CHAMPIONSHIP_ID = "id_championship";
    public static final String CHAMPIONSHIP_START = "start_date";
    public static final String CHAMPIONSHIP_END = "end_date";

    // Title of Columns of track
    public static final String TRACK_ID = "id_track";
    public static final String TRACK_NAME = "name";
    public static final String TRACK_CREATE = "create_date";
    public static final String TRACK_SYNC = "synchronized";

    // Title of Columns of user
    public static final String USER_ID = "id_user";
    public static final String USER_FIRSTNAME = "firstname";
    public static final String USER_LASTNAME = "lastname";
    public static final String USER_PASSWORD = "password";
    public static final String USER_EMAIL = "email";
    public static final String USER_PHONENUMBER = "phonenumber";
    public static final String USER_TAKE_PART_CHAMPIONSHIP = "championship";

    // Title of Columns of vehicle
    public static final String VEHICLE_ID = "id_vehicle";
    public static final String VEHICLE_NAME = "name";

    // N:M table -> User and Track
    public static final String TRACK_USER_IDTRACK = "id_track";
    public static final String TRACK_USER_IDUSER = "id_user";

    // N:M table -> Championship and user
    public static final String CHAMPIONSHIP_USER_IDCHAMPIONSHIP = "id_championship";
    public static final String CHAMPIONSHIP_USER_IDUSER = "id_user";

    // *************************************************************************
    //                      create table
    // *************************************************************************
    // Championship_user
    public static final String TABLE_CREATE_CHAMPIONSHIP_USER = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_CHAMPIONSHIP_USER + "("
            + CHAMPIONSHIP_USER_IDCHAMPIONSHIP + " LONG NOT NULL,"
            + CHAMPIONSHIP_USER_IDUSER + " LONG NOT NULL "
            + ")";

    // track_user
    public static final String TABLE_CREATE_TRACK_USER = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_TRACK_USER + "("
            + TRACK_USER_IDTRACK + " LONG NOT NULL,"
            + TRACK_USER_IDUSER + " LONG NOT NULL, "
            + " FOREIGN KEY(" + TRACK_USER_IDTRACK + ") REFERENCES " + TABLE_NAME_TRACK + " (" + TRACK_ID + "), "
            + " FOREIGN KEY(" + TRACK_USER_IDUSER + ") REFERENCES " + TABLE_NAME_USER + " (" + USER_ID + ")"
            + ")";

    // gps
    public static final String TABLE_CREATE_GPS = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_GPSDATA + "(" +
            GPSDATA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            GPSDATA_LONGITUDE + " DOUBLE, " +
            GPSDATA_LATITUDE + " DOUBLE, " +
            GPSDATA_ALTITUDE + " DOUBLE, " +
            GPSDATA_ACCURACY + " FLOAT, " +
            GPSDATA_SATELLITES + " INTEGER, " +
            GPSDATA_TIMESTAMP + " DATE, " +
            GPSDATA_SPEED + " FLOAT, " +
            GPSDATA_BEARING + " FLOAT, " +
            TRACK_ID + " LONG, " +
            " FOREIGN KEY(" + TRACK_ID + ") REFERENCES " + TABLE_NAME_TRACK + " (" + TRACK_ID + ")"
            + ")";

    // championship
    public static final String TABLE_CREATE_CHAMPIONSHIP = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_CHAMPIONSHIP + "("
            + CHAMPIONSHIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CHAMPIONSHIP_START + " DATE, "
            + CHAMPIONSHIP_END + " DATE)";

    // track
    public static final String TABLE_CREATE_TRACK = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_TRACK + "("
            + TRACK_ID + " INTEGER PRIMARY KEY,"
            + TRACK_NAME + " TEXT, "
            + TRACK_CREATE + " DATE, "
            + TRACK_SYNC + " BOOLEAN)";

    // user
    public static final String TABLE_CREATE_USER = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_USER
            + "("
            + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + USER_FIRSTNAME + " TEXT, "
            + USER_LASTNAME + " TEXT, "
            + USER_PASSWORD + " TEXT,"
            + USER_EMAIL + " TEXT,"
            + USER_PHONENUMBER + " TEXT,"
            + USER_TAKE_PART_CHAMPIONSHIP + " BOOLEAN" +
            ")";

    public static final String TABLE_CREATE_VEHICLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME_VEHICLE
            + "("
            + VEHICLE_ID + "INTEGER PRIMARY KEY, "
            + VEHICLE_NAME + " TEXT"
            + ")";

    public SQLHelper(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_CHAMPIONSHIP);
        db.execSQL(TABLE_CREATE_GPS);
        db.execSQL(TABLE_CREATE_TRACK);
        db.execSQL(TABLE_CREATE_USER);
        db.execSQL(TABLE_CREATE_TRACK_USER);
        db.execSQL(TABLE_CREATE_CHAMPIONSHIP_USER);
        db.execSQL(TABLE_CREATE_VEHICLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CHAMPIONSHIP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_GPSDATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TRACK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CHAMPIONSHIP_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TRACK_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_VEHICLE);

        // create fresh tables
        onCreate(db);
    }

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }


    }
}

