package com.mafi.app.data.remote;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String TAG = "ApiClient";
    // Android emülatörü için 10.0.2.2 adresi host makinenin localhost'unu gösterir
    private static final String BASE_URL = "http://10.0.2.2:3030";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Loglama konfigürasyonu
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(message -> {
                // Her yanıtı detaylı şekilde logla
                if (message.startsWith("{") || message.startsWith("[")) {
                    Log.d(TAG, "API JSON: " + message);
                }
                Log.d(TAG, message);
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            // JSON yanıtlarını kontrol eden bir interceptor ekleyelim
            Interceptor jsonDebugInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Response response = chain.proceed(request);
                    
                    if (response.isSuccessful()) {
                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            try {
                                String responseString = responseBody.string();
                                Log.d(TAG, "API Yanıtı: " + responseString);
                                
                                // Orijinal yanıtı kapatma ve yeni bir yanıt oluşturma
                                ResponseBody newResponseBody = ResponseBody.create(
                                        responseBody.contentType(), 
                                        responseString
                                );
                                return response.newBuilder()
                                        .body(newResponseBody)
                                        .build();
                                
                            } catch (Exception e) {
                                Log.e(TAG, "Yanıt gövdesi okunamadı", e);
                            }
                        }
                    }
                    return response;
                }
            };

            // Gson yapılandırma
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(180, TimeUnit.SECONDS)
                    .readTimeout(180, TimeUnit.SECONDS)
                    .writeTimeout(180, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(jsonDebugInterceptor)
                    .build();
            
            Log.d(TAG, "Retrofit istemcisi oluşturuluyor. BASE_URL: " + BASE_URL + ", timeout: 3 dakika");

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
} 