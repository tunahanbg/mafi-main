package com.mafi.app.data.model;

/**
 * Metin sınıflandırma API isteği için model
 */
public class ClassifyRequest {
    
    private String text;
    private String model;
    
    public ClassifyRequest() {
    }
    
    public ClassifyRequest(String text, String model) {
        this.text = text;
        this.model = model;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
} 