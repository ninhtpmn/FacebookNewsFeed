package com.example.ninh.facenewsprt3;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by ninh on 22/06/2015.
 */
public class Item {
    private Bitmap image;
    private String idn;
    private String title;
    private String time;
    private String link;
    private String id_person;

    public Item(Bitmap image, String idn, String title, String time, String link, String id_person) {
        super();
        this.image = image;
        this.title = title;
        this.time = time;
        this.link = link;
        this.idn = idn;
        this.id_person = id_person;
    }
    //
    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }

    public String getIDn()
    {
        return idn;
    }

    public void setIdn(String idn)
    {
        this.idn = idn;
    }

    public String getPersonId()
    {
        return id_person;
    }

    public void setPersonId(String id_person)
    {
        this.id_person = id_person;
    }
}
