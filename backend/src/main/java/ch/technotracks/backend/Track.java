package ch.technotracks.backend;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Evelyn on 12.12.2014.
 */
@Entity
public class Track implements Serializable{
    @Id
    private Long id;
    private String name;
    private Date create;
    private boolean sync;

    /* ************************************************************
     * 					Constructors
     **************************************************************/
    public Track(){
        gps = new ArrayList<Key<GPSData>>();
        sync = false;
    }

    public Track(String name, Date create) {
        this.name = name;
        this.create = create;

        gps = new ArrayList<Key<GPSData>>();
        sync = false;
    }
    /* ************************************************************
     * 					Relations
     **************************************************************/
    private Key<User> user;

    private List<Key<GPSData>> gps;

    private Key<Vehicle> vehicle;

	/* ************************************************************
	 * 					Helper methods
	 **************************************************************/
    //@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void addGPSData(Key<GPSData> gpsdata){
        gps.add(gpsdata);
    }
   // @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void removeGPSData(GPSData gpsData){
        gps.remove(gpsData);
    }

    /* ************************************************************
     * 					Getters & Setters
     **************************************************************/
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Date getCreate() {
        return create;
    }
    public void setCreate(Date create) {
        this.create = create;
    }

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<User> getUser() {
        return user;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setUser(Key<User> user) {
        this.user = user;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public List<Key<GPSData>> getGps() {
        return gps;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setGps(List<Key<GPSData>> gps) {
        this.gps = gps;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public Key<Vehicle> getVehicle() {
        return vehicle;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setVehicle(Key<Vehicle> vehicle) {
        this.vehicle = vehicle;
    }
}
