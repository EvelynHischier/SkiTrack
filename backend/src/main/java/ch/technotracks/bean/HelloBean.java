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
import java.util.List;
import java.util.logging.Logger;

import ch.technotracks.backend.GPSData;
import ch.technotracks.backend.GPSDataEndpoint;

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
public class HelloBean implements Serializable{

    public Collection<GPSData> getGPSData(){

        String cursor = null;
        CollectionResponse list = new GPSDataEndpoint().list(cursor, 100);


        return list.getItems();
    }

    private static final long serialVersionUID = 1L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
