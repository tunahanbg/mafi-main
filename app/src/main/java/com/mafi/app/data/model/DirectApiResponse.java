package com.mafi.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Görselde görülen alternatif API yanıt formatı için model
 */
public class DirectApiResponse {
    
    private String markdown;
    
    @SerializedName("raw_data")
    private String rawData;
    
    public DirectApiResponse() {
    }
    
    public DirectApiResponse(String markdown, String rawData) {
        this.markdown = markdown;
        this.rawData = rawData;
    }
    
    public String getMarkdown() {
        return markdown;
    }
    
    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }
    
    public String getRawData() {
        return rawData;
    }
    
    public void setRawData(String rawData) {
        this.rawData = rawData;
    }
} 