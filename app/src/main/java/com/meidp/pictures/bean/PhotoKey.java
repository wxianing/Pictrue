package com.meidp.pictures.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Package： com.meidp.pictures.bean
 * Author： wxianing
 * 作  用：
 * 时  间： 2016/9/20
 */
public class PhotoKey implements Serializable {

    private List<String> keys;

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }
}
