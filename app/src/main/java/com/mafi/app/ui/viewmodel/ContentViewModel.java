package com.mafi.app.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mafi.app.data.local.SharedPreferencesManager;
import com.mafi.app.data.model.Content;
import com.mafi.app.data.repository.ContentRepository;

public class ContentViewModel extends AndroidViewModel {
    private static final String TAG = "ContentViewModel";

    private ContentRepository contentRepository;
    private SharedPreferencesManager preferencesManager;
    private MutableLiveData<Content> selectedContent;
    private MutableLiveData<Boolean> operationSuccess;
    private MutableLiveData<String> errorMessage;

    public ContentViewModel(@NonNull Application application) {
        super(application);
        contentRepository = new ContentRepository(application);
        preferencesManager = SharedPreferencesManager.getInstance(application);
        selectedContent = new MutableLiveData<>();
        operationSuccess = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
    }

    public LiveData<Content> getSelectedContent() {
        return selectedContent;
    }

    public LiveData<Boolean> getOperationSuccess() {
        return operationSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadContent(int contentId) {
        try {
            Log.d(TAG, "İçerik yükleniyor, ID: " + contentId);
            Content content = contentRepository.getContentById(contentId);

            if (content != null) {
                selectedContent.setValue(content);
                Log.d(TAG, "İçerik başarıyla yüklendi: " + content.getTitle());
            } else {
                errorMessage.setValue("İçerik bulunamadı");
                Log.e(TAG, "İçerik bulunamadı, ID: " + contentId);
            }
        } catch (Exception e) {
            Log.e(TAG, "İçerik yükleme hatası: " + e.getMessage(), e);
            errorMessage.setValue("İçerik yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    public void createContent(String title, String description, int contentTypeId, String contentUrl) {
        try {
            // Kullanıcı ID'sini al
            int userId = preferencesManager.getUserId();

            Log.d(TAG, "İçerik oluşturuluyor. Başlık: " + title + ", Kullanıcı ID: " + userId);

            if (userId <= 0) {
                errorMessage.setValue("Kullanıcı girişi yapılmamış!");
                Log.e(TAG, "İçerik oluşturma hatası: Kullanıcı ID geçersiz: " + userId);
                return;
            }

            // Yeni içerik oluştur
            Content newContent = new Content();
            newContent.setTitle(title);
            newContent.setDescription(description);
            newContent.setContentTypeId(contentTypeId);
            newContent.setContentUrl(contentUrl);
            newContent.setUserId(userId);
            newContent.setCreatedAt(System.currentTimeMillis());

            // Veritabanına kaydet
            long contentId = contentRepository.insertContent(newContent);

            if (contentId > 0) {
                Log.d(TAG, "İçerik başarıyla oluşturuldu. ID: " + contentId);
                operationSuccess.setValue(true);
            } else {
                Log.e(TAG, "İçerik oluşturulamadı. Kayıt ID: " + contentId);
                errorMessage.setValue("İçerik oluşturulamadı.");
            }
        } catch (Exception e) {
            Log.e(TAG, "İçerik oluşturma hatası: " + e.getMessage(), e);
            errorMessage.setValue("İçerik oluşturulurken hata oluştu: " + e.getMessage());
        }
    }

    public void updateContent(Content content) {
        try {
            Log.d(TAG, "İçerik güncelleniyor, ID: " + content.getId());
            int result = contentRepository.updateContent(content);

            if (result > 0) {
                Log.d(TAG, "İçerik başarıyla güncellendi");
                operationSuccess.setValue(true);
            } else {
                Log.e(TAG, "İçerik güncellenemedi");
                errorMessage.setValue("İçerik güncellenemedi.");
            }
        } catch (Exception e) {
            Log.e(TAG, "İçerik güncelleme hatası: " + e.getMessage(), e);
            errorMessage.setValue("İçerik güncellenirken hata oluştu: " + e.getMessage());
        }
    }

    public void deleteContent(int contentId) {
        try {
            Log.d(TAG, "İçerik siliniyor, ID: " + contentId);
            int result = contentRepository.deleteContent(contentId);

            if (result > 0) {
                Log.d(TAG, "İçerik başarıyla silindi");
                operationSuccess.setValue(true);
            } else {
                Log.e(TAG, "İçerik silinemedi");
                errorMessage.setValue("İçerik silinemedi.");
            }
        } catch (Exception e) {
            Log.e(TAG, "İçerik silme hatası: " + e.getMessage(), e);
            errorMessage.setValue("İçerik silinirken hata oluştu: " + e.getMessage());
        }
    }
}