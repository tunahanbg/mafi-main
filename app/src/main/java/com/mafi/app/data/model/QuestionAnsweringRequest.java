package com.mafi.app.data.model;

/**
 * Soru-cevap API isteği için model
 */
public class QuestionAnsweringRequest {
    
    private String text;
    private String question;
    private String model;
    
    public QuestionAnsweringRequest() {
    }
    
    public QuestionAnsweringRequest(String text, String question, String model) {
        this.text = text;
        this.question = question;
        this.model = model;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
} 