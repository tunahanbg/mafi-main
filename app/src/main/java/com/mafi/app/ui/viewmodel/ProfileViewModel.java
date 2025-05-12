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
import com.mafi.app.data.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends AndroidViewModel {
    private static final String TAG = "ProfileViewModel";

    private ContentRepository contentRepository;
    private UserRepository userRepository;
    private SharedPreferencesManager preferencesManager;
    private MutableLiveData<List<Content>> userContentList;
    private MutableLiveData<String> username;
    private MutableLiveData<String> email;
    private MutableLiveData<String> profilePicture;
    private MutableLiveData<String> errorMessage;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        contentRepository = new ContentRepository(application);
        userRepository = new UserRepository(application);
        preferencesManager = SharedPreferencesManager.getInstance(application);
        userContentList = new MutableLiveData<>();
        username = new MutableLiveData<>();
        email = new MutableLiveData<>();
        profilePicture = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();

        // Kullanıcı bilgilerini yükle
        loadUserInfo();
    }

    public LiveData<List<Content>> getUserContentList() {
        return userContentList;
    }

    public LiveData<String> getUsername() {
        return username;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getProfilePicture() {
        return profilePicture;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadUserInfo() {
        try {
            username.setValue(preferencesManager.getUsername());
            email.setValue(preferencesManager.getEmail());
            profilePicture.setValue(preferencesManager.getProfilePicture());
            Log.d(TAG, "Kullanıcı bilgileri yüklendi: " + preferencesManager.getUsername());
        } catch (Exception e) {
            Log.e(TAG, "Kullanıcı bilgileri yüklenirken hata: " + e.getMessage(), e);
            errorMessage.setValue("Kullanıcı bilgileri yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    public void loadUserContents() {
        try {
            int userId = preferencesManager.getUserId();
            Log.d(TAG, "Kullanıcı içerikleri yükleniyor. UserId: " + userId);

            if (userId <= 0) {
                Log.e(TAG, "Geçersiz kullanıcı ID: " + userId);
                errorMessage.setValue("Kullanıcı girişi yapılmamış!");
                userContentList.setValue(new ArrayList<>());
                return;
            }

            List<Content> contents = contentRepository.getContentsByUserId(userId);
            if (contents == null) {
                contents = new ArrayList<>();
            }

            userContentList.setValue(contents);
            Log.d(TAG, "Kullanıcı içerikleri yüklendi: " + contents.size() + " adet");
        } catch (Exception e) {
            Log.e(TAG, "Kullanıcı içerikleri yüklenirken hata: " + e.getMessage(), e);
            errorMessage.setValue("Kullanıcı içerikleri yüklenirken hata oluştu: " + e.getMessage());
            userContentList.setValue(new ArrayList<>());
        }
    }

    public void logout() {
        Log.d(TAG, "Çıkış yapılıyor");
        preferencesManager.clearUserSession();
    }
}