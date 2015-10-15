package it.univaq.khestodocente.model;

import java.util.ArrayList;

/**
 * Created by beniamino on 07/09/15.
 */
public class User {
    private long idmoodle;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String address;
    private String city;
    private String country;
    private String picture;
    private ArrayList <Course> courses;


    public long getIdmoodle() {
        return idmoodle;
    }

    public void setIdmoodle(long idmoodle) {
        this.idmoodle = idmoodle;
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

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public ArrayList<Course> getCourses() {
        return courses;
    }

    public Course getCourse (long idCourse) {
        boolean trovato = false;
        Course corsoCercato = null;
        for (int i=0; i<courses.size()&&!trovato; i++){
            if (courses.get(i).getId() == idCourse){
                trovato=true;
                corsoCercato=courses.get(i);
            }
        }
        return corsoCercato;
    }

    public void setCourses(ArrayList<Course> courses) {
        this.courses = courses;
    }
}
