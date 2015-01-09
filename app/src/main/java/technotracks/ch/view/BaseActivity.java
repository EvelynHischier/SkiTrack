package technotracks.ch.view;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import ch.technotracks.backend.trackApi.model.Track;
import technotracks.ch.R;
import technotracks.ch.controller.SessionManager;
import technotracks.ch.database.DatabaseAccess;
import technotracks.ch.database.SQLHelper;
import technotracks.ch.database.Synchronize;


@SuppressWarnings("deprecation")
public class BaseActivity extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        String[] navMenuTitles = getResources().getStringArray(
                R.array.nav_drawer_items);

        TypedArray navMenuIcons = getResources().obtainTypedArray(
                R.array.nav_drawer_icons); // load icons from strings.xml

        set(navMenuTitles, navMenuIcons);

    }

    public void set(String[] navMenuTitles, TypedArray navMenuIcons) {
        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        navDrawerItems = new ArrayList<>();

        // adding nav drawer items
        if (navMenuIcons == null) {
            for (String navMenuTitle : navMenuTitles) {
                navDrawerItems.add(new NavDrawerItem(navMenuTitle));
            }
        } else {
            for (int i = 0; i < navMenuTitles.length; i++) {
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[i],
                        navMenuIcons.getResourceId(i, -1)));
            }
        }

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        try {
            if (getActionBar() != null) {
                getActionBar().setDisplayHomeAsUpEnabled(true);
                getActionBar().setHomeButtonEnabled(true);
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        //getSupportActionBar().setIcon(R.drawable.ic_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, // nav menu toggle icon
                R.string.app_name, // drawer open - for accessibility
                R.string.app_name // drawer close - for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        // boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        // menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    private void displayView(int position) {
        Intent intent = null;
        // update the main content by replacing fragments

        switch (position) {
            case 0:
                intent = new Intent(this, RecordTrackActivity.class);
                break;
            case 1:
                intent = new Intent(this, TrackHistoryActivity.class);
                break;
            case 2:
                intent = new Intent(this, AndroidDatabaseManager.class);
                break;
            case 3:
                SessionManager sm = new SessionManager(this);
                sm.logoutUser();
                intent = new Intent(this, LoginActivity.class);
                break;
            default:
                break;
        }

        // update selected item and title, then close the drawer
        updateAndCloseDrawer(position);
        startActivity(intent);
    }

    public void updateAndCloseDrawer(int position){
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        mDrawerLayout.closeDrawer(mDrawerList);
    }
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        if (getActionBar() != null)
            getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    public void buttonSync(View view){
        Synchronize sync = new Synchronize();

        // Synchronize tracks & gps
        List<Track> tracks = DatabaseAccess.readTrack(this);
        sync.new SyncTrack(this, tracks).execute();

        for (Track track : tracks){
            DatabaseAccess.updateToSynced(this, track.getIdLocal(), SQLHelper.TABLE_NAME_TRACK, SQLHelper.TRACK_ID);
        }
    }
}
