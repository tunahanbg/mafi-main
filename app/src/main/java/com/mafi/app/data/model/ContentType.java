package com.mafi.app.data.model;

public class ContentType {
    private int id;
    private String name;
    private String iconResource;

    // Sabit tip tanımlamaları
    public static final int TYPE_TEXT = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_AUDIO = 3;
    public static final int TYPE_VIDEO = 4;

    // Yapıcı metodlar
    public ContentType() {}

    public ContentType(int id, String name, String iconResource) {
        this.id = id;
        this.name = name;
        this.iconResource = iconResource;
    }

    // Getter ve Setter metodları
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIconResource() { return iconResource; }
    public void setIconResource(String iconResource) { this.iconResource = iconResource; }

    // İçerik tipine göre doğru icon'u döndüren yardımcı metod
    public static String getIconResourceForType(int typeId) {
        switch (typeId) {
            case TYPE_TEXT:
                return "ic_text";
            case TYPE_IMAGE:
                return "ic_image";
            case TYPE_AUDIO:
                return "ic_audio";
            case TYPE_VIDEO:
                return "ic_video";
            default:
                return "ic_content";
        }
    }
}

