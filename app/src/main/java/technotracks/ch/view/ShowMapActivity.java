package technotracks.ch.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.os.Bundle;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapController;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.ArrayItemizedOverlay;
import org.mapsforge.android.maps.overlay.ArrayWayOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.android.maps.overlay.OverlayWay;
import org.mapsforge.core.GeoPoint;

import java.util.LinkedList;
import java.util.List;

import ch.technotracks.backend.gPSDataApi.model.GPSData;
import technotracks.ch.R;
import technotracks.ch.common.DirectoryTools;
import technotracks.ch.constant.Constant;
import technotracks.ch.database.DatabaseAccess;
import technotracks.ch.network.DownloadMap;
import technotracks.ch.network.NetworkTools;


/**
 * A class which capture tracks and display the map
 * @author Joel
 *
 */
public class ShowMapActivity extends MapActivity
{
    private int track;
    private int satelliteNumber;
    private ArrayWayOverlay path;
    private List<GeoPoint> geoPoints;
    private MapView map;
    private MapController mc;
    private OverlayItem start;
    private OverlayItem stop;

    /**
     * Create the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map);

        satelliteNumber = 0;
        geoPoints = new LinkedList<GeoPoint>();
        path = new ArrayWayOverlay(PaintFactory.getDefaultPaintFill(), PaintFactory.getDefaultPaintOutline());
        start = null;
        stop = null;

        /* Test if network is available for upload data and download map if necessary */
        if(NetworkTools.isNetworkAvailable(getApplicationContext()))
        {
			/* If there is no map start the download */
            if(!DirectoryTools.getSwissMap().exists())
            {
                new DownloadMap(this);
            }
        }
        else	//there is no network
        {
            if(!DirectoryTools.getSwissMap().exists())	//there is no map we need
            {
                missingMap();	//inform the user
            }
        }
		/* Get the selected track id and store it */
        Intent i = getIntent();
        Bundle b = i.getExtras();
        if(b != null)
        {
            track = b.getInt("trackNumber");
        }

		/* Set the map and initialize it */
        map = (MapView)findViewById(R.id.mapView);
        map.setClickable(true);
        map.setMapFile(DirectoryTools.getSwissMap());
        map.setBuiltInZoomControls(true);
        mc = map.getController();

        putExistingMarkers();

    }

    /**
     * Start the activity
     */
    @Override
    protected void onStart()
    {
        super.onStart();

    }

    /**
     * A custom dialog in case the map is missing
     */
    private void missingMap()
    {
        showAlertDialog(getText(R.string.missingMap).toString(), getText(R.string.missingMapMessage).toString());
    }

    /**
     * Display an alert dialog if there is a problem
     * @param title
     * The title
     * @param message
     * The message
     */
    private void showAlertDialog(String title, String message)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setPositiveButton(getText(android.R.string.ok), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }

    // TODO replace function with our own GPS DB Access
    /**
     * Test if data exist for the selected track and put them on the map
     */
    private void putExistingMarkers()
    {

        List<GPSData> points = DatabaseAccess.readGPSData(this);
        GeoPoint coordinates;
        double latitude;
        double longitude;

        for (GPSData point : points){
            latitude = point.getLatitude();
            longitude  = point.getLongitude();
            coordinates = new GeoPoint(latitude, longitude);
            geoPoints.add(coordinates);
        }

        addStartPoint(geoPoints.get(0));
        addStopPoint(geoPoints.get(geoPoints.size() - 1));
        addPath();

        map.setCenter(geoPoints.get(geoPoints.size() - 1));	//re-center the view on the current position
        mc.setZoom(Constant.DEFAULT_ZOOM);	//set the zoom to the default

        map.redrawTiles();	//refresh the map

    }


    private void addStartPoint(GeoPoint coordinate)
    {
        start = new OverlayItem(coordinate, getText(R.string.currentLocation).toString(), getText(R.string.youAreHere).toString());
        ArrayItemizedOverlay startOverlay = new ArrayItemizedOverlay(getResources().getDrawable(R.drawable.ic_maps_indicator_starting_position));
        startOverlay.addItem(start);
        map.getOverlays().add(startOverlay);
    }

    private void addStopPoint(GeoPoint coordinate)
    {
        stop = new OverlayItem(coordinate, getText(R.string.currentLocation).toString(), getText(R.string.youAreHere).toString());
        ArrayItemizedOverlay stopOverlay = new ArrayItemizedOverlay(getResources().getDrawable(R.drawable.ic_maps_indicator_stop_position));
        stopOverlay.addItem(stop);
        map.getOverlays().add(stopOverlay);
    }

    private void addPath()
    {
        OverlayWay ow =
                new OverlayWay(new GeoPoint[][]{geoPoints.toArray(new GeoPoint[geoPoints.size()])});
        path.addWay(ow);
        map.getOverlays().add(path);	//adding path to the map
    }

    private void addCurrentLocation(GeoPoint coordinates)
    {
        OverlayItem item = new OverlayItem(coordinates, getText(R.string.currentLocation).toString(), getText(R.string.youAreHere).toString());	//creating the item to display current location
        ArrayItemizedOverlay itemizedOverlay = new ArrayItemizedOverlay(getResources().getDrawable(R.drawable.ic_maps_indicator_current_position));	//creating the array containing all overlayItem
        itemizedOverlay.addItem(item);	//adding item to the array
        map.getOverlays().add(itemizedOverlay);	//adding the item overlay to the map
    }

    /**
     * Destroy the activity and stop the gps listening
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    /**
     * A factory for creating Paint element for the track
     * @author Joel
     *
     */
    private abstract static class PaintFactory
    {
        /**
         * The default paint for filling path
         * @return
         * A paint with default fill configuration
         */
        public static Paint getDefaultPaintFill()
        {
            Paint tmp = defaultOption();
            tmp.setAlpha(160);
            tmp.setPathEffect(new DashPathEffect(new float[] { 20, 20  }, 0));

            return tmp;
        }

        /**
         * The default paint for outlining path
         * @return
         * A paint with default outline configuration
         */
        public static Paint getDefaultPaintOutline()
        {
            Paint tmp = defaultOption();
            tmp.setAlpha(128);

            return tmp;
        }

        /**
         * A basic paint
         * @return
         * A basic paint with default option
         */
        private static Paint defaultOption()
        {
            Paint tmp = new Paint(Paint.ANTI_ALIAS_FLAG);
            tmp.setStyle(Paint.Style.STROKE);
            tmp.setColor(Color.BLUE);
            tmp.setStrokeWidth(7);
            tmp.setStrokeJoin(Paint.Join.ROUND);

            return tmp;
        }
    }
}
