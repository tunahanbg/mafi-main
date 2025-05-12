package com.mafi.app.ui.viewmodel;

import android.app.Application;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mafi.app.data.model.User;
import com.mafi.app.data.repository.UserRepository;

public class RegisterViewModel extends AndroidViewModel {
    private static final String TAG = "RegisterViewModel";

    private UserRepository userRepository;
    private MutableLiveData<Boolean> registerSuccess;
    private MutableLiveData<String> errorMessage;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        registerSuccess = new MutableLiveData<>();
        errorMessage = new MutableLiveData<>();
    }

    public LiveData<Boolean> getRegisterSuccess() {
        return registerSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void register(String username, String email, String password, String confirmPassword) {
        Log.d(TAG, "Kayıt işlemi başlatılıyor: " + email);

        // Girdi doğrulama
        if (username == null || username.trim().isEmpty()) {
            errorMessage.setValue("Kullanıcı adı gerekli");
            return;
        }

        if (email == null || email.trim().isEmpty()) {
            errorMessage.setValue("E-posta adresi gerekli");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage.setValue("Geçerli bir e-posta adresi girin");
            return;
        }

        if (password == null || password.isEmpty()) {
            errorMessage.setValue("Şifre gerekli");
            return;
        }

        if (password.length() < 6) {
            errorMessage.setValue("Şifre en az 6 karakter olmalıdır");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorMessage.setValue("Şifreler eşleşmiyor");
            return;
        }

        // E-posta zaten kullanılıyor mu kontrol et
        if (userRepository.isEmailExists(email)) {
            errorMessage.setValue("Bu e-posta adresi zaten kullanılıyor");
            return;
        }

        // Kullanıcı oluştur ve kaydet
        try {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);
            user.setProfilePictureUrl(""); // Varsayılan profil resmi
            user.setCreatedAt(System.currentTimeMillis());
            user.setActive(true);

            long userId = userRepository.insertUser(user);

            if (userId > 0) {
                Log.d(TAG, "Kullanıcı başarıyla kaydedildi. ID: " + userId);
                registerSuccess.setValue(true);
            } else {
                errorMessage.setValue("Kullanıcı kaydedilirken bir hata oluştu");
                Log.e(TAG, "Kullanıcı ekleme başarısız");
            }
        } catch (Exception e) {
            errorMessage.setValue("Kayıt işlemi sırasında hata oluştu: " + e.getMessage());
            Log.e(TAG, "Register hatası: " + e.getMessage());
        }
    }
}