package com.mycompany.concertschedule.models;

import android.media.Image;

public class Concert {
    private String image, title, desc, date, time, place, price;

    public Concert(){}

    public Concert(String image, String title, String desc, String date, String time, String place, String price) {
        this.image = image;
        this.title = title;
        this.desc = desc;
        this.date = date;
        this.time = time;
        this.place = place;
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Concert(String image, String title, String date, String place) {
        this.image = image;
        this.title = title;
        this.date = date;
        this.place = place;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
