package ch.technotracks.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;

/**
 * Created by Evelyn on 16.12.2014.
 */
@Entity
public class Vehicle {

    @Id
    private Long id;
    private String name;

    /* ************************************************************
     * 					Constructors
     **************************************************************/
    public Vehicle() {
    }

    public Vehicle(String name) {
        this.name = name;
    }

    /* ************************************************************
     * 					Helper methods
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
}
