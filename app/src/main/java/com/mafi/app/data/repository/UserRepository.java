package com.mafi.app.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mafi.app.data.local.DatabaseHelper;
import com.mafi.app.data.model.User;

public class UserRepository {
    private static final String TAG = "UserRepository";
    private DatabaseHelper dbHelper;

    public UserRepository(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // Kullanıcı ekleme (Kayıt olma işlemi için)
    public long insertUser(User user) {
        long id = -1;
        SQLiteDatabase db = null;

        try {
            Log.d(TAG, "Yeni kullanıcı oluşturuluyor: " + user.getEmail());
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
            values.put(DatabaseHelper.COLUMN_EMAIL, user.getEmail());
            values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());
            values.put(DatabaseHelper.COLUMN_PROFILE_PICTURE_URL, user.getProfilePictureUrl());
            values.put(DatabaseHelper.COLUMN_CREATED_AT, System.currentTimeMillis());
            values.put(DatabaseHelper.COLUMN_IS_ACTIVE, user.isActive() ? 1 : 0);

            id = db.insert(DatabaseHelper.TABLE_USERS, null, values);

            if (id == -1) {
                Log.e(TAG, "Kullanıcı eklenirken hata oluştu");
            } else {
                Log.d(TAG, "Kullanıcı başarıyla eklendi. ID: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "insertUser hatası: " + e.getMessage(), e);
        }

        return id;
    }

    // E-posta ve şifre ile giriş kontrolü
    public User login(String email, String password) {
        User user = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            Log.d(TAG, "Giriş denemesi: " + email);
            db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COLUMN_EMAIL + " = ? AND " +
                            DatabaseHelper.COLUMN_PASSWORD + " = ? AND " +
                            DatabaseHelper.COLUMN_IS_ACTIVE + " = ?",
                    new String[]{email, password, "1"},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                user = cursorToUser(cursor);
                Log.d(TAG, "Giriş başarılı. Kullanıcı ID: " + user.getId());
            } else {
                Log.d(TAG, "Giriş başarısız. E-posta: " + email);
            }
        } catch (Exception e) {
            Log.e(TAG, "login hatası: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return user;
    }

    // ID'ye göre kullanıcı getirme
    public User getUserById(int userId) {
        User user = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    null,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(userId)},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                user = cursorToUser(cursor);
                Log.d(TAG, "Kullanıcı bulundu. ID: " + userId);
            } else {
                Log.d(TAG, "Kullanıcı bulunamadı. ID: " + userId);
            }
        } catch (Exception e) {
            Log.e(TAG, "getUserById hatası: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return user;
    }

    // E-posta'ya göre kullanıcı kontrolü (e-posta benzersiz olmalı)
    public boolean isEmailExists(String email) {
        boolean exists = false;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    DatabaseHelper.TABLE_USERS,
                    new String[]{DatabaseHelper.COLUMN_ID},
                    DatabaseHelper.COLUMN_EMAIL + " = ?",
                    new String[]{email},
                    null, null, null
            );

            exists = (cursor != null && cursor.getCount() > 0);
            Log.d(TAG, "E-posta kontrol: " + email + ", Var mı: " + exists);

        } catch (Exception e) {
            Log.e(TAG, "isEmailExists hatası: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return exists;
    }

    // Cursor'dan User nesnesine dönüştürme
    private User cursorToUser(Cursor cursor) {
        User user = new User();

        try {
            int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
            int usernameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME);
            int emailIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_EMAIL);
            int passwordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD);
            int profilePictureUrlIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PROFILE_PICTURE_URL);
            int createdAtIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT);
            int isActiveIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_ACTIVE);

            if (idIndex != -1) user.setId(cursor.getInt(idIndex));
            if (usernameIndex != -1) user.setUsername(cursor.getString(usernameIndex));
            if (emailIndex != -1) user.setEmail(cursor.getString(emailIndex));
            if (passwordIndex != -1) user.setPassword(cursor.getString(passwordIndex));
            if (profilePictureUrlIndex != -1) user.setProfilePictureUrl(cursor.getString(profilePictureUrlIndex));
            if (createdAtIndex != -1) user.setCreatedAt(cursor.getLong(createdAtIndex));
            if (isActiveIndex != -1) user.setActive(cursor.getInt(isActiveIndex) == 1);
        } catch (Exception e) {
            Log.e(TAG, "cursorToUser hatası: " + e.getMessage(), e);
        }

        return user;
    }
}