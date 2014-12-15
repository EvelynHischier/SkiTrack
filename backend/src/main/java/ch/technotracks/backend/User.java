package ch.technotracks.backend;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.datastore.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evelyn on 12.12.2014.
 */
@Entity
public class User{
    @Id
    private Long id;
    private String firstname;
    private String lastname;
    private String password;
    private String eMail;
    private String phoneNumber;
    private boolean takePartChampionship;

    /* ************************************************************
     * 					Constructors
     **************************************************************/
    public User(){
        tracks = new ArrayList<Ref<Track>>();
        championships = new ArrayList<Ref<Championship>>();
    }
    public User(String firstname, String lastname, String password, String eMail, String phoneNumber, boolean championship) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.eMail = eMail;
        this.phoneNumber = phoneNumber;
        this.takePartChampionship = championship;

        tracks = new ArrayList<Ref<Track>>();
        championships = new ArrayList<Ref<Championship>>();
    }
	/* ************************************************************
	 * 					Relations
	 **************************************************************/
    private List<Ref<Track>> tracks;
    private List<Ref<Championship>> championships;

    /* ************************************************************
     * 					Helper methods
     **************************************************************/
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void addTrack(Ref<Track> track){
        tracks.add(track);
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void removeTrack(Track track){
        tracks.remove(track);
    }

    public void addChampionship(Ref<Championship> championship){
        championships.add(championship);
    }
    public void removeChampionship(Championship championship){
        championships.remove(championship);
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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isChampionship() {
        return takePartChampionship;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setChampionship(boolean championship) {
        this.takePartChampionship = championship;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public boolean isTakePartChampionship() {
        return takePartChampionship;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setTakePartChampionship(boolean takePartChampionship) {
        this.takePartChampionship = takePartChampionship;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public List<Ref<Track>> getTracks() {
        return tracks;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setTracks(List<Ref<Track>> tracks) {
        this.tracks = tracks;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public List<Ref<Championship>> getChampionships() {
        return championships;
    }

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    public void setChampionships(List<Ref<Championship>> championships) {
        this.championships = championships;
    }
}
