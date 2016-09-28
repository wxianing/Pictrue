package com.meidp.pictures.bean;

import java.io.Serializable;

/**
 * Package： com.meidp.pictures.bean
 * Author： wxianing
 * 作  用：
 * 时  间： 2016/9/19
 */
public class PhotosData implements Serializable {

    private int number;

    private float xcoordinate;

    private float ycoordinate;

    private String content;

    private NotePhoto photo;

    public NotePhoto getPhoto() {
        return photo;
    }

    public void setPhoto(NotePhoto photo) {
        this.photo = photo;
    }

    public PhotosData() {
    }

    public PhotosData(int number, float xcoordinate, float ycoordinate, String content) {
        this.number = number;
        this.xcoordinate = xcoordinate;
        this.ycoordinate = ycoordinate;
        this.content = content;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public float getXcoordinate() {
        return xcoordinate;
    }

    public void setXcoordinate(float xcoordinate) {
        this.xcoordinate = xcoordinate;
    }

    public float getYcoordinate() {
        return ycoordinate;
    }

    public void setYcoordinate(float ycoordinate) {
        this.ycoordinate = ycoordinate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
