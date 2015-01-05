package ch.technotracks.bean;

import java.io.Serializable;
import java.util.List;

import ch.technotracks.backend.GPSData;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * Created by Evelyn on 05.01.2015.
 */

@ManagedBean
@SessionScoped
public class ServiceBean  implements Serializable{

    public List<GPSData> getGPSData(){

        return null;
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
