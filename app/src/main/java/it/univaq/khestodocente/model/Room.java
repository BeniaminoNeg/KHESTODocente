package it.univaq.khesto.model;

/**
 * Created by developer on 28/10/14.
 */
public class Room {

    private long id;
    private String surname;
    private long id_professor;
    private String professorname;
    private String title;
    private int cfu = 0;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public long getId_professor() {
        return id_professor;
    }

    public void setId_professor(long id_professor) {
        this.id_professor = id_professor;
    }

    public String getProfessorname() {
        return professorname;
    }

    public void setProfessorname(String professorname) {
        this.professorname = professorname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCfu() {
        return cfu;
    }

    public void setCfu(int cfu) {
        this.cfu = cfu;
    }
}
