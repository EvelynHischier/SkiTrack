package technotracks.ch.common;

import android.os.Environment;

import com.google.api.client.util.Lists;

import java.io.File;
import java.util.ArrayList;

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