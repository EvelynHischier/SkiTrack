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
    private Long idLocal;
    private String name;
    private Date create;
    private boolean sync;

    /* ************************************************************
     * 					Constructors
     **************************************************************/
    public Track(){
        gps = new ArrayList<GPSData>();
        sync = false;
    }

    public Track(String name, Date create) {
        this.name = name;
        this.create = create;

        gps = new ArrayList<GPSData>();
        sync = false;
    }
    /* ************************************************************
     * 					Relations
     **************************************************************/
    private User user;

    private List<GPSData> gps;

    private Vehicle vehicle;

	/* ************************************************************
	 * 					Helper methods
	 **************************************************************/
    //@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void addGPSData(GPSData gpsdata){
        gps.add(gpsdata);
    }
   // @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void removeGPSData(GPSData gpsData){
        gps.remove(gpsData);
    }


    @Override
    public String toString() {
        return create.getYear()+"-"+create.getMonth()+"-"+create.getDay() +" : " + name;
    }

    /* ************************************************************
             * 					Getters & Setters
             **************************************************************/
    public Long getIdLocal() {
        return idLocal;
    }
    public void setIdLocal(Long idLocal) {
        this.idLocal = idLocal;
    }
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<GPSData> getGps() {
        return gps;
    }

    public void setGps(List<GPSData> gps) {
        this.gps = gps;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}
