package com.mafi.app.data.remote;

import com.mafi.app.data.model.AnalysisRequest;
import com.mafi.app.data.model.ApiResponse;
import com.mafi.app.data.model.ClassifyRequest;
import com.mafi.app.data.model.DirectApiResponse;
import com.mafi.app.data.model.ModelResponse;
import com.mafi.app.data.model.QuestionAnsweringRequest;
import com.mafi.app.data.model.SetModelRequest;
import com.mafi.app.data.model.SummarizeRequest;
import com.mafi.app.data.model.SummarizeResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    
    @POST("/api/analyze/summarize")
    Call<ApiResponse<SummarizeResponse>> summarizeText(@Body SummarizeRequest request);
    
    @POST("/api/analyze/deep-analysis")
    Call<ApiResponse<SummarizeResponse>> analyzeText(@Body AnalysisRequest request);
    
    // Alternatif API yanıt formatları
    @POST("/api/analyze/summarize")
    Call<DirectApiResponse> summarizeTextDirect(@Body SummarizeRequest request);
    
    @POST("/api/analyze/deep-analysis") 
    Call<DirectApiResponse> analyzeTextDirect(@Body AnalysisRequest request);
    
    // Yeni eklenen API endpointleri
    @POST("/api/analyze/classify")
    Call<ApiResponse<SummarizeResponse>> classifyText(@Body ClassifyRequest request);
    
    @POST("/api/analyze/classify")
    Call<DirectApiResponse> classifyTextDirect(@Body ClassifyRequest request);
    
    @POST("/api/analyze/question-answering")
    Call<ApiResponse<SummarizeResponse>> answerQuestion(@Body QuestionAnsweringRequest request);
    
    @POST("/api/analyze/question-answering")
    Call<DirectApiResponse> answerQuestionDirect(@Body QuestionAnsweringRequest request);
    
    // Model yönetimi için API'ler
    @GET("/api/models")
    Call<ModelResponse> getAvailableModels();
    
    @POST("/api/set-model")
    Call<ApiResponse<Void>> setModel(@Body SetModelRequest request);
} 