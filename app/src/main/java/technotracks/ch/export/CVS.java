package technotracks.ch.export;

import android.database.Cursor;
import android.os.Environment;

import java.io.File;
import java.util.List;

import ch.technotracks.backend.gPSDataApi.model.GPSData;
import technotracks.ch.constant.Constant;

/**
 * Created by Evelyn on 19.12.2014.
 */
public class CVS {

    private List<GPSData> gpsDatas;

    public CVS(List<GPSData> gpsDatas){

    }

    public static File getCSVExportDirectory()
    {
        String path = getAppDirectory().getAbsolutePath() + File.separator
                + Constant.EXPORT_FOLDER_NAME + File.separator + Constant.CSV_FOLDER_NAME;

        return directory(path);
    }

    private static File getAppDirectory()
    {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + Constant.TECHNOTRACKS_FOLDER_NAME;

        return directory(path);
    }

    private static File directory(String path)
    {
        File file = new File(path);

        if(!file.exists())
        {
            if(!file.mkdirs())
            {
                return null;
            }
        }

        return file;
    }
}
