//package technotracks.ch.skitrack_scrum;
//
//
//        import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Color;
//import android.graphics.DashPathEffect;
//import android.graphics.Paint;
//import android.location.GpsSatellite;
//import android.location.GpsStatus;
//import android.location.Location;
//import android.location.LocationListener;
//import android.location.LocationManager;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//
//
//import org.mapsforge.android.maps.MapActivity;
//import org.mapsforge.android.maps.MapController;
//import org.mapsforge.android.maps.MapView;
//import org.mapsforge.android.maps.overlay.ArrayItemizedOverlay;
//import org.mapsforge.android.maps.overlay.ArrayWayOverlay;
//import org.mapsforge.android.maps.overlay.OverlayItem;
//import org.mapsforge.android.maps.overlay.OverlayWay;
//import org.mapsforge.core.GeoPoint;
//
//import java.util.LinkedList;
//import java.util.List;
//
//        import technotracks.ch.R;
//
///**
// * A class which capture tracks and display the map
// * @author Joel
// *
// */
//public class Capturing extends MapActivity
//{
//    private int track;
//    private LocationManager manager;
//    private LocationListener locationListener;
//    private int satelliteNumber;
//    private boolean capturing;
//    private ArrayWayOverlay path;
//    private List<GeoPoint> geoPoints;
//    private MapView map;
//    private MapController mc;
//    private OverlayItem start;
//    private OverlayItem stop;
//
//    /**
//     * Create the menu
//     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.tracking, menu);
//        return true;
//    }
//
//    /**
//     * Set icon and title of the menus
//     */
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu)
//    {
//        super.onPrepareOptionsMenu(menu);
//
//        MenuItem item = menu.findItem(R.id.capture);
//
//        if(item != null)
//        {
//            item.setTitle(R.string.notCapturing)
//                    .setIcon(R.drawable.device_access_location_searching);
//        }
//        return true;
//    }
//
//    /**
//     * Create the activity
//     */
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_capturing_advanced);
//
//        satelliteNumber = 0;
//        geoPoints = new LinkedList<GeoPoint>();
//        path = new ArrayWayOverlay(PaintFactory.getDefaultPaintFill(), PaintFactory.getDefaultPaintOutline());
//        start = null;
//        stop = null;
//
//		/* Get the selected track id and store it */
//        Intent i = getIntent();
//        Bundle b = i.getExtras();
//        if(b != null)
//        {
//            track = b.getInt("trackNumber");
//        }
//
//		/* Set the map and initialize it */
//        map = (MapView)findViewById(R.id.mapView);
//        map.setClickable(true);
//        map.setMapFile(DirectoryTools.getSwissMap());
//        map.setBuiltInZoomControls(true);
//        mc = map.getController();
//
//        putExistingMarkers();
//
//		/* GPS initialization */
//        capturing = false;
//        locationListener = new MyLocationListener();
//        GpsStatus.Listener gpsStatusListener = new MyGpsStatusListener();
//        manager = (LocationManager)getSystemService(LOCATION_SERVICE);
//        manager.addGpsStatusListener(gpsStatusListener);
//    }
//
//    /**
//     * Test if data exist for the selected track and put them on the map
//     */
//    private void putExistingMarkers()
//    {
//        Cursor c = DatabaseAccessObject.getTrackPoints(track);
//
//        if(c.getCount() > 0 && map != null)	//if the cursor is not empty (ie there are track points) and there is a map
//        {
//            GeoPoint coordinates;
//            double latitude;
//            double longitude;
//
//            c.moveToPosition(-1);
//
//			/* Prepare points to be added on the map */
//            while(c.moveToNext())
//            {
//                latitude = c.getDouble(1);
//                longitude = c.getDouble(0);
//
//                coordinates = new GeoPoint(latitude, longitude);
//                geoPoints.add(coordinates);	//filling the list
//            }
//
//            addStartPoint(geoPoints.get(0));
//            addStopPoint(geoPoints.get(geoPoints.size() - 1));
//            addPath();
//
//            map.setCenter(geoPoints.get(geoPoints.size() - 1));	//re-center the view on the current position
//            mc.setZoom(Constant.DEFAULT_ZOOM);	//set the zoom to the default
//
//            map.redrawTiles();	//refresh the map
//        }
//    }
//
//    /**
//     * Catch the menu item selection
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        int id = item.getItemId();
//        switch(id)
//        {
//            case R.id.capture :
//                if(!capturing)
//                {
//                    item.setTitle(R.string.capturing);
//                    item.setIcon(R.drawable.device_access_location_found);
//                    startCapture();
//                    return true;
//                }
//                else
//                {
//                    item.setTitle(R.string.notCapturing);
//                    item.setIcon(R.drawable.device_access_location_searching);
//                    stopCapture();
//                    return true;
//                }
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    /**
//     * Stop capturing and upload data if possible
//     */
//    private void stopCapture()
//    {
//        capturing = false;
//        manager.removeUpdates(locationListener);
//        if(NetworkTools.isNetworkAvailable(getApplicationContext()))
//            DatabaseAccessObject.save();
//    }
//
//    /**
//     * Start capturing data
//     */
//    private void startCapture()
//    {
//        capturing = true;
//        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constant.MIN_TIME, Constant.MIN_DISTANCE, locationListener);
//    }
//
//    /**
//     * Start the activity
//     */
//    @Override
//    protected void onStart()
//    {
//        super.onStart();
//
//		/* Check if GPS is activated and ask the user to activate it if not */
//        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
//        {
//            NoGPSDialog.showNoGPSDialog(this);
//        }
//    }
//
//    private void addStartPoint(GeoPoint coordinate)
//    {
//        start = new OverlayItem(coordinate, getText(R.string.currentLocation).toString(), getText(R.string.youAreHere).toString());
//        ArrayItemizedOverlay startOverlay = new ArrayItemizedOverlay(getResources().getDrawable(R.drawable.ic_maps_indicator_starting_position));
//        startOverlay.addItem(start);
//        map.getOverlays().add(startOverlay);
//    }
//
//    private void addStopPoint(GeoPoint coordinate)
//    {
//        stop = new OverlayItem(coordinate, getText(R.string.currentLocation).toString(), getText(R.string.youAreHere).toString());
//        ArrayItemizedOverlay stopOverlay = new ArrayItemizedOverlay(getResources().getDrawable(R.drawable.ic_maps_indicator_stop_position));
//        stopOverlay.addItem(stop);
//        map.getOverlays().add(stopOverlay);
//    }
//
//    private void addPath()
//    {
//        OverlayWay ow =
//                new OverlayWay(new GeoPoint[][]{geoPoints.toArray(new GeoPoint[geoPoints.size()])});
//        path.addWay(ow);
//        map.getOverlays().add(path);	//adding path to the map
//    }
//
//    private void addCurrentLocation(GeoPoint coordinates)
//    {
//        OverlayItem item = new OverlayItem(coordinates, getText(R.string.currentLocation).toString(), getText(R.string.youAreHere).toString());	//creating the item to display current location
//        ArrayItemizedOverlay itemizedOverlay = new ArrayItemizedOverlay(getResources().getDrawable(R.drawable.ic_maps_indicator_current_position));	//creating the array containing all overlayItem
//        itemizedOverlay.addItem(item);	//adding item to the array
//        map.getOverlays().add(itemizedOverlay);	//adding the item overlay to the map
//    }
//
//    /**
//     * Destroy the activity and stop the gps listening
//     */
//    @Override
//    protected void onDestroy()
//    {
//        manager.removeUpdates(locationListener);	//stop the GPS listening
//        super.onDestroy();
//    }
//
//
//    /**
//     * A custom location listener. Write data in db and update displays
//     * @author Joel
//     *
//     */
//    private class MyLocationListener implements LocationListener
//    {
//        /**
//         * Called when the current location change
//         */
//        @Override
//        public void onLocationChanged(Location location)
//        {
//            double latitude = location.getLatitude();
//            double longitude = location.getLongitude();
//            double altitude = location.getAltitude();
//            float speed = location.getSpeed();
//            float bearing = location.getBearing();
//            float accuracy = location.getAccuracy();
//            long time = location.getTime();
//
//            update(latitude, longitude);	//update the display
//
//            saveInDB(latitude, longitude, altitude, speed, bearing, accuracy, time);	//save in db
//        }
//
//        /**
//         * Update display
//         * @param latitude
//         * The latitude
//         * @param longitude
//         * The longitude
//         */
//        private void update(double latitude, double longitude)
//        {
//            if(map != null)	//if we have a map
//            {
//				/* Basically do the same as in putExistingMarker and add a marker on the current location */
//                GeoPoint coordinates = new GeoPoint(latitude, longitude);
//
//                geoPoints.add(coordinates);
//
//                addPath();
//
//                if(start == null)
//                {
//                    addStartPoint(coordinates);
//                }
//
//                addCurrentLocation(coordinates);
//
//                map.setCenter(coordinates);
//                map.redrawTiles();
//            }
//        }
//
//        /**
//         * Save data for the point in DB
//         * @param latitude
//         * the latitude
//         * @param longitude
//         * the longitude
//         * @param altitude
//         * the altitude
//         * @param speed
//         * the speed
//         * @param bearing
//         * the bearing
//         * @param accuracy
//         * the accuracy
//         * @param time
//         * the time
//         */
//        private void saveInDB(double latitude, double longitude, double altitude, float speed, float bearing, float accuracy, long time)
//        {
//            DatabaseAccessObject.writePoint(track, latitude, longitude, altitude, speed, bearing, accuracy, time, satelliteNumber);
//        }
//
//        /**
//         * Called when gps is disabled in settings
//         */
//        @Override
//        public void onProviderDisabled(String provider)
//        {
//            stopCapture();	// when GPS is disabled in settings stop capturing
//        }
//
//        @Override
//        public void onProviderEnabled(String provider)
//        {}	// Useless
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras)
//        {}	// Useless
//    }
//
//
//    /**
//     * A custom gps status listener. Update the satellite number
//     * @author Joel
//     *
//     */
//    private class MyGpsStatusListener implements GpsStatus.Listener
//    {
//        /**
//         * Called when the gps status change (typically when the number of satellites change)
//         */
//        @Override
//        public void onGpsStatusChanged(int event)
//        {
//            if(event == GpsStatus.GPS_EVENT_SATELLITE_STATUS)
//            {
//                int currentSatelliteNumber = getSatelliteNumber();
//
//				/* if we can update display we do it
//				   update only if satellite number change */
//                if(currentSatelliteNumber != satelliteNumber)
//                {
//                    satelliteNumber = currentSatelliteNumber;
//                }
//            }
//        }
//
//        /**
//         * Give the number of satellite currently locked
//         * @return
//         * The number of satellites
//         */
//        private int getSatelliteNumber()
//        {
//            int satNumber = 0;
//
//			/* Count the number of satellites */
//            GpsStatus gpsStatus = manager.getGpsStatus(null);
//            for (GpsSatellite ignored : gpsStatus.getSatellites())
//            {
//                satNumber++;
//            }
//
//            return satNumber;
//        }
//    }
//
//
//    /**
//     * A factory for creating Paint element for the track
//     * @author Joel
//     *
//     */
//    private abstract static class PaintFactory
//    {
//        /**
//         * The default paint for filling path
//         * @return
//         * A paint with default fill configuration
//         */
//        public static Paint getDefaultPaintFill()
//        {
//            Paint tmp = defaultOption();
//            tmp.setAlpha(160);
//            tmp.setPathEffect(new DashPathEffect(new float[] { 20, 20  }, 0));
//
//            return tmp;
//        }
//
//        /**
//         * The default paint for outlining path
//         * @return
//         * A paint with default outline configuration
//         */
//        public static Paint getDefaultPaintOutline()
//        {
//            Paint tmp = defaultOption();
//            tmp.setAlpha(128);
//
//            return tmp;
//        }
//
//        /**
//         * A basic paint
//         * @return
//         * A basic paint with default option
//         */
//        private static Paint defaultOption()
//        {
//            Paint tmp = new Paint(Paint.ANTI_ALIAS_FLAG);
//            tmp.setStyle(Paint.Style.STROKE);
//            tmp.setColor(Color.BLUE);
//            tmp.setStrokeWidth(7);
//            tmp.setStrokeJoin(Paint.Join.ROUND);
//
//            return tmp;
//        }
//    }
//}
