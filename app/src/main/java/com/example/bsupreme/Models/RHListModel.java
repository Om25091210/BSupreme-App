package com.example.bsupreme.Models;

import com.google.firebase.firestore.Exclude;
import com.google.type.LatLng;

public class RHListModel {
    private boolean is_bar;
    private boolean is_room;
    private boolean is_table;
    private boolean is_veg;
    private String name, dest, desc;
    private long rating;
    private String image;
    private LatLng location;

    public boolean isIs_bar() {
        return is_bar;
    }

    public void setIs_bar(boolean is_bar) {
        this.is_bar = is_bar;
    }

    public boolean isIs_room() {
        return is_room;
    }

    public void setIs_room(boolean is_room) {
        this.is_room = is_room;
    }

    public boolean isIs_table() {
        return is_table;
    }

    public void setIs_table(boolean is_table) {
        this.is_table = is_table;
    }

    public boolean isIs_veg() {
        return is_veg;
    }

    public void setIs_veg(boolean is_veg) {
        this.is_veg = is_veg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getRating() {
        return rating;
    }

    public void setRating(long rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Exclude
    public LatLng getLocation() {
        return location;
    }

    @Exclude
    public void setLocation(LatLng location) {
        this.location = location;
    }

    public RHListModel() {
    }

    public RHListModel(boolean is_bar, boolean is_room, boolean is_table, boolean is_veg, String name, String dest, String desc, long rating, String image, LatLng location) {
        this.is_bar = is_bar;
        this.is_room = is_room;
        this.is_table = is_table;
        this.is_veg = is_veg;
        this.name = name;
        this.dest = dest;
        this.desc = desc;
        this.rating = rating;
        this.image = image;
        this.location = location;
    }
}
