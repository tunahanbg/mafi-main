package com.mafi.app.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mafi.app.R;
import com.mafi.app.data.model.ContentType;
import com.mafi.app.ui.viewmodel.ContentViewModel;
import com.mafi.app.utils.NotificationHelper;

public class ContentManagerFragment extends Fragment {
    private static final String TAG = "ContentManagerFragment";

    private ContentViewModel viewModel;
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextContentUrl;
    private RadioGroup radioGroupContentType;
    private Button buttonSaveContent;
    private NotificationHelper notificationHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content_manager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated başladı");

        // View elemanlarını bağla
        editTextTitle = view.findViewById(R.id.edit_text_title);
        editTextDescription = view.findViewById(R.id.edit_text_description);
        editTextContentUrl = view.findViewById(R.id.edit_text_content_url);
        radioGroupContentType = view.findViewById(R.id.radio_group_content_type);
        buttonSaveContent = view.findViewById(R.id.button_save_content);

        // ViewModel'i bağla
        viewModel = new ViewModelProvider(this).get(ContentViewModel.class);

        // Bildirim yardımcısını oluştur
        notificationHelper = new NotificationHelper(requireContext());

        // Gözlemcileri ayarla
        viewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Log.d(TAG, "İçerik kaydetme başarılı");
                // İşlem başarılı, kullanıcıya bildir ve ana sayfaya dön
                Toast.makeText(getContext(), "İçerik başarıyla kaydedildi", Toast.LENGTH_SHORT).show();

                // Bildirim göster
                notificationHelper.showContentAddedNotification(editTextTitle.getText().toString());

                // Ana sayfaya dön
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Log.e(TAG, "Hata: " + errorMessage);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Kaydet butonuna tıklama işlemi
        buttonSaveContent.setOnClickListener(v -> {
            Log.d(TAG, "Kaydet butonuna tıklandı");
            saveContent();
        });
    }

    private void saveContent() {
        // Form verilerini al
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String contentUrl = editTextContentUrl.getText().toString().trim();

        Log.d(TAG, "İçerik kaydediliyor. Başlık: " + title);

        // Validasyon kontrolleri
        if (title.isEmpty()) {
            editTextTitle.setError("Başlık gerekli");
            return;
        }

        if (description.isEmpty()) {
            editTextDescription.setError("Açıklama gerekli");
            return;
        }

        // İçerik tipini al
        int contentTypeId;
        int checkedId = radioGroupContentType.getCheckedRadioButtonId();

        if (checkedId == R.id.radio_button_text) {
            contentTypeId = ContentType.TYPE_TEXT;
        } else if (checkedId == R.id.radio_button_image) {
            contentTypeId = ContentType.TYPE_IMAGE;
        } else if (checkedId == R.id.radio_button_audio) {
            contentTypeId = ContentType.TYPE_AUDIO;
        } else if (checkedId == R.id.radio_button_video) {
            contentTypeId = ContentType.TYPE_VIDEO;
        } else {
            contentTypeId = ContentType.TYPE_TEXT; // Varsayılan
        }

        Log.d(TAG, "İçerik tipi: " + contentTypeId);

        // İçeriği ViewModel aracılığıyla kaydet
        viewModel.createContent(title, description, contentTypeId, contentUrl);
    }
}