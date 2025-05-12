package com.mafi.app.data.model;

import java.util.List;

/**
 * Kullanılabilir AI modellerini listelemek için yanıt modeli
 */
public class ModelResponse {
    
    private boolean success;
    private String message;
    private List<Model> models;
    private String current_model;
    
    public ModelResponse() {
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<Model> getModels() {
        return models;
    }
    
    public void setModels(List<Model> models) {
        this.models = models;
    }
    
    public String getCurrentModel() {
        return current_model;
    }
    
    public void setCurrentModel(String current_model) {
        this.current_model = current_model;
    }
} 