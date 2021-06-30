package com.samuelvialle.recyclerviewimagesandvideo.models;


import java.util.List;

public class MyModel {

    private List<String> mediaList;

    public MyModel(List<String> mediaList) {
        this.mediaList = mediaList;
    }

    public List<String> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<String> mediaList) {
        this.mediaList = mediaList;
    }
}
