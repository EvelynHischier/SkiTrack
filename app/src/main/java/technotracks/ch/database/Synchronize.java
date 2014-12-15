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

    public class SyncTrack extends AsyncTask<Void, Void, List<Track>>{
        private Context context;
        private TrackApi myService = null;

        public SyncTrack(Context context){
            this.context = context;
        }

        @Override
        protected List<Track> doInBackground(Void... params) {

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
                return myService.list().execute().getItems();    //listQuote().execute().getItems();
            } catch (IOException e) {
                return Collections.EMPTY_LIST;
            }
        }

        @Override
        protected void onPostExecute(List<Track> result) {
            for (Track q : result) {
                Toast.makeText(context, q.getName()+" : "+q.getId(), Toast.LENGTH_LONG).show();
            }
        }
    }


}
