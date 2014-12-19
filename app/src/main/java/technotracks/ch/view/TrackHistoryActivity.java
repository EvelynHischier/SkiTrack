package technotracks.ch.view;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import ch.technotracks.backend.trackApi.model.Track;
import technotracks.ch.R;
import technotracks.ch.database.DatabaseAccess;

public class TrackHistoryActivity extends BaseActivity {

    public static final String EXTRA_TRACK_ID = "Track_ID";
    private List<Track> allTracks;
    private List<String> allTitles;


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

        allTracks = DatabaseAccess.readTrackHistory(this);

        allTitles = new ArrayList<String>();

        for (Track t : allTracks){
            String historyText = t.getCreate() + " - " + t.getName() + " - Synched: " + t.getSync();
            allTitles.add(historyText);
        }

        listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, allTitles));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Track clicked = allTracks.get(position);
                Intent i = new Intent(getApplicationContext(), ShowMapActivity.class);
                i.putExtra(EXTRA_TRACK_ID, clicked.getIdLocal());
                startActivity(i);
            }
        });
    }

}
