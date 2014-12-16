package technotracks.ch.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Blechfalke on 16.12.14.
 */
public abstract class NetworkTools
{
    /**
     * Tell if there is a network connection
     * @param context
     * The application context
     * @return
     * true if a network connection is available
     */
    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        return !(networkInfo == null || !networkInfo.isConnected());
    }
}
