package technotracks.ch.network;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import technotracks.ch.R;
import technotracks.ch.common.DirectoryTools;
import technotracks.ch.constant.Constant;

/**
 * A class which download the map
 * @author Joel
 *
 */
public class DownloadMap
{
    private ProgressDialog pd;
    private long enqueue;
    private DownloadManager dm;

    /**
     * Default constructor
     * @param activity
     * The context calling this class
     */
    public DownloadMap(final Activity activity)
    {
		/* A broadcast receiver to know when the download is successfully completed */
        BroadcastReceiver receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action))	//if the action is the map download
                {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(enqueue);
                    Cursor c = dm.query(query);
                    if(c != null)
                    {
                        if (c.moveToFirst())
                        {
                            int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                            if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex))	//if the download is successful
                            {
                                pd.dismiss();
                            }
                            else
                            {
                                activity.finish();
                            }
                        }
                    }
                }
            }
        };

		/* Register the broadcast receiver we previously created */
        activity.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        pd = new CustomProgressDialog(activity);

        dm = (DownloadManager)activity.getSystemService(Context.DOWNLOAD_SERVICE);	//get the download manager

		/* Prepare download (url, message, title, path) */
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Constant.MAP_SERVER_URL));
        request.setDescription(activity.getText(R.string.fileDownloadDescription));
        request.setTitle(activity.getText(R.string.fileDownloadTitle));
        request.setDestinationUri(Uri.parse("file://" + DirectoryTools.getSwissMap().getAbsolutePath()));

        enqueue = dm.enqueue(request);	//start download

        if(Build.VERSION.SDK_INT >= 11)
        {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);	//set the notification to visible
        }
    }


    /**
     * A customized progress dialog
     * @author Joel
     *
     */
    private class CustomProgressDialog extends ProgressDialog
    {
        /**
         * Constructor
         * @param context
         * The context in which running
         */
        public CustomProgressDialog(Context context)
        {
            super(context);
            setTitle(R.string.downloadingMap);
            setMessage(context.getText(R.string.pleaseWait));
            setIndeterminate(true);
            setCancelable(false);
            setCanceledOnTouchOutside(false);

            show();
        }
    }
}
