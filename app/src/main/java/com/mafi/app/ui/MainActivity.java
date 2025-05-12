package com.mafi.app.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationBarView;
import com.mafi.app.R;
import com.mafi.app.data.local.DatabaseHelper;
import com.mafi.app.data.local.SharedPreferencesManager;
import com.mafi.app.data.repository.ContentRepository;
import com.mafi.app.ui.fragment.HomeFragment;
import com.mafi.app.ui.fragment.LoginFragment;
import com.mafi.app.ui.fragment.ProfileFragment;
import com.mafi.app.ui.fragment.SettingsFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigation;
    private MaterialCardView bottomNavBackground;
    private SharedPreferencesManager preferencesManager;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "MainActivity oluşturuluyor");

        // Veritabanını başlat
        dbHelper = DatabaseHelper.getInstance(this);
        Log.d(TAG, "Veritabanı başlatıldı");

        // Veritabanı durumunu kontrol et
        ContentRepository contentRepository = new ContentRepository(this);
        contentRepository.checkDatabase();

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavBackground = findViewById(R.id.bottom_nav_background);
        
        // NavigationBarView kullanarak dinleyici atıyoruz
        bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (id == R.id.nav_content) {
                fragment = new HomeFragment(); // İçerik sayfası yerine şimdilik HomeFragment kullanıyoruz
            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
            } else if (id == R.id.nav_settings) {
                fragment = new SettingsFragment();
            }
            
            return loadFragment(fragment);
        });

        // İkon ve metin renklerini ayarla
        setupNavBarAppearance();

        preferencesManager = SharedPreferencesManager.getInstance(this);

        // Kullanıcının giriş durumunu kontrol et
        if (preferencesManager.isLoggedIn()) {
            // Geçerli bir kullanıcı ID'si var mı kontrol et
            int userId = preferencesManager.getUserId();
            Log.d(TAG, "Kullanıcı oturum durumu: Giriş yapılmış. UserId: " + userId);

            if (userId > 0) {
                // Kullanıcı giriş yapmışsa ana ekranı göster
                loadFragment(new HomeFragment());
                // Varsayılan olarak ana ekran seçili olsun
                bottomNavigation.setSelectedItemId(R.id.nav_home);
                showBottomNavigation(true);
            } else {
                // Kullanıcı ID geçersizse oturumu temizle ve giriş sayfasını göster
                Log.d(TAG, "Geçersiz kullanıcı ID, oturum temizleniyor");
                preferencesManager.clearUserSession();
                loadFragment(new LoginFragment());
                showBottomNavigation(false);
            }
        } else {
            // Kullanıcı giriş yapmamışsa giriş ekranını göster
            Log.d(TAG, "Kullanıcı oturum durumu: Giriş yapılmamış");
            loadFragment(new LoginFragment());
            showBottomNavigation(false);
        }
    }

    private void setupNavBarAppearance() {
        // Material Design 3 bileşenlerini desteklemek için gölge ve kenar ayarları
        bottomNavigation.setElevation(0);
        bottomNavigation.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
        
        // Seçili öğenin özel gösterimini kapatıyoruz
        bottomNavigation.setItemRippleColor(null);
        
        // İkon ve metin arasındaki mesafeyi ayarla
        try {
            // BottomNavigationView içindeki öğelere erişim (reflection kullanmadan)
            for (int i = 0; i < bottomNavigation.getMenu().size(); i++) {
                // Öğelerin görünüm parametrelerini güncelleme
                MenuItem item = bottomNavigation.getMenu().getItem(i);
                // Öğeyi yeniden oluşturma
                bottomNavigation.getMenu().removeItem(item.getItemId());
                bottomNavigation.getMenu().add(0, item.getItemId(), i, item.getTitle()).setIcon(item.getIcon());
            }
        } catch (Exception e) {
            // Hata durumunda devam et
            Log.e(TAG, "Gezinme çubuğu öğeleri ayarlanırken hata oluştu", e);
        }
        
        // Alt gezinme çubuğunun içindeki öğelerin görsel özelliklerini ayarla
        View bottomNavView = bottomNavigation.getRootView();
        if (bottomNavView instanceof ViewGroup) {
            ((ViewGroup) bottomNavView).setClipChildren(false);
            ((ViewGroup) bottomNavView).setClipToPadding(false);
        }
    }

    private void showBottomNavigation(boolean show) {
        float translationY = show ? 0 : 250;
        float alpha = show ? 1f : 0f;
        
        if (show && bottomNavBackground.getVisibility() == View.GONE) {
            bottomNavBackground.setVisibility(View.VISIBLE);
            bottomNavBackground.setAlpha(0f);
            bottomNavBackground.setTranslationY(250);
        }
        
        bottomNavBackground.animate()
                .translationY(translationY)
                .alpha(alpha)
                .setDuration(500)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .withEndAction(() -> {
                    if (!show) {
                        bottomNavBackground.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            Log.d(TAG, "Fragment yükleniyor: " + fragment.getClass().getSimpleName());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}