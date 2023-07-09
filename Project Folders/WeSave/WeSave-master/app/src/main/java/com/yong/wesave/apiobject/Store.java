package com.yong.wesave.apiobject;

/**
 * Created by Yong on 19/1/2017.
 */

public class Store {

    public String id;
    public String store_name;
    public String type;
    public String image;

    public String getName() {
        return store_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStoreName(String name) {
        this.store_name = name;
    }

    public void setType(String cat) {
        this.type = cat;
    }

    public void setImage(String img) {
        this.image = img;
    }
}
