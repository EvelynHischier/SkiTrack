package ch.technotracks.bean;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import ch.technotracks.backend.GPSData;
import ch.technotracks.backend.GPSDataEndpoint;
import ch.technotracks.export.CSVFile;

import javax.annotation.Nullable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by Evelyn on 05.01.2015.
 */

@ManagedBean
@SessionScoped
public class ServiceBean implements Serializable{

    private static final long serialVersionUID = 1L;

    public Collection<GPSData> getGPSData(){

        String cursor = null;
        CollectionResponse list = new GPSDataEndpoint().list(cursor, 100);

        return list.getItems();
    }

    public String getCSVText(){
        Collection list = getGPSData();
        Date today = new Date();

        return CSVFile.generateCSVFile(list);
    }


}
