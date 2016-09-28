package com.meidp.pictures.bean;

import java.io.Serializable;

/**
 * Package： com.meidp.pictures.bean
 * Author： wxianing
 * 作  用：
 * 时  间： 2016/9/28
 */
public class NotePhoto implements Serializable{
    private String paths;
    private String imageName;

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
