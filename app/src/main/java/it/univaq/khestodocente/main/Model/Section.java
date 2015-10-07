package it.univaq.khestodocente.main.Model;

/**
 * Created by beniamino on 06/10/15.
 */
public class Section {

    private long id;            // id
    private String title;       // name
    private String description; // summary
    private String number; //section


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
