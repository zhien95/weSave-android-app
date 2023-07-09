package com.yong.wesave.apiobject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Yong on 19/1/2017.
 */

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

public class Item implements Parcelable {

    public int id;
    public String name;
    public String info;
    public String image;
    public String createdAt;

    //for shopping plan purpose
    public int qty;
    public String store_name;
    public String price;
    public String likecount;
    public String followcount;

    public Item() {

    }

    protected Item(Parcel in) {
        id = in.readInt();
        name = in.readString();
        info = in.readString();
        image = in.readString();
        createdAt = in.readString();
        qty = in.readInt();
        store_name = in.readString();
        price = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getQty() {
        return qty;
    }

    public String getInfo() {
        return info;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getImage() {
        return image;
    }

    public String getLikeCount() {
        return likecount;
    }

    public String getFollowCount() {
        return followcount;
    }

    public void setQty(int new_qty) {
        qty = new_qty;
    }

    public void setStoreName(String store_name) {
        this.store_name = store_name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(info);
        dest.writeString(image);
        dest.writeString(createdAt);
        dest.writeInt(qty);
        dest.writeString(store_name);
        dest.writeString(price);
    }
}
