package com.mafi.app.ui.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mafi.app.data.local.SharedPreferencesManager;
import com.mafi.app.data.model.Content;
import com.mafi.app.data.repository.ContentRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {
    private static final String TAG = "HomeViewModel";

    private ContentRepository contentRepository;
    private SharedPreferencesManager preferencesManager;
    private MutableLiveData<List<Content>> contentList;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        contentRepository = new ContentRepository(application);
        preferencesManager = SharedPreferencesManager.getInstance(application);
        contentList = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
    }

    public LiveData<List<Content>> getContentList() {
        return contentList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Kullanıcının kendine ait içeriklerini getir
    public void loadUserContents() {
        isLoading.setValue(true);
        errorMessage.setValue(null); // Hata mesajını temizle

        try {
            // Mevcut kullanıcının ID'sini al
            int userId = preferencesManager.getUserId();

            Log.d(TAG, "Kullanıcı içerikleri yükleniyor. UserId: " + userId);

            if (userId <= 0) {
                isLoading.setValue(false);
                errorMessage.setValue("Geçerli bir kullanıcı bulunamadı");
                contentList.setValue(new ArrayList<>());
                return;
            }

            // İçerikleri getir
            List<Content> contents = contentRepository.getContentsByUserId(userId);

            // Null check
            if (contents == null) {
                contents = new ArrayList<>();
            }

            contentList.setValue(contents);

            Log.d(TAG, "İçerikler yüklendi: " + contents.size() + " adet");

            isLoading.setValue(false);
        } catch (Exception e) {
            Log.e(TAG, "İçerik yükleme hatası: " + e.getMessage(), e);
            isLoading.setValue(false);
            errorMessage.setValue("İçerikler yüklenirken hata oluştu: " + e.getMessage());
            contentList.setValue(new ArrayList<>()); // Boş liste
        }
    }

    // Tüm içerikleri getir (eskiden kullanılan metod)
    public void loadAllContents() {
        loadUserContents(); // Şimdi bu, kullanıcının kendi içeriklerini getirir
    }

    // İçerik tipine göre filtrele
    public void loadContentsByType(int contentTypeId) {
        isLoading.setValue(true);
        errorMessage.setValue(null);

        try {
            // Mevcut kullanıcının ID'sini al
            int userId = preferencesManager.getUserId();

            if (userId <= 0) {
                isLoading.setValue(false);
                errorMessage.setValue("Geçerli bir kullanıcı bulunamadı");
                contentList.setValue(new ArrayList<>());
                return;
            }

            // Önce kullanıcının tüm içeriklerini getir
            List<Content> userContents = contentRepository.getContentsByUserId(userId);

            // Sonra istenen tiptekileri filtrele
            List<Content> filteredContents = new ArrayList<>();
            if (userContents != null) {
                for (Content content : userContents) {
                    if (content.getContentTypeId() == contentTypeId) {
                        filteredContents.add(content);
                    }
                }
            }

            contentList.setValue(filteredContents);
            isLoading.setValue(false);
        } catch (Exception e) {
            Log.e(TAG, "İçerik filtreleme hatası: " + e.getMessage(), e);
            isLoading.setValue(false);
            errorMessage.setValue("İçerikler filtrelenirken hata oluştu: " + e.getMessage());
            contentList.setValue(new ArrayList<>());
        }
    }

    public void deleteContent(int contentId) {
        try {
            isLoading.setValue(true);
            errorMessage.setValue(null);

            int result = contentRepository.deleteContent(contentId);

            if (result > 0) {
                // Silme başarılı, içerik listesini yenile
                loadUserContents();
                Toast.makeText(getApplication(), "İçerik başarıyla silindi", Toast.LENGTH_SHORT).show();
            } else {
                errorMessage.setValue("İçerik silinirken bir hata oluştu");
            }

            isLoading.setValue(false);
        } catch (Exception e) {
            Log.e(TAG, "İçerik silme hatası: " + e.getMessage(), e);
            isLoading.setValue(false);
            errorMessage.setValue("İçerik silinirken bir hata oluştu: " + e.getMessage());
        }
    }
}