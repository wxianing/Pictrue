package com.meidp.pictures.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Package： com.meidp.pictures.bean
 * Author： wxianing
 * 作  用：
 * 时  间： 2016/9/19
 */
public class Photos implements Serializable {

    private String paths;
    private String imageName;

    private List<PhotosData> photosDatas;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public List<PhotosData> getPhotosDatas() {
        return photosDatas;
    }

    public void setPhotosDatas(List<PhotosData> photosDatas) {
        this.photosDatas = photosDatas;
    }
}
