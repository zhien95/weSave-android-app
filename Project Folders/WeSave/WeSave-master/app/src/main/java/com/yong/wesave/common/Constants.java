package com.yong.wesave.common;

/**
 * Created by Yong on 4/1/2017.
 */

/**
 * Author: Koo Yan Chong
 * Last updated date: 26/3/2018
 */

public class Constants {
    public static final String NTU_WIFI = "10.27.98.105";
    public static final String SHARED_WIFI = "192.168.1.6";

       public static final String API_BASE_URL = "http://" + SHARED_WIFI + ":3000";

//    NGROK
//    public static final String API_BASE_URL = "https://29957bb0.ngrok.io";
//    public static final String API_BASE_URL = "http://10.0.2.2:3000";
    //  public static final String API_BASE_URL = "http://" + NTU_WIFI + ":3000";
    //  public static final String API_BASE_URL = "http://18.136.194.26:3000";
    public static final String API_BASE_URL_API = API_BASE_URL + "/api/";
    public static final String API_BASE_URL_ITEM_IMAGES = API_BASE_URL + "/uploads/item_images/";
    public static final String API_BASE_URL_STORE_IMAGES = API_BASE_URL + "/uploads/store_images/";
    public static final String TOKEN = "token";
    public static final String EMAIL = "appwesave@gmail.com"; //pw: WeSave1234



    public static final String API_ADD_STORE = "addstore";
    public static final String API_GET_ALL_STORES = "getallstores";
    public static final String API_ADD_PRICE = "addprice";
    public static final String API_GET_ITEM = "getitem";
    public static final String API_SEARCH_NEARBY_Stores = "searchNearbyStores";
    
    //item comment
    public static final String API_GET_COMMENTS = "getComments";
    public static final String API_INSERT_COMMENT = "insertComment";

}