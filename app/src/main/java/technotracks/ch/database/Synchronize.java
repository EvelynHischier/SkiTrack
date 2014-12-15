package technotracks.ch.database;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import ch.technotracks.backend.trackApi.TrackApi;
import ch.technotracks.backend.trackApi.model.Track;

/**
 * Created by Evelyn on 12.12.2014.
 */
public class Synchronize {

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

            try {
                // insert into app engine
                for(Track track : tracks) {
                    myService.insert(track).execute();
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
