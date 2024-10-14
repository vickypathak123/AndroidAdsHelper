package com.example.app.ads.helper.retrofit.builder

import android.annotation.SuppressLint
import com.example.app.ads.helper.retrofit.listener.APIInterface
import com.example.app.ads.helper.retrofit.utils.FORCE_UPDATE_BASE_URL
import com.example.app.ads.helper.retrofit.utils.REVIEW_BASE_URL
import com.example.app.ads.helper.utils.isEnableDebugMode
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager


object APIBuilder {
    val forceUpdateClient: APIInterface get() = forceUpdateClientBuilder.create(APIInterface::class.java)
    val reviewClient: APIInterface get() = reviewClientBuilder.create(APIInterface::class.java)

    private val trustManager: X509TrustManager =
        @SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            @SuppressLint("TrustAllX509TrustManager")
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

        }


    private val sslContext: SSLContext = SSLContext.getInstance("SSL")

    init {
        sslContext.init(null, arrayOf(trustManager), java.security.SecureRandom())
    }

    private val okHttpClient: OkHttpClient
        get() {
            return OkHttpClient.Builder().apply {
                this.callTimeout(120, TimeUnit.SECONDS)
                this.connectTimeout(120, TimeUnit.SECONDS)
                this.readTimeout(120, TimeUnit.SECONDS)
                this.writeTimeout(120, TimeUnit.SECONDS)
                this.sslSocketFactory(sslContext.socketFactory, trustManager)
                this.hostnameVerifier { _, _ -> true }
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