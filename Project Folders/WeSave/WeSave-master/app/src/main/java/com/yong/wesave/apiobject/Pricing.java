package com.yong.wesave.apiobject;

/**
 * Created by Yong on 19/1/2017.
 */

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

public class Pricing {

    public int id;
    public String store_name;
    public String image;
    public String name;
    public String info;
    public int item_id;
    public String original_price;
    public String promo_price;
    public String promo_qty;
    public String promo_start;
    public String promo_end;
    public String username;
    public String createdat;
    public String has_promo;
    public Double lat;
    public Double lng;
    public Double distance;
    public String singleprice;
    public String lowestprice;

    public int getId() {
        return id;
    }

    public String getStoreName() {
        return store_name;
    }

    public String getImage() {
        return image;
    }

    public String getItemName() {
        return name;
    }

    public String getItemInfo() {
        return info;
    }

    public int getItemId() {
        return item_id;
    }

    public String getOriginalPrice() {
        return original_price;
    }

    public String getPromoPrice() {
        return promo_price;
    }

    public String getPromoQty() {
        return promo_qty;
    }

    public String getPromoStart() {
        return promo_start;
    }

    public String getPromoEnd() {
        return promo_end;
    }

    public String getUsername() {
        return username;
    }

    public String getCreatedat() {
        return createdat;
    }

    public String getHasPromo() {
        return has_promo;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(double value) {
        distance = value;
    }

    public String getSinglePiecePrice() {
        return singleprice;
    }

    public String getLowestPrice() {
        return lowestprice;
    }
}
