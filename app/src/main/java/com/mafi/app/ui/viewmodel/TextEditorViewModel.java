package com.mafi.app.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mafi.app.data.model.Content;
import com.mafi.app.data.repository.ContentRepository;
import com.mafi.app.data.repository.ModelRepository;
import com.mafi.app.data.repository.TextAnalysisRepository;

public class TextEditorViewModel extends AndroidViewModel {
    private static final String TAG = "TextEditorViewModel";

    private ContentRepository contentRepository;
    private TextAnalysisRepository textAnalysisRepository;
    
    private MutableLiveData<Content> selectedContent;
    private MutableLiveData<String> editedText;
    private MutableLiveData<Boolean> saveSuccess;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> questionInput;

    public TextEditorViewModel(@NonNull Application application) {
        super(application);
        contentRepository = new ContentRepository(application);
        textAnalysisRepository = new TextAnalysisRepository();
        
        selectedContent = new MutableLiveData<>();
        editedText = new MutableLiveData<>("");
        saveSuccess = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        questionInput = new MutableLiveData<>("");
    }

    public LiveData<Content> getSelectedContent() {
        return selectedContent;
    }

    public LiveData<String> getEditedText() {
        return editedText;
    }

    public LiveData<Boolean> getSaveSuccess() {
        return saveSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getQuestionInput() {
        return questionInput;
    }
    
    public void setQuestionInput(String question) {
        questionInput.setValue(question);
    }

    public void loadContent(int contentId) {
        try {
            Log.d(TAG, "İçerik yükleniyor. ID: " + contentId);

            Content content = contentRepository.getContentById(contentId);
            if (content != null) {
                selectedContent.setValue(content);

                // Eğer içeriğin halihazırda bir açıklaması varsa, editöre koy
                if (content.getDescription() != null && !content.getDescription().isEmpty()) {
                    editedText.setValue(content.getDescription());
                }

                Log.d(TAG, "İçerik başarıyla yüklendi: " + content.getTitle());
            } else {
                Log.e(TAG, "İçerik bulunamadı. ID: " + contentId);
                errorMessage.setValue("İçerik bulunamadı.");
            }
        } catch (Exception e) {
            Log.e(TAG, "İçerik yükleme hatası: " + e.getMessage(), e);
            errorMessage.setValue("İçerik yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    public void setEditedText(String text) {
        editedText.setValue(text);
    }

    public void saveContent() {
        try {
            Content content = selectedContent.getValue();
            String text = editedText.getValue();

            if (content == null) {
                Log.e(TAG, "İçerik null, kaydedilemez");
                errorMessage.setValue("İçerik bulunamadı");
                return;
            }

            if (text == null || text.isEmpty()) {
                Log.e(TAG, "Metin boş, kaydedilemez");
                errorMessage.setValue("Lütfen bir metin girin");
                return;
            }

            Log.d(TAG, "İçerik kaydediliyor. ID: " + content.getId());

            // İçerik açıklamasını güncelle
            content.setDescription(text);

            // Veritabanını güncelle
            int result = contentRepository.updateContent(content);

            if (result > 0) {
                Log.d(TAG, "İçerik başarıyla kaydedildi");
                saveSuccess.setValue(true);
            } else {
                Log.e(TAG, "İçerik kaydedilemedi");
                errorMessage.setValue("İçerik kaydedilemedi");
            }
        } catch (Exception e) {
            Log.e(TAG, "İçerik kaydetme hatası: " + e.getMessage(), e);
            errorMessage.setValue("İçerik kaydedilirken hata oluştu: " + e.getMessage());
        }
    }
    
    private String getCurrentModel() {
        return ModelRepository.getCurrentModel();
    }

    // API ile özet oluşturma işlevi
    public void summarizeText() {
        String currentText = editedText.getValue();
        if (currentText == null || currentText.isEmpty()) {
            errorMessage.setValue("Özetlenecek metin yok");
            return;
        }

        // API çağrısı için repository'yi kullan
        textAnalysisRepository.summarizeText(
            currentText, 
            getCurrentModel(), 
            editedText, 
            isLoading, 
            errorMessage
        );
    }

    // API ile derin analiz işlevi
    public void expandText() {
        String currentText = editedText.getValue();
        if (currentText == null || currentText.isEmpty()) {
            errorMessage.setValue("Analiz edilecek metin yok");
            return;
        }

        // API çağrısı için repository'yi kullan
        textAnalysisRepository.analyzeText(
            currentText, 
            getCurrentModel(), 
            editedText, 
            isLoading, 
            errorMessage
        );
    }
    
    // API ile metin sınıflandırma işlevi
    public void classifyText() {
        String currentText = editedText.getValue();
        if (currentText == null || currentText.isEmpty()) {
            errorMessage.setValue("Sınıflandırılacak metin yok");
            return;
        }

        // API çağrısı için repository'yi kullan
        textAnalysisRepository.classifyText(
            currentText, 
            getCurrentModel(), 
            editedText, 
            isLoading, 
            errorMessage
        );
    }
    
    // API ile soru cevaplama işlevi
    public void answerQuestion(String question) {
        String currentText = editedText.getValue();
        if (currentText == null || currentText.isEmpty()) {
            errorMessage.setValue("Soru sorulacak metin yok");
            return;
        }
        
        if (question == null || question.isEmpty()) {
            errorMessage.setValue("Lütfen bir soru girin");
            return;
        }

        // API çağrısı için repository'yi kullan
        textAnalysisRepository.answerQuestion(
            currentText,
            question,
            getCurrentModel(), 
            editedText, 
            isLoading, 
            errorMessage
        );
    }

    public void resetSaveSuccess() {
        saveSuccess.setValue(false);
    }
}