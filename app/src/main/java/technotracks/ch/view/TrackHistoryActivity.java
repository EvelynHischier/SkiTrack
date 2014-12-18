package technotracks.ch.view;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
import ch.technotracks.backend.trackApi.model.Track;
import technotracks.ch.R;
import technotracks.ch.database.DatabaseAccess;

public class TrackHistoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_history);

        String[] navMenuTitles = getResources().getStringArray(
                R.array.nav_drawer_items);

        TypedArray navMenuIcons = getResources().obtainTypedArray(
                R.array.nav_drawer_icons); // load icons from strings.xml

        set(navMenuTitles, navMenuIcons);

        //now you must initialize your list view
        ListView listview =(ListView)findViewById(R.id.list_track_history);

        List<Track> tracks = DatabaseAccess.readTrack(this);

        String[] trackTitles = new String[tracks.size()];
        int i = 0;
        for (Track t : tracks){
            trackTitles[i++] = t.toString();
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, trackTitles);

        listview.setAdapter(adapter);

    }

}
