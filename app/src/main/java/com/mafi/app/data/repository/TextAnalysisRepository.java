package com.mafi.app.data.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.mafi.app.data.model.AnalysisRequest;
import com.mafi.app.data.model.ApiResponse;
import com.mafi.app.data.model.ClassifyRequest;
import com.mafi.app.data.model.DirectApiResponse;
import com.mafi.app.data.model.QuestionAnsweringRequest;
import com.mafi.app.data.model.SummarizeRequest;
import com.mafi.app.data.model.SummarizeResponse;
import com.mafi.app.data.remote.ApiClient;
import com.mafi.app.data.remote.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TextAnalysisRepository {
    private static final String TAG = "TextAnalysisRepository";
    private ApiService apiService;

    public TextAnalysisRepository() {
        this.apiService = ApiClient.getApiService();
    }

    private void handleRawApiError(retrofit2.Response<?> response, MutableLiveData<String> result, MutableLiveData<String> errorMessage) {
        try {
            // Raw response body'yi okuma denemesi
            if (response.errorBody() != null) {
                String errorBodyStr = response.errorBody().string();
                Log.e(TAG, "Raw error body: " + errorBodyStr);
                
                // Basit bir çözüm olarak, yanıtta "markdown" içeren bir şey varsa
                if (errorBodyStr.contains("\"markdown\"") || errorBodyStr.contains("\"message\"")) {
                    String markdown = extractMarkdownFromRawResponse(errorBodyStr);
                    Log.d(TAG, "API yanıtından çıkarılan içerik: " + markdown.substring(0, Math.min(50, markdown.length())) + "...");
                    result.setValue(markdown);
                    return;
                }
                
                // Hiçbir şekilde parse edilemeyen yanıtlar için
                result.setValue("API yanıtı: " + errorBodyStr);
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "Raw yanıt işlenirken hata oluştu", e);
        }
        
        // Hatayı normal şekilde işle
        errorMessage.setValue("API yanıtı işlenemedi: " + response.message());
    }

    public void summarizeText(String text, String model, 
                              MutableLiveData<String> result,
                              MutableLiveData<Boolean> isLoading,
                              MutableLiveData<String> errorMessage) {
        isLoading.setValue(true);
        SummarizeRequest request = new SummarizeRequest(text, model);

        // Önce direkt yanıt formatı ile dene
        apiService.summarizeTextDirect(request).enqueue(new Callback<DirectApiResponse>() {
            @Override
            public void onResponse(Call<DirectApiResponse> call, Response<DirectApiResponse> response) {
                // İstek başarılı ise
                if (response.isSuccessful() && response.body() != null) {
                    DirectApiResponse directResponse = response.body();
                    String markdown = directResponse.getMarkdown();
                    
                    if (markdown != null && !markdown.isEmpty()) {
                        Log.d(TAG, "Direkt API çağrısı başarılı - markdown: " + markdown);
                        isLoading.setValue(false);
                        result.setValue(markdown);
                        return;
                    }
                }
                
                // Direkt API çağrısı başarısız olursa, diğer formata geç
                apiService.summarizeText(request).enqueue(new Callback<ApiResponse<SummarizeResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<SummarizeResponse>> call, Response<ApiResponse<SummarizeResponse>> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<SummarizeResponse> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                SummarizeResponse data = apiResponse.getData();
                                String markdown = data.getMarkdown();
                                Log.d(TAG, "Markdown yanıtı alındı: " + markdown);
                                result.setValue(markdown);
                            } else {
                                // API başarılı döndü ama veri yoksa raw body'yi okuma denemesi
                                handleRawApiError(response, result, errorMessage);
                            }
                        } else {
                            // API başarısız döndüyse raw body'yi okuma denemesi
                            handleRawApiError(response, result, errorMessage);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<SummarizeResponse>> call, Throwable t) {
                        isLoading.setValue(false);
                        Log.e(TAG, "Özet API çağrısı başarısız", t);
                        errorMessage.setValue("Sunucu bağlantısı başarısız: " + t.getMessage());
                    }
                });
            }
            
            @Override
            public void onFailure(Call<DirectApiResponse> call, Throwable t) {
                Log.e(TAG, "Direkt API çağrısı başarısız, alternatif formata geçiliyor", t);
                
                // Direkt API çağrısı başarısız olursa, diğer formata geç
                apiService.summarizeText(request).enqueue(new Callback<ApiResponse<SummarizeResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<SummarizeResponse>> call, Response<ApiResponse<SummarizeResponse>> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<SummarizeResponse> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                SummarizeResponse data = apiResponse.getData();
                                String markdown = data.getMarkdown();
                                Log.d(TAG, "Markdown yanıtı alındı: " + markdown);
                                result.setValue(markdown);
                            } else {
                                // API başarılı döndü ama veri yoksa raw body'yi okuma denemesi
                                handleRawApiError(response, result, errorMessage);
                            }
                        } else {
                            // API başarısız döndüyse raw body'yi okuma denemesi
                            handleRawApiError(response, result, errorMessage);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<SummarizeResponse>> call, Throwable t) {
                        isLoading.setValue(false);
                        Log.e(TAG, "Özet API çağrısı başarısız", t);
                        errorMessage.setValue("Sunucu bağlantısı başarısız: " + t.getMessage());
                    }
                });
            }
        });
    }

    public void analyzeText(String text, String model,
                           MutableLiveData<String> result,
                           MutableLiveData<Boolean> isLoading,
                           MutableLiveData<String> errorMessage) {
        isLoading.setValue(true);
        AnalysisRequest request = new AnalysisRequest(text, model);
        
        // Önce direkt yanıt formatı ile dene
        apiService.analyzeTextDirect(request).enqueue(new Callback<DirectApiResponse>() {
            @Override
            public void onResponse(Call<DirectApiResponse> call, Response<DirectApiResponse> response) {
                // İstek başarılı ise
                if (response.isSuccessful() && response.body() != null) {
                    DirectApiResponse directResponse = response.body();
                    String markdown = directResponse.getMarkdown();
                    
                    if (markdown != null && !markdown.isEmpty()) {
                        Log.d(TAG, "Direkt API çağrısı başarılı - markdown: " + markdown);
                        isLoading.setValue(false);
                        result.setValue(markdown);
                        return;
                    }
                }
                
                // Direkt API çağrısı başarısız olursa, diğer formata geç
                apiService.analyzeText(request).enqueue(new Callback<ApiResponse<SummarizeResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<SummarizeResponse>> call, Response<ApiResponse<SummarizeResponse>> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<SummarizeResponse> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                SummarizeResponse data = apiResponse.getData();
                                String markdown = data.getMarkdown();
                                Log.d(TAG, "Markdown yanıtı alındı: " + markdown);
                                result.setValue(markdown);
                            } else {
                                // API başarılı döndü ama veri yoksa raw body'yi okuma denemesi
                                handleRawApiError(response, result, errorMessage);
                            }
                        } else {
                            // API başarısız döndüyse raw body'yi okuma denemesi
                            handleRawApiError(response, result, errorMessage);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<SummarizeResponse>> call, Throwable t) {
                        isLoading.setValue(false);
                        Log.e(TAG, "Derin analiz API çağrısı başarısız", t);
                        errorMessage.setValue("Sunucu bağlantısı başarısız: " + t.getMessage());
                    }
                });
            }
            
            @Override
            public void onFailure(Call<DirectApiResponse> call, Throwable t) {
                Log.e(TAG, "Direkt API çağrısı başarısız, alternatif formata geçiliyor", t);
                
                // Direkt API çağrısı başarısız olursa, diğer formata geç
                apiService.analyzeText(request).enqueue(new Callback<ApiResponse<SummarizeResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<SummarizeResponse>> call, Response<ApiResponse<SummarizeResponse>> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<SummarizeResponse> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                SummarizeResponse data = apiResponse.getData();
                                String markdown = data.getMarkdown();
                                Log.d(TAG, "Markdown yanıtı alındı: " + markdown);
                                result.setValue(markdown);
                            } else {
                                // API başarılı döndü ama veri yoksa raw body'yi okuma denemesi
                                handleRawApiError(response, result, errorMessage);
                            }
                        } else {
                            // API başarısız döndüyse raw body'yi okuma denemesi
                            handleRawApiError(response, result, errorMessage);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<SummarizeResponse>> call, Throwable t) {
                        isLoading.setValue(false);
                        Log.e(TAG, "Derin analiz API çağrısı başarısız", t);
                        errorMessage.setValue("Sunucu bağlantısı başarısız: " + t.getMessage());
                    }
                });
            }
        });
    }
    
    // Raw API yanıtından markdown içeriğini çıkarmaya çalışan yardımcı metot
    private String extractMarkdownFromRawResponse(String rawResponse) {
        try {
            // "markdown" alanını bul
            if (rawResponse.contains("\"markdown\"")) {
                int start = rawResponse.indexOf("\"markdown\":") + "\"markdown\":".length();
                
                // Eğer sonraki karakter " işareti ise, bu bir string olduğunu gösterir
                if (rawResponse.substring(start).trim().startsWith("\"")) {
                    start = rawResponse.indexOf("\"", start) + 1;
                    // String'in kapandığı yeri bul
                    int end = start;
                    boolean escaped = false;
                    
                    while (end < rawResponse.length()) {
                        char c = rawResponse.charAt(end);
                        if (c == '\\') {
                            escaped = !escaped; // \ işareti ters çeviriyor
                            end++;
                            continue;
                        }
                        
                        if (c == '"' && !escaped) {
                            // Kapatma tırnak işaretini bulduk
                            break;
                        }
                        
                        escaped = false; // Herhangi bir karakterden sonra escape durumu bitmiş oluyor
                        end++;
                    }
                    
                    if (end > start) {
                        String markdown = rawResponse.substring(start, end);
                        // Escape karakterlerini temizle
                        markdown = markdown.replace("\\n", "\n")
                                  .replace("\\\"", "\"")
                                  .replace("\\\\", "\\")
                                  .replace("\\/", "/");
                        
                        Log.d(TAG, "Çıkarılan markdown: " + markdown.substring(0, Math.min(50, markdown.length())) + "...");
                        return markdown;
                    }
                }
            }
            
            // "markdown" bulunamadıysa, message alanını dene
            if (rawResponse.contains("\"message\"")) {
                int start = rawResponse.indexOf("\"message\":") + "\"message\":".length();
                
                // Eğer string ise
                if (rawResponse.substring(start).trim().startsWith("\"")) {
                    start = rawResponse.indexOf("\"", start) + 1;
                    int end = start;
                    boolean escaped = false;
                    
                    while (end < rawResponse.length()) {
                        char c = rawResponse.charAt(end);
                        if (c == '\\') {
                            escaped = !escaped;
                            end++;
                            continue;
                        }
                        
                        if (c == '"' && !escaped) {
                            break;
                        }
                        
                        escaped = false;
                        end++;
                    }
                    
                    if (end > start) {
                        String message = rawResponse.substring(start, end);
                        message = message.replace("\\n", "\n")
                                 .replace("\\\"", "\"")
                                 .replace("\\\\", "\\")
                                 .replace("\\/", "/");
                        return message;
                    }
                }
            }
            
            // Bunlar da bulunamadıysa, "text" veya "content" alanlarını dene
            for (String field : new String[]{"text", "content", "data"}) {
                if (rawResponse.contains("\"" + field + "\"")) {
                    int start = rawResponse.indexOf("\"" + field + "\":") + ("\"" + field + "\":").length();
                    
                    // Eğer string ise
                    if (rawResponse.substring(start).trim().startsWith("\"")) {
                        start = rawResponse.indexOf("\"", start) + 1;
                        int end = start;
                        boolean escaped = false;
                        
                        while (end < rawResponse.length()) {
                            char c = rawResponse.charAt(end);
                            if (c == '\\') {
                                escaped = !escaped;
                                end++;
                                continue;
                            }
                            
                            if (c == '"' && !escaped) {
                                break;
                            }
                            
                            escaped = false;
                            end++;
                        }
                        
                        if (end > start) {
                            String content = rawResponse.substring(start, end);
                            content = content.replace("\\n", "\n")
                                     .replace("\\\"", "\"")
                                     .replace("\\\\", "\\")
                                     .replace("\\/", "/");
                            return content;
                        }
                    }
                }
            }
            
            // Hiçbir şey bulunamadıysa ve bu bir JSON ise, düğünlenmiş bir JSON veya basit metin olabilir
            if (rawResponse.startsWith("{") && rawResponse.endsWith("}")) {
                // İçeriğe göre bir özet dönelim
                if (rawResponse.length() > 100) {
                    return "API yanıtı (JSON): " + rawResponse.substring(0, 100) + "...";
                } else {
                    return "API yanıtı: " + rawResponse;
                }
            }
            
            return rawResponse;
        } catch (Exception e) {
            Log.e(TAG, "Markdown çıkarılırken hata oluştu", e);
            return "API yanıtı işlenemedi: " + e.getMessage();
        }
    }
    
    // Metin sınıflandırma işlemi
    public void classifyText(String text, String model,
                           MutableLiveData<String> result,
                           MutableLiveData<Boolean> isLoading,
                           MutableLiveData<String> errorMessage) {
        isLoading.setValue(true);
        ClassifyRequest request = new ClassifyRequest(text, model);
        
        // Önce direkt yanıt formatı ile dene
        apiService.classifyTextDirect(request).enqueue(new Callback<DirectApiResponse>() {
            @Override
            public void onResponse(Call<DirectApiResponse> call, Response<DirectApiResponse> response) {
                // İstek başarılı ise
                if (response.isSuccessful() && response.body() != null) {
                    DirectApiResponse directResponse = response.body();
                    String markdown = directResponse.getMarkdown();
                    
                    if (markdown != null && !markdown.isEmpty()) {
                        Log.d(TAG, "Direkt API çağrısı başarılı - markdown: " + markdown);
                        isLoading.setValue(false);
                        result.setValue(markdown);
                        return;
                    }
                }
                
                // Direkt API çağrısı başarısız olursa, diğer formata geç
                apiService.classifyText(request).enqueue(new Callback<ApiResponse<SummarizeResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<SummarizeResponse>> call, Response<ApiResponse<SummarizeResponse>> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<SummarizeResponse> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                SummarizeResponse data = apiResponse.getData();
                                String markdown = data.getMarkdown();
                                Log.d(TAG, "Markdown yanıtı alındı: " + markdown);
                                result.setValue(markdown);
                            } else {
                                // API başarılı döndü ama veri yoksa raw body'yi okuma denemesi
                                handleRawApiError(response, result, errorMessage);
                            }
                        } else {
                            // API başarısız döndüyse raw body'yi okuma denemesi
                            handleRawApiError(response, result, errorMessage);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<SummarizeResponse>> call, Throwable t) {
                        isLoading.setValue(false);
                        Log.e(TAG, "Sınıflandırma API çağrısı başarısız", t);
                        errorMessage.setValue("Sunucu bağlantısı başarısız: " + t.getMessage());
                    }
                });
            }
            
            @Override
            public void onFailure(Call<DirectApiResponse> call, Throwable t) {
                Log.e(TAG, "Direkt API çağrısı başarısız, alternatif formata geçiliyor", t);
                
                // Direkt API çağrısı başarısız olursa, diğer formata geç
                apiService.classifyText(request).enqueue(new Callback<ApiResponse<SummarizeResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<SummarizeResponse>> call, Response<ApiResponse<SummarizeResponse>> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<SummarizeResponse> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                SummarizeResponse data = apiResponse.getData();
                                String markdown = data.getMarkdown();
                                Log.d(TAG, "Markdown yanıtı alındı: " + markdown);
                                result.setValue(markdown);
                            } else {
                                // API başarılı döndü ama veri yoksa raw body'yi okuma denemesi
                                handleRawApiError(response, result, errorMessage);
                            }
                        } else {
                            // API başarısız döndüyse raw body'yi okuma denemesi
                            handleRawApiError(response, result, errorMessage);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<SummarizeResponse>> call, Throwable t) {
                        isLoading.setValue(false);
                        Log.e(TAG, "Sınıflandırma API çağrısı başarısız", t);
                        errorMessage.setValue("Sunucu bağlantısı başarısız: " + t.getMessage());
                    }
                });
            }
        });
    }
    
    // Metin soru cevaplama işlemi
    public void answerQuestion(String text, String question, String model,
                             MutableLiveData<String> result,
                             MutableLiveData<Boolean> isLoading,
                             MutableLiveData<String> errorMessage) {
        isLoading.setValue(true);
        QuestionAnsweringRequest request = new QuestionAnsweringRequest(text, question, model);
        
        // Önce direkt yanıt formatı ile dene
        apiService.answerQuestionDirect(request).enqueue(new Callback<DirectApiResponse>() {
            @Override
            public void onResponse(Call<DirectApiResponse> call, Response<DirectApiResponse> response) {
                // İstek başarılı ise
                if (response.isSuccessful() && response.body() != null) {
                    DirectApiResponse directResponse = response.body();
                    String markdown = directResponse.getMarkdown();
                    
                    if (markdown != null && !markdown.isEmpty()) {
                        Log.d(TAG, "Direkt API çağrısı başarılı - markdown: " + markdown);
                        isLoading.setValue(false);
                        result.setValue(markdown);
                        return;
                    }
                }
                
                // Direkt API çağrısı başarısız olursa, diğer formata geç
                apiService.answerQuestion(request).enqueue(new Callback<ApiResponse<SummarizeResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<SummarizeResponse>> call, Response<ApiResponse<SummarizeResponse>> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<SummarizeResponse> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                SummarizeResponse data = apiResponse.getData();
                                String markdown = data.getMarkdown();
                                Log.d(TAG, "Markdown yanıtı alındı: " + markdown);
                                result.setValue(markdown);
                            } else {
                                // API başarılı döndü ama veri yoksa raw body'yi okuma denemesi
                                handleRawApiError(response, result, errorMessage);
                            }
                        } else {
                            // API başarısız döndüyse raw body'yi okuma denemesi
                            handleRawApiError(response, result, errorMessage);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<SummarizeResponse>> call, Throwable t) {
                        isLoading.setValue(false);
                        Log.e(TAG, "Soru cevaplama API çağrısı başarısız", t);
                        errorMessage.setValue("Sunucu bağlantısı başarısız: " + t.getMessage());
                    }
                });
            }
            
            @Override
            public void onFailure(Call<DirectApiResponse> call, Throwable t) {
                Log.e(TAG, "Direkt API çağrısı başarısız, alternatif formata geçiliyor", t);
                
                // Direkt API çağrısı başarısız olursa, diğer formata geç
                apiService.answerQuestion(request).enqueue(new Callback<ApiResponse<SummarizeResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<SummarizeResponse>> call, Response<ApiResponse<SummarizeResponse>> response) {
                        isLoading.setValue(false);
                        
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<SummarizeResponse> apiResponse = response.body();
                            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                                SummarizeResponse data = apiResponse.getData();
                                String markdown = data.getMarkdown();
                                Log.d(TAG, "Markdown yanıtı alındı: " + markdown);
                                result.setValue(markdown);
                            } else {
                                // API başarılı döndü ama veri yoksa raw body'yi okuma denemesi
                                handleRawApiError(response, result, errorMessage);
                            }
                        } else {
                            // API başarısız döndüyse raw body'yi okuma denemesi
                            handleRawApiError(response, result, errorMessage);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<ApiResponse<SummarizeResponse>> call, Throwable t) {
                        isLoading.setValue(false);
                        Log.e(TAG, "Soru cevaplama API çağrısı başarısız", t);
                        errorMessage.setValue("Sunucu bağlantısı başarısız: " + t.getMessage());
                    }
                });
            }
        });
    }
} 