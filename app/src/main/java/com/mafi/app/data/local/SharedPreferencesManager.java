package com.mafi.app.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String PREF_NAME = "MafiAppPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PROFILE_PICTURE = "profilePicture";
    private static final String KEY_THEME = "theme";
    private static final String KEY_NOTIFICATION_ENABLED = "notificationEnabled";

    private SharedPreferences sharedPreferences;
    private static SharedPreferencesManager instance;

    private SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context);
        }
        return instance;
    }

    // Giriş durumu işlemleri
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setLoggedIn(boolean loggedIn) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, loggedIn).apply();
    }

    // Kullanıcı bilgileri için işlemler
    public void saveUserSession(int userId, String username, String email, String profilePicture) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PROFILE_PICTURE, profilePicture);
        editor.apply();
    }

    public void clearUserSession() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_PROFILE_PICTURE);
        editor.apply();
    }

    // Kullanıcı bilgisi getirme metodları
    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }

    public String getProfilePicture() {
        return sharedPreferences.getString(KEY_PROFILE_PICTURE, "");
    }

    // Tema ayarları
    public String getTheme() {
        return sharedPreferences.getString(KEY_THEME, "light");
    }

    public void setTheme(String theme) {
        sharedPreferences.edit().putString(KEY_THEME, theme).apply();
    }

    // Bildirim ayarları
    public boolean isNotificationEnabled() {
        return sharedPreferences.getBoolean(KEY_NOTIFICATION_ENABLED, true);
    }

    public void setNotificationEnabled(boolean enabled) {
        sharedPreferences.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply();
    }
}