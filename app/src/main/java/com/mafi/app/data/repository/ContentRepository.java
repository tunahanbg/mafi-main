package com.mafi.app.data.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mafi.app.data.local.DatabaseHelper;
import com.mafi.app.data.model.Content;
import com.mafi.app.data.model.ContentType;

import java.util.ArrayList;
import java.util.List;

public class ContentRepository {
    private static final String TAG = "ContentRepository";
    private DatabaseHelper dbHelper;

    public ContentRepository(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    // Veritabanı durumunu kontrol etmek için debug metodu
    public void checkDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db != null && db.isOpen()) {
            Log.d(TAG, "Veritabanı başarıyla açıldı");

            // Tabloları kontrol et
            Cursor cursor = null;
            try {
                cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
                if (cursor != null && cursor.moveToFirst()) {
                    Log.d(TAG, "Veritabanı tabloları:");
                    do {
                        String tableName = cursor.getString(0);
                        Log.d(TAG, "- " + tableName);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                Log.e(TAG, "Tablo listesi alınırken hata: " + e.getMessage(), e);
            } finally {
                if (cursor != null) cursor.close();
            }

            // İçerikler tablosundaki kayıtları kontrol et
            try {
                cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_CONTENTS, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int count = cursor.getInt(0);
                    Log.d(TAG, "İçerik sayısı: " + count);
                }
            } catch (Exception e) {
                Log.e(TAG, "İçerik sayısı alınırken hata: " + e.getMessage(), e);
            } finally {
                if (cursor != null) cursor.close();
            }

            // Kullanıcılar tablosundaki kayıtları kontrol et
            try {
                cursor = db.rawQuery("SELECT COUNT(*) FROM " + DatabaseHelper.TABLE_USERS, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int count = cursor.getInt(0);
                    Log.d(TAG, "Kullanıcı sayısı: " + count);
                }
            } catch (Exception e) {
                Log.e(TAG, "Kullanıcı sayısı alınırken hata: " + e.getMessage(), e);
            } finally {
                if (cursor != null) cursor.close();
            }
        } else {
            Log.e(TAG, "Veritabanı açılamadı");
        }
    }

    // İçerik ekleme
    public long insertContent(Content content) {
        Log.d(TAG, "İçerik ekleme başlatıldı: " + content.getTitle());

        SQLiteDatabase db = null;
        long id = -1;

        try {
            db = dbHelper.getWritableDatabase();

            // UserID kontrolü
            if (content.getUserId() <= 0) {
                Log.e(TAG, "Geçersiz kullanıcı ID: " + content.getUserId());
                return -1;
            }

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_TITLE, content.getTitle());
            values.put(DatabaseHelper.COLUMN_DESCRIPTION, content.getDescription());
            values.put(DatabaseHelper.COLUMN_CONTENT_TYPE_ID, content.getContentTypeId());
            values.put(DatabaseHelper.COLUMN_CONTENT_URL, content.getContentUrl());
            values.put(DatabaseHelper.COLUMN_USER_ID, content.getUserId());
            values.put(DatabaseHelper.COLUMN_CREATED_AT, System.currentTimeMillis());

            id = db.insert(DatabaseHelper.TABLE_CONTENTS, null, values);

            if (id == -1) {
                Log.e(TAG, "İçerik eklenirken hata oluştu");
            } else {
                Log.d(TAG, "İçerik başarıyla eklendi. ID: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "insertContent hatası: " + e.getMessage(), e);
        }

        return id;
    }

    // İçerik güncelleme
    public int updateContent(Content content) {
        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_TITLE, content.getTitle());
            values.put(DatabaseHelper.COLUMN_DESCRIPTION, content.getDescription());
            values.put(DatabaseHelper.COLUMN_CONTENT_TYPE_ID, content.getContentTypeId());
            values.put(DatabaseHelper.COLUMN_CONTENT_URL, content.getContentUrl());

            rowsAffected = db.update(
                    DatabaseHelper.TABLE_CONTENTS,
                    values,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(content.getId())}
            );

