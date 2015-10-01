package it.univaq.khestodocente.main.Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by beniamino on 07/09/15.
 */
public class User {
    private long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String lang;
    private String picture_url;
    private ArrayList <String> lastcourses;

    public User(String email, String username, String picture_url, String lastname, ArrayList<String> lastcourses, String lang, long id, String firstname) {
        this.email = email;
        this.username = username;
        this.picture_url = picture_url;
        this.lastname = lastname;
        this.lastcourses = lastcourses;
        this.lang = lang;
        this.id = id;
        this.firstname = firstname;
    }

    public ArrayList<String> getLastcourses() {
        return lastcourses;
    }

    public void setLastcourses(ArrayList<String> lastcourses) {
        this.lastcourses = lastcourses;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }



}
