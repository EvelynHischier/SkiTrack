package technotracks.ch.skitrack_scrum;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import technotracks.ch.R;

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

        //EDITED Code
        String[] items = new String[] {"Item 1", "Item 2", "Item 3"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        listview.setAdapter(adapter);

    }

}
