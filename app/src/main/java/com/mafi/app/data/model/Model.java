package com.mafi.app.data.model;

/**
 * API'den gelen model bilgilerini temsil eden sınıf
 */
public class Model {
    
    private String model;
    private String modified_at;
    private String name;
    private long size;
    
    public Model() {
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getModified_at() {
        return modified_at;
    }
    
    public void setModified_at(String modified_at) {
        this.modified_at = modified_at;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public long getSize() {
        return size;
    }
    
    public void setSize(long size) {
        this.size = size;
    }
    
    @Override
    public String toString() {
        return name;
    }
} 