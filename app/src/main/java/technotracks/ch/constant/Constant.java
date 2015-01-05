package technotracks.ch.constant;

/**
 * Created by Blechfalke on 16.12.14.
 */
public class Constant {
    // downloadMap
    public static final String MAP_SERVER_URL = "http://download.mapsforge.org/maps/europe/switzerland.map";
    public static final String URL_TO_UPLOAD_DATA = "http://vlhiigdev.hevs.ch:8080/TechnoTracksServer/rest/TechnoTracksServer";

    // captureDisplay
    public static final long MIN_TIME = 30 * 1000;
    public static final float MIN_DISTANCE = 0f;
    public static final int DEFAULT_ZOOM = 15;

    public static final String MAP_FOLDER_NAME = "maps";
    public static final String TECHNOTRACKS_FOLDER_NAME = "TechnoTracks";
    public static final String EXPORT_FOLDER_NAME = "export";
    public static final String CSV_FOLDER_NAME = "csv";

    // Recording Parameters
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // The fastest update frequency, in seconds
    public static final int FASTEST_INTERVAL_IN_SECONDS = 3;
    // A fast frequency ceiling in milliseconds
    public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
            * FASTEST_INTERVAL_IN_SECONDS;
    // Update frequency in milliseconds
    public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
            * UPDATE_INTERVAL_IN_SECONDS;
}
