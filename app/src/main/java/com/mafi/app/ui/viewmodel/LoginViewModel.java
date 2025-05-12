package com.mafi.app.ui.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.mafi.app.data.model.User;
import com.mafi.app.data.repository.UserRepository;

import com.mafi.app.data.local.SharedPreferencesManager;

public class LoginViewModel extends AndroidViewModel {

    private static final String TAG = "LoginViewModel";

    private SharedPreferencesManager preferencesManager;
    private UserRepository userRepository;
    private MutableLiveData<Boolean> loginSuccess;
    private MutableLiveData<String> errorMessage;
    private android.util.Log Log;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        preferencesManager = SharedPreferencesManager.getInstance(application);
        userRepository = new UserRepository(application);
        loginSuccess = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
    }

    public LiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void login(String email, String password) {
        Log.d(TAG, "Login işlemi başlatılıyor: " + email);

        // Input validation
        if (email == null || email.isEmpty()) {
            errorMessage.setValue("E-posta adresi gerekli");
            return;
        }

        if (password == null || password.isEmpty()) {
            errorMessage.setValue("Şifre gerekli");
            return;
        }

        // Gerçek veritabanı doğrulaması
        try {
            User user = userRepository.login(email, password);

            if (user != null) {
                // Başarılı giriş - kullanıcı verilerini kaydet
                preferencesManager.saveUserSession(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getProfilePictureUrl()
                );
                loginSuccess.setValue(true);
                Log.d(TAG, "Giriş başarılı: " + user.getUsername());
            } else {
                errorMessage.setValue("Geçersiz e-posta veya şifre");
                Log.d(TAG, "Giriş başarısız");
            }
        } catch (Exception e) {
            errorMessage.setValue("Giriş işlemi sırasında hata oluştu: " + e.getMessage());
            Log.e(TAG, "Login hatası: " + e.getMessage());
        }
    }

    public boolean isLoggedIn() {
        return preferencesManager.isLoggedIn();
    }
}