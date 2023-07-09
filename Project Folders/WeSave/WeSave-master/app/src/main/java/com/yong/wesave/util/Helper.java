package com.yong.wesave.util;

import com.yong.wesave.apiobject.Item;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Yong0156 on 21/2/2017.
 */

public class Helper {

    public String stringify(List<Item> l) {
        String rs = "";
        for (Item marker : l) {
            rs = rs + ',' + marker.toString();
        }
        rs.substring(1);
        return rs;
    }

    public List<Item> makeList(String rs) {
        List<Item> rl = new LinkedList<Item>();
        String[] a = rs.split(",");
        for (String string : a) {
            Item rm = new Item();
            // I don't know what class of marker you use,
            //but here you should create the marker from the string
            rl.add(rm);
        }
        return rl;
    }

}