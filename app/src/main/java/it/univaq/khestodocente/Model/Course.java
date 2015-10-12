package it.univaq.khestodocente.Model;

import java.util.ArrayList;

/**
 * Created by beniamino on 05/10/15.
 */
public class Course {

    private long id;            // id
    private String name;        // fullname
    private String description; // summary

    private ArrayList<Section> sections;
    private ArrayList<Chat> chats;
    private ArrayList<File> files;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<Section> getSections() {
        return sections;
    }

    public Section getSection (long idSection) {
        Section sectionCercata = null;
        boolean trovata = false;
        for (int i=0; i<sections.size() && !trovata; i++){
            if (sections.get(i).getId() == idSection){
                trovata = true;
                sectionCercata = sections.get(i);
            }
        }
        return sectionCercata;
    }

    public void setSections(ArrayList<Section> sections) {
        this.sections = sections;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public File getFile (long idFile){
        File fileCercato = null;
        boolean trovato = false;
        for (int i=0; i<files.size() && !trovato; i++){
            if (files.get(i).getId() == idFile){
                trovato = true;
                fileCercato = files.get(i);
            }
        }
        return  fileCercato;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    public ArrayList<Chat> getChats() {
        return chats;
    }

    public Chat getChat (long id){
        Chat chatcercata = null;
        boolean trovata = false;
        for (int i=0; i<chats.size() && !trovata; i++){
            if (chats.get(i).getId() == id){
                trovata = true;
                chatcercata = chats.get(i);
            }
        }
        return chatcercata;
    }

    public void setChats(ArrayList<Chat> chats) {
        this.chats = chats;
    }
}
