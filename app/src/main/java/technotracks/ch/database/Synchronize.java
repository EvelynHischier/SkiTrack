package technotracks.ch.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import ch.technotracks.backend.gPSDataApi.GPSDataApi;
import ch.technotracks.backend.gPSDataApi.model.GPSData;
import ch.technotracks.backend.trackApi.TrackApi;
import ch.technotracks.backend.trackApi.model.Track;
import ch.technotracks.backend.userApi.UserApi;
import ch.technotracks.backend.userApi.model.User;

/**
 * Created by Evelyn on 12.12.2014.
 */
public class Synchronize {

    public  void synchronizeAll(){
        //TODO synchronizeAll --> für alle
    }

    /* ***********************************************************************
     *                       Track
     *************************************************************************/
    public class SyncTrack extends AsyncTask<Void, Void, Long>{
        private Context context;
        private TrackApi myService = null;
        private List<Track> tracks;

        public SyncTrack(Context context, List<Track> tracks){
            this.context = context;
            this.tracks = tracks;
        }

        @Override
        protected Long doInBackground(Void... params) {

            // singleton
            if (myService == null){
                TrackApi.Builder builder = new TrackApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("https://skilful-union-792.appspot.com/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer(){

                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);}
                        });
                myService = builder.build();
            }

            Log.e("start uploading track", "-------------------------------");
            try {
                // insert into app engine
                for(Track track : tracks) {
                    track = myService.insert(track).execute();

                    Log.e("id track", track.getId()+"");
                    new SyncGPSData(context, DatabaseAccess.readGPSData(context, track.getId())).execute();
                }
                Log.e("finished uploading track", "-------------------------------");
                return 1l;

                //get
                //return myService.list().execute().getItems();
            } catch (IOException e) {
                Log.e("error while uploading track", e.getMessage());
                return -1l;
            }
        }
    }

    /* ***********************************************************************
     *                       User
     *************************************************************************/
    public class UploadUser extends AsyncTask<Void, Void, Long>{
        private Context context;
        private UserApi myService = null;
        private List<User> users;

        public UploadUser(Context context, List<User> users){
            this.context = context;
            this.users = users;
        }

        @Override
        protected Long doInBackground(Void... params) {

            // singleton
            if (myService == null){
                UserApi.Builder builder = new UserApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("https://skilful-union-792.appspot.com/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer(){
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);}
                        });
                myService = builder.build();
            }

            try {
                // insert into app engine
                for(User user : users) {
                    myService.insert(user).execute();
                }
                return 1l;

                //get
                //return myService.list().execute().getItems();
            } catch (IOException e) {
                return -1l;
            }
        }
    }

    public class DownlaodUser extends AsyncTask<Void, Void, List<User>>{
        private Context context;
        private UserApi myService = null;
        private List<User> users;

        public DownlaodUser(Context context){
            this.context = context;
        }

        @Override
        protected List<User> doInBackground(Void... params) {

            // singleton
            if (myService == null){
                UserApi.Builder builder = new UserApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("https://skilful-union-792.appspot.com/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer(){
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);}
                        });
                myService = builder.build();
            }

            try {
                //get
                return myService.list().execute().getItems();
            } catch (IOException e) {
                return Collections.emptyList();
            }
        }
    }
    /* ***********************************************************************
     *                       GPSData
     *************************************************************************/
    public class SyncGPSData extends AsyncTask<Void, Void, Long>{
        private Context context;
        private GPSDataApi myService = null;
        private List<GPSData> gpss;

        public SyncGPSData(Context context, List<GPSData> gpss){
            this.context = context;
            this.gpss = gpss;
        }

        @Override
        protected Long doInBackground(Void... params) {

            // singleton
            if (myService == null){
                GPSDataApi.Builder builder = new GPSDataApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("https://skilful-union-792.appspot.com/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer(){
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);}
                        });
                myService = builder.build();
            }

            try {
                // insert into app engine
                for(GPSData gps : gpss) {
                    myService.insert(gps).execute();
                }
                return 1l;

                //get
                //return myService.list().execute().getItems();
            } catch (IOException e) {
                return -1l;
            }
        }
    }
}
