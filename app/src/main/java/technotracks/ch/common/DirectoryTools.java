package technotracks.ch.common;

import android.os.Environment;

import java.io.File;

import technotracks.ch.constant.Constant;

public abstract class DirectoryTools
{
    /**
     * Give the switzerland map file
     * @return
     * The file
     */
    public static File getSwissMap()
    {
        String path = getMapsDirectory().getAbsolutePath() + File.separator + "switzerland.map";

        return new File(path);
    }

    /**
     * Give the directory for CSV export
     * @return
     * The file
     */
    public static File getCSVExportDirectory()
    {
        String path = getAppDirectory().getAbsolutePath() + File.separator
                + Constant.EXPORT_FOLDER_NAME + File.separator + Constant.CSV_FOLDER_NAME;

        return directory(path);
    }

    /**
     * Give the directory for KML export
     * @return
     * The file
     */
    public static File getKMLExportDirectory()
    {
        String path = getAppDirectory().getAbsolutePath() + File.separator
                + Constant.EXPORT_FOLDER_NAME + File.separator + Constant.KML_FOLDER_NAME;

        return directory(path);
    }

    /**
     * Give the maps directory
     * @return
     * The file
     */
    public static File getMapsDirectory()
    {
        String path = getAppDirectory().getAbsolutePath() + File.separator + Constant.MAP_FOLDER_NAME;

        return directory(path);
    }

    /**
     * Give the app directory
     * @return
     * The file
     */
    private static File getAppDirectory()
    {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + Constant.TECHNOTRACKS_FOLDER_NAME;

        return directory(path);
    }

    /**
     * Give the file directory with the given path.
     * If the directory does not exist creates it and its ancestor
     * @param path
     * The path to the directory
     * @return
     * The directory file
     */
    private static File directory(String path)
    {
        File f = new File(path);

        if(!f.exists())
        {
            if(!f.mkdirs())
            {
                return null;
            }
        }

        return f;
    }
}