package it.univaq.khestodocente.main.Model;

import android.support.annotation.NonNull;

/**
 * Created by beniamino on 05/10/15.
 */
public class File implements Comparable<File>{

    private long id;            // id
    private String filename;    // filename
    private String mimetype;    // mimetype
    private String url;         // url
    private String author;      // author
    private long size;          // filesize
    private long time;          // timemodified
    private long userid;        // userid
    private long sectionid;     // section


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public long getSectionid() {
        return sectionid;
    }

    public void setSectionid(long sectionid) {
        this.sectionid = sectionid;
    }

    @Override
    public int compareTo(@NonNull File file) {

        int result = 0;
        if (this.time > (file.getTime()))
        {
            result = -1;
        }
        else if (this.time == (file.getTime()))
        {
            result = 0;
        }
        else if (this.time < (file.getTime()))
        {
            result = 1;
        }
        return result;
    }


}
