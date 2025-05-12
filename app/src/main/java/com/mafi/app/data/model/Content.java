package com.mafi.app.data.model;

public class Content {
    private int id;
    private String title;
    private String description;
    private int contentTypeId;
    private String contentUrl;
    private int userId;
    private long createdAt;


    private String originalInput;     // Kullanıcının girdiği orijinal metin/içerik
    private String aiResponse;        // AI'dan dönen yanıt
    private String aiProcessingType;  // Özet, genişletme, anahtar kelimeler vb.

    // Yapıcı metodlar
    public Content() {}

    public Content(int id, String title, String description, int contentTypeId,
                   String contentUrl, int userId, long createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.contentTypeId = contentTypeId;
        this.contentUrl = contentUrl;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    // Getter ve Setter metodları
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getContentTypeId() { return contentTypeId; }
    public void setContentTypeId(int contentTypeId) { this.contentTypeId = contentTypeId; }

    public String getContentUrl() { return contentUrl; }
    public void setContentUrl(String contentUrl) { this.contentUrl = contentUrl; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}