            if (rowsAffected > 0) {
                Log.d(TAG, "İçerik başarıyla güncellendi. ID: " + content.getId());
            } else {
                Log.e(TAG, "İçerik güncellenirken hata oluştu. ID: " + content.getId());
            }
        } catch (Exception e) {
            Log.e(TAG, "updateContent hatası: " + e.getMessage(), e);
        }

        return rowsAffected;
    }

    // İçerik silme
    public int deleteContent(int contentId) {
        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            db = dbHelper.getWritableDatabase();

            rowsAffected = db.delete(
                    DatabaseHelper.TABLE_CONTENTS,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(contentId)}
            );

            if (rowsAffected > 0) {
                Log.d(TAG, "İçerik başarıyla silindi. ID: " + contentId);
            } else {
                Log.e(TAG, "İçerik silinirken hata oluştu. ID: " + contentId);
            }
        } catch (Exception e) {
            Log.e(TAG, "deleteContent hatası: " + e.getMessage(), e);
        }

        return rowsAffected;
    }

    // ID'ye göre içerik getirme
    public Content getContentById(int contentId) {
        Content content = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    DatabaseHelper.TABLE_CONTENTS,
                    null,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(contentId)},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                content = cursorToContent(cursor);
                Log.d(TAG, "İçerik bulundu. ID: " + contentId);
            } else {
                Log.d(TAG, "İçerik bulunamadı. ID: " + contentId);
            }
        } catch (Exception e) {
            Log.e(TAG, "getContentById hatası: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return content;
    }

    // Tüm içerikleri getirme
    public List<Content> getAllContents() {
        List<Content> contentList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    DatabaseHelper.TABLE_CONTENTS,
                    null, null, null, null, null,
                    DatabaseHelper.COLUMN_CREATED_AT + " DESC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Content content = cursorToContent(cursor);
                    if (content != null) {
                        contentList.add(content);
                    }
                } while (cursor.moveToNext());

                Log.d(TAG, contentList.size() + " içerik bulundu");
            } else {
                Log.d(TAG, "Hiç içerik bulunamadı");
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllContents hatası: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return contentList;
    }

    // Belirli bir kullanıcının içeriklerini getirme
    public List<Content> getContentsByUserId(int userId) {
        List<Content> contentList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            Log.d(TAG, "Kullanıcı ID: " + userId + " için içerikler getiriliyor");

            if (userId <= 0) {
                Log.e(TAG, "Geçersiz kullanıcı ID: " + userId);
                return contentList;
            }

            db = dbHelper.getReadableDatabase();

            String selection = DatabaseHelper.COLUMN_USER_ID + " = ?";
            String[] selectionArgs = new String[]{String.valueOf(userId)};

            cursor = db.query(
                    DatabaseHelper.TABLE_CONTENTS,
                    null,
                    selection,
                    selectionArgs,
                    null, null,
                    DatabaseHelper.COLUMN_CREATED_AT + " DESC"
            );

            if (cursor != null) {
                Log.d(TAG, "Sorgu başarılı. Bulunan içerik sayısı: " + cursor.getCount());

                if (cursor.moveToFirst()) {
                    do {
                        Content content = cursorToContent(cursor);
                        if (content != null) {
                            contentList.add(content);
                            Log.d(TAG, "İçerik eklendi: " + content.getTitle());
                        }
                    } while (cursor.moveToNext());
                } else {
                    Log.d(TAG, "Kullanıcı için hiç içerik bulunamadı. UserID: " + userId);
                }
            } else {
                Log.e(TAG, "Sorgu başarısız oldu. Cursor null.");
            }
        } catch (Exception e) {
            Log.e(TAG, "getContentsByUserId hatası: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        Log.d(TAG, "Toplam döndürülen içerik sayısı: " + contentList.size());
        return contentList;
    }

    // İçerik tipine göre içerikleri getirme
    public List<Content> getContentsByType(int contentTypeId) {
        List<Content> contentList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();

            cursor = db.query(
                    DatabaseHelper.TABLE_CONTENTS,
                    null,
                    DatabaseHelper.COLUMN_CONTENT_TYPE_ID + " = ?",
                    new String[]{String.valueOf(contentTypeId)},
                    null, null,
                    DatabaseHelper.COLUMN_CREATED_AT + " DESC"
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Content content = cursorToContent(cursor);
                    if (content != null) {
                        contentList.add(content);
                    }
                } while (cursor.moveToNext());

                Log.d(TAG, contentTypeId + " tipi için " + contentList.size() + " içerik bulundu");
            } else {
                Log.d(TAG, contentTypeId + " tipi için hiç içerik bulunamadı");
            }
        } catch (Exception e) {
            Log.e(TAG, "getContentsByType hatası: " + e.getMessage(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return contentList;
    }

    // Cursor'dan Content nesnesine dönüştürme - NULL CHECK'LERİ ÖNEMLİ
    private Content cursorToContent(Cursor cursor) {
        if (cursor == null) {
            Log.e(TAG, "cursorToContent: Cursor null");
            return null;
        }

        try {
            Content content = new Content();

            // Sütun index'lerini kontrol et
            int idIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TITLE);
            int descriptionIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DESCRIPTION);
            int contentTypeIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTENT_TYPE_ID);
            int contentUrlIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CONTENT_URL);
            int userIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER_ID);
            int createdAtIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CREATED_AT);

            // Sütun indexleri geçerli mi kontrol et
            if (idIndex == -1 || titleIndex == -1 || contentTypeIdIndex == -1 || userIdIndex == -1) {
                Log.e(TAG, "cursorToContent: Bazı sütunlar bulunamadı! " +
                        "idIndex=" + idIndex + ", titleIndex=" + titleIndex +
                        ", contentTypeIdIndex=" + contentTypeIdIndex + ", userIdIndex=" + userIdIndex);
                return null;
            }

            // Değerleri al
            content.setId(cursor.getInt(idIndex));
            content.setTitle(cursor.getString(titleIndex));

            if (descriptionIndex != -1) {
                content.setDescription(cursor.getString(descriptionIndex));
            }

            content.setContentTypeId(cursor.getInt(contentTypeIdIndex));

            if (contentUrlIndex != -1) {
                content.setContentUrl(cursor.getString(contentUrlIndex));
            }

            content.setUserId(cursor.getInt(userIdIndex));

            if (createdAtIndex != -1) {
                content.setCreatedAt(cursor.getLong(createdAtIndex));
            } else {
                content.setCreatedAt(System.currentTimeMillis());
            }

            return content;
        } catch (Exception e) {
            Log.e(TAG, "cursorToContent hatası: " + e.getMessage(), e);
            return null;
        }
    }
}