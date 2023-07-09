package com.yong.wesave.apiobject;

/**
 * Created by Yong0156 on 28/2/2017.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LocationModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6112846960022663628L;
    private String id;
    private String name;
    private double lng;
    private double lat;
    private String address;
    private String iconURL;
    private double distance; //in meters
    private String locationCategory;
    private List<String> tags = new ArrayList<String>();
    private boolean custom = false;

    public LocationModel() {
    }

    public LocationModel(double lng, double lat, String name) {
        this.lng = lng;
        this.lat = lat;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getLocationCategory() {
        return locationCategory;
    }

    public void setLocationCategory(String locationCategory) {
        this.locationCategory = locationCategory;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public double getLng() {
        return lng;
    }


    public void setLng(double lng) {
        this.lng = lng;
    }


    public double getLat() {
        return lat;
    }


    public void setLat(double lat) {
        this.lat = lat;
    }


    public String getIconURL() {
        return iconURL;
    }


    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }


    public double getDistance() {
        return distance;
    }


    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void addTags(String tag) {
        tags.add(tag);
    }

    public List<String> getTags() {
        return tags;
    }


    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

}
