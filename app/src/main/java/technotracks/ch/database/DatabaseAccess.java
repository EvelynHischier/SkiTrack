package technotracks.ch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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

}
