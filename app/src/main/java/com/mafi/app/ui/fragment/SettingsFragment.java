package com.mafi.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.mafi.app.R;
import com.mafi.app.data.local.SharedPreferencesManager;
import com.mafi.app.ui.viewmodel.SettingsViewModel;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private SharedPreferencesManager preferencesManager;
    private RadioGroup radioGroupTheme;
    private SwitchMaterial switchNotification;
    private TextInputLayout textInputAiModel;
    private MaterialAutoCompleteTextView autoCompleteAiModel;
    private ProgressBar progressBarModels;
    private TextView tvErrorModels;
    private Button btnRetryModels;
    
    private SettingsViewModel viewModel;
    private ArrayAdapter<String> modelAdapter;
    private List<String> modelList = new ArrayList<>();
    private String selectedModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ViewModel bağlantısı
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        // SharedPreferences yöneticisini al
        preferencesManager = SharedPreferencesManager.getInstance(requireContext());

        // View elemanlarını bağla
        radioGroupTheme = view.findViewById(R.id.radio_group_theme);
        switchNotification = view.findViewById(R.id.switch_notification);
        textInputAiModel = view.findViewById(R.id.text_input_ai_model);
        autoCompleteAiModel = view.findViewById(R.id.auto_complete_ai_model);
        progressBarModels = view.findViewById(R.id.progress_bar_models);
        
        // Hata durumu için view'ları ekleyelim
        tvErrorModels = view.findViewById(R.id.text_view_error_models);
        btnRetryModels = view.findViewById(R.id.button_retry_models);
        
        btnRetryModels.setOnClickListener(v -> {
            tvErrorModels.setVisibility(View.GONE);
            btnRetryModels.setVisibility(View.GONE);
            loadModels();
        });

        // Model adaptörünü ayarla
        modelAdapter = new ArrayAdapter<>(requireContext(), 
                R.layout.item_dropdown, modelList);
        autoCompleteAiModel.setAdapter(modelAdapter);

        // Mevcut ayarları yükle
        loadCurrentSettings();
        
        // Model listesini yükle
        loadModels();
        
        // AutoCompleteTextView'da model seçimi için listener
        autoCompleteAiModel.setOnItemClickListener((parent, view1, position, id) -> {
            // Kullanıcı aynı modeli seçtiyse değişiklik yapma
            String selectedModel = modelList.get(position);
            if (!selectedModel.equals(viewModel.getCurrentModel())) {
                // Yeni model seçildi, API'ye gönder
                viewModel.setModel(selectedModel);
            }
        });

        // Tema değişikliği için listener
        radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            String theme;
            if (checkedId == R.id.radio_button_light) {
                theme = "light";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (checkedId == R.id.radio_button_dark) {
                theme = "dark";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                theme = "system";
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }

            preferencesManager.setTheme(theme);
            Snackbar.make(requireView(), "Tema değiştirildi", Snackbar.LENGTH_SHORT).show();
        });

        // Bildirim ayarı için listener
        switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferencesManager.setNotificationEnabled(isChecked);
            Snackbar.make(requireView(), 
                    isChecked ? "Bildirimler etkinleştirildi" : "Bildirimler devre dışı bırakıldı",
                    Snackbar.LENGTH_SHORT).show();
        });
        
        // ViewModel'den gözlemcileri ayarla
        setupObservers();
    }
    
    private void setupObservers() {
        // Model listesi değişikliğini gözle
        viewModel.getAvailableModels().observe(getViewLifecycleOwner(), models -> {
            if (models != null) {
                modelList.clear();
                modelList.addAll(models);
                modelAdapter.notifyDataSetChanged();
                
                if (models.isEmpty()) {
                    tvErrorModels.setText("Kullanılabilir model bulunamadı");
                    tvErrorModels.setVisibility(View.VISIBLE);
                    btnRetryModels.setVisibility(View.VISIBLE);
                    textInputAiModel.setVisibility(View.GONE);
                } else {
                    tvErrorModels.setVisibility(View.GONE);
                    btnRetryModels.setVisibility(View.GONE);
                    textInputAiModel.setVisibility(View.VISIBLE);
                    
                    // Mevcut seçili modeli belirle
                    setCurrentModelSelection();
                }
            }
        });
        
        // Yükleme durumunu gözle
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBarModels.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            
            // Yükleme sırasında diğer görünümleri gizle
            if (isLoading) {
                tvErrorModels.setVisibility(View.GONE);
                btnRetryModels.setVisibility(View.GONE);
            }
        });
        
        // Hata mesajlarını gözle
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                tvErrorModels.setText(error);
                tvErrorModels.setVisibility(View.VISIBLE);
                btnRetryModels.setVisibility(View.VISIBLE);
                textInputAiModel.setVisibility(View.GONE);
                
                Snackbar.make(requireView(), error, Snackbar.LENGTH_LONG).show();
            }
        });
        
        // Model değişim işlemini gözle
        viewModel.getModelChangeSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Snackbar.make(requireView(), "AI modeli başarıyla değiştirildi", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadModels() {
        // Kullanılabilir modelleri yükle
        viewModel.loadAvailableModels();
    }
    
    private void setCurrentModelSelection() {
        // Mevcut modeli AutoCompleteTextView'da ayarla
        String currentModel = viewModel.getCurrentModel();
        autoCompleteAiModel.setText(currentModel, false);
    }

    private void loadCurrentSettings() {
        // Tema ayarını yükle
        String currentTheme = preferencesManager.getTheme();
        switch (currentTheme) {
            case "light":
                radioGroupTheme.check(R.id.radio_button_light);
                break;
            case "dark":
                radioGroupTheme.check(R.id.radio_button_dark);
                break;
            default:
                radioGroupTheme.check(R.id.radio_button_system);
                break;
        }

        // Bildirim ayarını yükle
        switchNotification.setChecked(preferencesManager.isNotificationEnabled());
    }
}