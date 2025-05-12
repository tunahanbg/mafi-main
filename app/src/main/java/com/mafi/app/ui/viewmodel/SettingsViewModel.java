package com.mafi.app.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mafi.app.data.repository.ModelRepository;

import java.util.List;

public class SettingsViewModel extends AndroidViewModel {
    
    private ModelRepository modelRepository;
    
    private MutableLiveData<List<String>> availableModels = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> modelChangeSuccess = new MutableLiveData<>();
    
    public SettingsViewModel(@NonNull Application application) {
        super(application);
        modelRepository = new ModelRepository();
    }
    
    public LiveData<List<String>> getAvailableModels() {
        return availableModels;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Boolean> getModelChangeSuccess() {
        return modelChangeSuccess;
    }
    
    public String getCurrentModel() {
        return ModelRepository.getCurrentModel();
    }
    
    /**
     * Sunucudan kullanılabilir modelleri yükler
     */
    public void loadAvailableModels() {
        modelRepository.getAvailableModels(availableModels, isLoading, errorMessage);
    }
    
    /**
     * Sunucuda kullanılacak modeli değiştirir
     */
    public void setModel(String modelName) {
        modelRepository.setModel(modelName, modelChangeSuccess, isLoading, errorMessage);
    }
} 