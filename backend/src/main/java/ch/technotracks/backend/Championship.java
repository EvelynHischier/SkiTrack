package ch.technotracks.backend;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.google.appengine.api.datastore.Key;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Evelyn on 12.12.2014.
 */
@Entity
public class Championship{

    @Id
    private Long id;
    private Date start;
    private Date end;
    private String name;

    /* ************************************************************
     * 					Constructors
     **************************************************************/
    public Championship(){
        users =  new ArrayList<Ref<User>>();
    }

    public Championship(Date start, Date end) {
        this.start = start;
        this.end = end;

        users = new ArrayList<Ref<User>>();
    }

    /* ************************************************************
     * 					Relations
     **************************************************************/
    private List<Ref<User>> users;

    /* ************************************************************
     * 					Helper methods
     **************************************************************/
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void addUser(Ref<User> user){
        users.add(user);
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void removeUser(User user){
        users.remove(user);
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
    public Date getStart() {
        return start;
    }
    public void setStart(Date start) {
        this.start = start;
    }
    public Date getEnd() {
        return end;
    }
    public void setEnd(Date end) {
        this.end = end;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public List<Ref<User>> getUsers() {
        return users;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setUsers(List<Ref<User>> users) {
        this.users = users;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
