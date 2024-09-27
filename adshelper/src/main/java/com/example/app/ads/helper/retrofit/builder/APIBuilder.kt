package com.example.app.ads.helper.retrofit.builder

import com.example.app.ads.helper.retrofit.listener.APIInterface
import com.example.app.ads.helper.retrofit.utils.FORCE_UPDATE_BASE_URL
import com.example.app.ads.helper.retrofit.utils.REVIEW_BASE_URL
import com.example.app.ads.helper.utils.isEnableDebugMode
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object APIBuilder {
    val forceUpdateClient: APIInterface get() = forceUpdateClientBuilder.create(APIInterface::class.java)
    val reviewClient: APIInterface get() = reviewClientBuilder.create(APIInterface::class.java)

    private val okHttpClient: OkHttpClient
        get() {
            return OkHttpClient.Builder().apply {
                this.callTimeout(5, TimeUnit.SECONDS)
                this.connectTimeout(5, TimeUnit.SECONDS)
                this.readTimeout(5, TimeUnit.SECONDS)
                this.writeTimeout(5, TimeUnit.SECONDS)
                if (isEnableDebugMode) {
                    this.addInterceptor(provideLoggingInterceptor)
                }
            }.build()
        }

    private val provideLoggingInterceptor = run {
        HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val forceUpdateBaseUrl: String get() = FORCE_UPDATE_BASE_URL

    private val forceUpdateClientBuilder: Retrofit
        get() {
            return Retrofit.Builder()
                .baseUrl(forceUpdateBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }

    private val reviewBaseUrl: String get() = REVIEW_BASE_URL

    private val reviewClientBuilder: Retrofit
        get() {
            return Retrofit.Builder()
                .baseUrl(reviewBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }

}