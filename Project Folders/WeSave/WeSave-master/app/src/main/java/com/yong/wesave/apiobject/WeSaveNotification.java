package com.yong.wesave.apiobject;

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

public class WeSaveNotification {

    public int id;
    public String type;
    public int user_id;
    public int item_id;
    public String timestamp;
    public int recipient_id;
    public String image;
    public String username;

    public String getType() {
        return type;
    }

    public String getCreatedat() {
        return timestamp;
    }

    public int getID() {
        return id;
    }

    public int getUserID() {
        return user_id;
    }

    public int getItemID() {
        return item_id;
    }

    public int getRecipientID() {
        return recipient_id;
    }

    public String getItemImage() {
        return image;
    }

    public String getUsername() {
        return username;
    }
}
