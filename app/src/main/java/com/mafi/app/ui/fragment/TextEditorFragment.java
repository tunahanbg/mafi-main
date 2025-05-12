package com.mafi.app.ui.fragment;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mafi.app.R;
import com.mafi.app.data.model.Content;
import com.mafi.app.ui.viewmodel.TextEditorViewModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditorFragment extends Fragment {
    private static final String TAG = "TextEditorFragment";
    private static final String ARG_CONTENT_ID = "content_id";

    private TextEditorViewModel viewModel;
    private TextView textViewTitle;
    private EditText editTextContent;
    private MaterialButton buttonSummarize;
    private MaterialButton buttonExpand;
    private MaterialButton buttonClassify;
    private MaterialButton buttonQuestion;
    private ProgressBar progressBar;
    private View dimBackground;
    
    // AI Sonuç Paneli Bileşenleri
    private CardView cardAiResult;
    private TextView textViewAiTitle;
    private TextView textViewAiContent;
    private MaterialButton buttonReject;
    private MaterialButton buttonAccept;
    
    private String originalText; // Orijinal metni saklamak için
    private String aiGeneratedText; // AI tarafından oluşturulan metni saklamak için

    private int contentId;

    // API çağrısı yapılıp yapılmadığını kontrol etmek için bayrak
    private boolean isApiCallInProgress = false;

    public static TextEditorFragment newInstance(int contentId) {
        TextEditorFragment fragment = new TextEditorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CONTENT_ID, contentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            contentId = getArguments().getInt(ARG_CONTENT_ID, -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_text_editor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated başladı. İçerik ID: " + contentId);

        // View elemanlarını bağla
        textViewTitle = view.findViewById(R.id.text_view_title);
        editTextContent = view.findViewById(R.id.edit_text_content);
        buttonSummarize = view.findViewById(R.id.button_summarize);
        buttonExpand = view.findViewById(R.id.button_expand);
        buttonClassify = view.findViewById(R.id.button_classify);
        buttonQuestion = view.findViewById(R.id.button_question);
        progressBar = view.findViewById(R.id.progress_bar);
        dimBackground = view.findViewById(R.id.view_dim_background);
        
        // AI Sonuç Paneli elemanlarını bağla
        cardAiResult = view.findViewById(R.id.card_ai_result);
        textViewAiTitle = view.findViewById(R.id.text_view_ai_title);
        textViewAiContent = view.findViewById(R.id.text_view_ai_content);
        buttonReject = view.findViewById(R.id.button_reject);
        buttonAccept = view.findViewById(R.id.button_accept);

        // ViewModel'i bağla
        viewModel = new ViewModelProvider(this).get(TextEditorViewModel.class);

        // Gözlemcileri ayarla
        viewModel.getSelectedContent().observe(getViewLifecycleOwner(), content -> {
            if (content != null) {
                textViewTitle.setText(content.getTitle());
                
                // Not ilk yüklendiğinde, bu AI yanıtı değil, bu yüzden isApiCallInProgress false olmalı
                isApiCallInProgress = false;
            }
        });

        viewModel.getEditedText().observe(getViewLifecycleOwner(), text -> {
            if (text != null) {
                // Eğer bir API çağrısı yapıldıysa ve metin farklıysa
                if (isApiCallInProgress && !text.equals(editTextContent.getText().toString())) {
                    Log.d(TAG, "API yanıtı alındı: " + text.substring(0, Math.min(50, text.length())) + "...");
                    
                    // API çağrısı tamamlandı
                    isApiCallInProgress = false;
                    
                    // AI panelini gösterme
                    showAiResultPanel(text);
                }
                // Eğer API çağrısı yapılmadıysa (ilk yükleme veya doğrudan bir değişiklik)
                else if (!isApiCallInProgress) {
                    // Doğrudan içeriği göster, AI paneli gösterme
                    updateEditorContent(text);
                }
            }
        });

        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                Toast.makeText(getContext(), "İçerik başarıyla kaydedildi", Toast.LENGTH_SHORT).show();
                viewModel.resetSaveSuccess();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                // Hata oluştuğunda API çağrısı durumunu sıfırla
                isApiCallInProgress = false;
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                dimBackground.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                setButtonsEnabled(!isLoading);
                
                // Yükleme başladığında API çağrısının başladığını işaretle
                if (isLoading) {
                    isApiCallInProgress = true;
                    Toast.makeText(getContext(), "AI modeli çalışıyor, lütfen bekleyin (en fazla 3 dakika sürebilir)...", Toast.LENGTH_LONG).show();
                }
            }
        });

        // İçeriği yükle
        if (contentId > 0) {
            viewModel.loadContent(contentId);
        } else {
            Toast.makeText(getContext(), "Geçersiz içerik ID", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        }

        // Buton işlevlerini ayarla
        buttonSummarize.setOnClickListener(v -> {
            String currentText = editTextContent.getText().toString();
            originalText = currentText; // Orijinal metni sakla
            Log.d(TAG, "Özetle butonuna basıldı. Metin: " + currentText.substring(0, Math.min(50, currentText.length())) + "...");
            
            // API çağrısı başlıyor
            isApiCallInProgress = true;
            
            // EditText içeriğini ViewModel'e gönder
            viewModel.setEditedText(currentText);
            viewModel.summarizeText();
        });

        buttonExpand.setOnClickListener(v -> {
            String currentText = editTextContent.getText().toString();
            originalText = currentText; // Orijinal metni sakla
            Log.d(TAG, "Derin Analiz butonuna basıldı. Metin: " + currentText.substring(0, Math.min(50, currentText.length())) + "...");
            
            // API çağrısı başlıyor
            isApiCallInProgress = true;
            
            // EditText içeriğini ViewModel'e gönder
            viewModel.setEditedText(currentText);
            viewModel.expandText();
        });

        buttonClassify.setOnClickListener(v -> {
            String currentText = editTextContent.getText().toString();
            if (currentText.isEmpty()) {
                Toast.makeText(getContext(), "Lütfen sınıflandırılacak metin girin", Toast.LENGTH_SHORT).show();
                return;
            }
            
            originalText = currentText; // Orijinal metni sakla
            Log.d(TAG, "Sınıflandır butonuna basıldı. Metin: " + currentText.substring(0, Math.min(50, currentText.length())) + "...");
            
            // API çağrısı başlıyor
            isApiCallInProgress = true;
            
            // EditText içeriğini ViewModel'e gönder
            viewModel.setEditedText(currentText);
            viewModel.classifyText();
        });

        buttonQuestion.setOnClickListener(v -> {
            String currentText = editTextContent.getText().toString();
            if (currentText.isEmpty()) {
                Toast.makeText(getContext(), "Lütfen soru sormak için bir metin girin", Toast.LENGTH_SHORT).show();
                return;
            }
            
            originalText = currentText; // Orijinal metni sakla
            
            // Soru sorma dialogunu göster
            showQuestionDialog();
        });
        
        // AI Sonuç Paneli butonlarını ayarla
        buttonReject.setOnClickListener(v -> {
            // Reddet butonuna basıldığında paneli kapat ve orijinal metne dön
            hideAiResultPanel();
            Log.d(TAG, "AI içeriği reddedildi, orijinal metin korunuyor");
            Toast.makeText(getContext(), "AI içeriği reddedildi", Toast.LENGTH_SHORT).show();
        });
        
        buttonAccept.setOnClickListener(v -> {
            // Kabul et butonuna basıldığında paneli kapat ve AI metnini kullan
            hideAiResultPanel();
            
            // Oluşturulan metni editöre yerleştir
            if (aiGeneratedText != null) {
                // İçeriği format için hazırla
                String preparedText = prepareContentForDisplay(aiGeneratedText);
                
                // Editörü güncelle
                editTextContent.setText(preparedText);
                editTextContent.setSelection(editTextContent.getText().length());
                
                // İçeriği kaydet
                Content content = viewModel.getSelectedContent().getValue();
                if (content != null) {
                    // HTML yerine düz metin olarak kaydet
                    content.setDescription(preparedText);
                    viewModel.saveContent();
                }
                
                Log.d(TAG, "AI içeriği kabul edildi ve uygulandı");
                Toast.makeText(getContext(), "AI içeriği uygulandı", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Soru sorma dialogunu gösterir
     */
    private void showQuestionDialog() {
        // Dialog için View oluştur
        TextInputLayout textInputLayout = new TextInputLayout(requireContext());
        textInputLayout.setHint("Metne ilişkin sorunuzu yazın");
        textInputLayout.setPadding(32, 16, 32, 0);

        //Float yapılacak hint özelliğini aktif et
        textInputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        textInputLayout.setHintAnimationEnabled(true);
        textInputLayout.setHintEnabled(true);
        
        TextInputEditText editTextQuestion = new TextInputEditText(requireContext());
        textInputLayout.addView(editTextQuestion);
        
        // Dialog oluştur
        new MaterialAlertDialogBuilder(requireContext())
            .setTitle("Soru Sor")
            .setView(textInputLayout)
            .setPositiveButton("Sor", (dialog, which) -> {
                String question = editTextQuestion.getText().toString();
                if (question.isEmpty()) {
                    Toast.makeText(getContext(), "Lütfen bir soru girin", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                Log.d(TAG, "Soru soruldu: " + question);
                
                // API çağrısı başlıyor
                isApiCallInProgress = true;
                
                // Soruyu gönder
                viewModel.answerQuestion(question);
            })
            .setNegativeButton("İptal", null)
            .show();
    }
    
    /**
     * AI sonuç panelini gösterir
     */
    private void showAiResultPanel(String aiText) {
        if (aiText == null || aiText.isEmpty()) {
            Log.e(TAG, "AI yanıtı boş veya null, panel gösterilmiyor");
            Toast.makeText(getContext(), "AI yanıtı alınamadı", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            // AI sonucunu sakla
            aiGeneratedText = aiText;
            
            // Markdown formatını HTML'e dönüştür
            String formattedText = markdownToHtml(aiText);
            
            // Panel içeriğini ayarla - HTML olarak göster
            Log.d(TAG, "AI panel içeriği ayarlanıyor. İçerik: " + aiText.substring(0, Math.min(50, aiText.length())) + "...");
            
            // Başlık güncelleme - metni kontrol ederek hangi işlem olduğunu belirlemeye çalış
            String title = "AI Tarafından Oluşturulan İçerik";
            if (aiText.contains("GENEL DEĞERLENDİRME") || aiText.contains("derinlemesine")) {
                title = "Derin Analiz Sonucu";
                textViewAiTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_dark));
            } else if (aiText.contains("sınıflandırma") || aiText.contains("kategorisi")) {
                title = "Sınıflandırma Sonucu";
                textViewAiTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.accent));
            } else if (aiText.contains("Cevap:") || aiText.contains("Soru:")) {
                title = "Soru Cevap Sonucu";
                textViewAiTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.success));
            } else if (aiText.contains("Özet:") || aiText.contains("özetlemek")) {
                title = "Özet Sonucu";
                textViewAiTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.info));
            }
            
            textViewAiTitle.setText(title);
            textViewAiContent.setText(Html.fromHtml(formattedText, Html.FROM_HTML_MODE_LEGACY));
            textViewAiContent.setMovementMethod(LinkMovementMethod.getInstance());
            
            // Animasyon oluştur
            Animation slideUp = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_up);
            cardAiResult.startAnimation(slideUp);
            
            // Paneli göster
            cardAiResult.setVisibility(View.VISIBLE);
            Log.d(TAG, "AI paneli gösterildi");
        } catch (Exception e) {
            Log.e(TAG, "AI paneli gösterilirken hata oluştu: " + e.getMessage(), e);
            Toast.makeText(getContext(), "İçerik gösterilirken hata oluştu", Toast.LENGTH_SHORT).show();
            isApiCallInProgress = false; // API çağrısı durumunu sıfırla
        }
    }
    
    /**
     * AI sonuç panelini gizler
     */
    private void hideAiResultPanel() {
        // Animasyon oluştur
        Animation slideDown = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_down);
        
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            
            @Override
            public void onAnimationEnd(Animation animation) {
                cardAiResult.setVisibility(View.GONE);
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        
        // Animasyon uygula
        cardAiResult.startAnimation(slideDown);
    }
    
    private void setButtonsEnabled(boolean enabled) {
        ColorStateList primaryColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary));
        ColorStateList disabledColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.button_disabled));
        
        // Tüm butonları güncelle
        buttonSummarize.setEnabled(enabled);
        buttonExpand.setEnabled(enabled);
        buttonClassify.setEnabled(enabled);
        buttonQuestion.setEnabled(enabled);
        
        if (!enabled) {
            buttonSummarize.setBackgroundTintList(disabledColor);
            buttonExpand.setBackgroundTintList(disabledColor);
            buttonClassify.setBackgroundTintList(disabledColor);
            buttonQuestion.setBackgroundTintList(disabledColor);
        } else {
            buttonSummarize.setBackgroundTintList(primaryColor);
            buttonExpand.setBackgroundTintList(primaryColor);
            buttonClassify.setBackgroundTintList(primaryColor);
            buttonQuestion.setBackgroundTintList(primaryColor);
        }
    }
    
    // Basit Markdown formatını HTML'e dönüştürme
    private String markdownToHtml(String markdown) {
        if (markdown == null) {
            return "";
        }
        
        Log.d(TAG, "Markdown metni HTML'e dönüştürülüyor: " + markdown.substring(0, Math.min(50, markdown.length())) + "...");
        
        // Eğer metin zaten HTML içeriyorsa, olduğu gibi döndür
        if (markdown.contains("<h1") || markdown.contains("<br>") || markdown.contains("<i>")) {
            Log.d(TAG, "Metin zaten HTML içeriyor, olduğu gibi kullanılıyor");
            return markdown;
        }
        
        // API yanıtı metni içeriyorsa düzeltme
        if (markdown.startsWith("API yanıtı")) {
            // Metni daha okunabilir hale getir
            return "<div style='background-color:#FFF3CD;padding:12px;border-radius:8px;border:1px solid #FFEEBA;'>" +
                   "<p style='color:#856404;font-weight:bold;'>⚠️ API Yanıtı İşlenemiyor</p>" +
                   "<p>" + markdown + "</p>" +
                   "</div>";
        }
        
        // Markdown dönüşümü
        // Başlıklar
        markdown = markdown.replaceAll("(?m)^# (.*?)$", "<h1 style='color:#550AE1'>$1</h1>");
        markdown = markdown.replaceAll("(?m)^## (.*?)$", "<h2 style='color:#550AE1'>$1</h2>");
        markdown = markdown.replaceAll("(?m)^### (.*?)$", "<h3 style='color:#550AE1'>$1</h3>");
        
        // Kalın metin
        markdown = markdown.replaceAll("\\*\\*(.*?)\\*\\*", "<b>$1</b>");
        
        // İtalik metin
        markdown = markdown.replaceAll("\\*(.*?)\\*", "<i>$1</i>");
        
        // Listeler
        markdown = markdown.replaceAll("(?m)^- (.*?)$", "• $1<br>");
        markdown = markdown.replaceAll("(?m)^\\d+\\. (.*?)$", "$1. $2<br>");
        
        // Linkler
        Pattern linkPattern = Pattern.compile("\\[(.*?)\\]\\((.*?)\\)");
        Matcher linkMatcher = linkPattern.matcher(markdown);
        StringBuffer sb = new StringBuffer();
        while (linkMatcher.find()) {
            String text = linkMatcher.group(1);
            String url = linkMatcher.group(2);
            linkMatcher.appendReplacement(sb, "<a href=\"" + url + "\" style='color:#550AE1'>" + text + "</a>");
        }
        linkMatcher.appendTail(sb);
        markdown = sb.toString();
        
        // Kod blokları
        markdown = markdown.replaceAll("```(.*?)```", "<pre style='background-color:#DED0FC;padding:8px;border-radius:4px'>$1</pre>");
        
        // Yeni satırlar
        markdown = markdown.replaceAll("\n\n", "<br><br>");
        markdown = markdown.replaceAll("\n", "<br>");
        
        Log.d(TAG, "HTML sonucu: " + markdown.substring(0, Math.min(50, markdown.length())) + "...");
        return markdown;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Fragment kapatılırken içeriği kaydet
        saveContent();
    }

    private void saveContent() {
        if (editTextContent.getText() != null) {
            viewModel.setEditedText(editTextContent.getText().toString());
            viewModel.saveContent();
        }
    }

    // Format kontrolü yapıp düzgün şekilde görüntüleme için yardımcı metod
    private String prepareContentForDisplay(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        // Eğer API'den dönen HTML etiketlerini içeriyorsa, temizleme yapalım
        if (content.contains("<br>") || content.contains("<h1") || content.contains("<b>")) {
            // HTML etiketleri \n ile değiştir
            String cleanedText = content
                .replaceAll("<br>", "\n")
                .replaceAll("<br/>", "\n")
                .replaceAll("<h1[^>]*>(.*?)</h1>", "$1")
                .replaceAll("<h2[^>]*>(.*?)</h2>", "$1")
                .replaceAll("<h3[^>]*>(.*?)</h3>", "$1")
                .replaceAll("<b>(.*?)</b>", "$1")
                .replaceAll("<i>(.*?)</i>", "$1")
                .replaceAll("<.*?>", ""); // Diğer HTML etiketlerini temizle
            
            Log.d(TAG, "İçerik HTML etiketlerinden temizlendi: " + cleanedText.substring(0, Math.min(50, cleanedText.length())) + "...");
            return cleanedText;
        }
        
        return content;
    }

    // UpdateEditorContent fonksiyonunu güncelle
    private void updateEditorContent(String text) {
        Log.d(TAG, "İçerik doğrudan güncelleniyor (AI paneli kullanılmadan)");
        
        // İçeriği format için hazırla
        String preparedText = prepareContentForDisplay(text);
        
        // EditText'e ayarla
        editTextContent.setText(preparedText);
        
        // İmleci en sona getir
        editTextContent.setSelection(editTextContent.getText().length());
    }
}