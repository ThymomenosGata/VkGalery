package com.ilatis.vkgalery.StructClass;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Friend implements Serializable {
    private String id;
    private String full_name;
    private Bitmap photo_130;

    public Friend(String id, String full_name, Bitmap photo_130) {
        this.id = id;
        this.full_name = full_name;
        this.photo_130 = photo_130;
    }

    public String getId() {
        return id;
    }

    public String getFull_name() {
        return full_name;
    }

    public Bitmap getPhoto_130() {
        return photo_130;
    }
}
