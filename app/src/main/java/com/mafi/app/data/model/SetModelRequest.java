package com.mafi.app.data.model;

/**
 * Model seçimi için istek modeli
 */
public class SetModelRequest {
    
    private String model;
    
    public SetModelRequest() {
    }
    
    public SetModelRequest(String model) {
        this.model = model;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
} 