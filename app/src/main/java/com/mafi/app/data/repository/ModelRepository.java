package com.mafi.app.data.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.mafi.app.data.model.ApiResponse;
import com.mafi.app.data.model.Model;
import com.mafi.app.data.model.ModelResponse;
import com.mafi.app.data.model.SetModelRequest;
import com.mafi.app.data.remote.ApiClient;
import com.mafi.app.data.remote.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModelRepository {
    private static final String TAG = "ModelRepository";
    private ApiService apiService;
    
    // Şu an kullanımdaki model
    private static String currentModel = "llama3.1"; // Varsayılan model
    
    public ModelRepository() {
        this.apiService = ApiClient.getApiService();
    }
    
    /**
     * Mevcut kullanılan model adını döndürür
     */
    public static String getCurrentModel() {
        return currentModel;
    }
    
    /**
     * Erişilebilir AI modellerini getir
     */
    public void getAvailableModels(MutableLiveData<List<String>> models,
                                  MutableLiveData<Boolean> isLoading,
                                  MutableLiveData<String> errorMessage) {
        isLoading.setValue(true);
        
        apiService.getAvailableModels().enqueue(new Callback<ModelResponse>() {
            @Override
            public void onResponse(Call<ModelResponse> call, Response<ModelResponse> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ModelResponse modelResponse = response.body();
                    if (modelResponse.getModels() != null) {
                        // API'den gelen mevcut model bilgisini güncelle
                        if (modelResponse.getCurrentModel() != null) {
                            currentModel = modelResponse.getCurrentModel();
                        }
                        
                        // Model nesnelerini String listesine dönüştür
                        List<String> modelNames = modelResponse.getModels().stream()
                                .map(Model::getName)
                                .collect(Collectors.toList());
                        
                        Log.d(TAG, "Modeller başarıyla alındı: " + modelNames.size() + " model var");
                        models.setValue(modelNames);
                    } else {
                        String error = modelResponse.getMessage() != null ? 
                                     modelResponse.getMessage() : "Model listesi alınamadı";
                        Log.e(TAG, "Model listesi alınamadı: " + error);
                        errorMessage.setValue(error);
                        // Boş bir liste döndür
                        models.setValue(new ArrayList<>());
                    }
                } else {
                    Log.e(TAG, "Model API yanıtı başarısız: " + response.message());
                    errorMessage.setValue("Model listesi alınamadı: " + response.message());
                    // Boş bir liste döndür
                    models.setValue(new ArrayList<>());
                }
            }
            
            @Override
            public void onFailure(Call<ModelResponse> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Model API çağrısı başarısız", t);
                errorMessage.setValue("Sunucuya bağlanılamadı: " + t.getMessage());
                // Boş bir liste döndür
                models.setValue(new ArrayList<>());
            }
        });
    }
    
    /**
     * Aktif modeli değiştir
     */
    public void setModel(String modelName,
                       MutableLiveData<Boolean> success,
                       MutableLiveData<Boolean> isLoading,
                       MutableLiveData<String> errorMessage) {
        isLoading.setValue(true);
        SetModelRequest request = new SetModelRequest(modelName);
        
        apiService.setModel(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Log.d(TAG, "Model başarıyla değiştirildi: " + modelName);
                        // Yeni modeli kaydet
                        currentModel = modelName;
                        success.setValue(true);
                    } else {
                        String error = apiResponse.getMessage() != null ? 
                                     apiResponse.getMessage() : "Model değiştirilemedi";
                        Log.e(TAG, "Model değiştirilemedi: " + error);
                        errorMessage.setValue(error);
                        success.setValue(false);
                    }
                } else {
                    Log.e(TAG, "Model değiştirme API yanıtı başarısız: " + response.message());
                    errorMessage.setValue("Model değiştirilemedi: " + response.message());
                    success.setValue(false);
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                isLoading.setValue(false);
                Log.e(TAG, "Model değiştirme API çağrısı başarısız", t);
                errorMessage.setValue("Sunucuya bağlanılamadı: " + t.getMessage());
                success.setValue(false);
            }
        });
    }
} 