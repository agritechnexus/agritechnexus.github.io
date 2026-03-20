package com.fixmybill.app.di

import com.fixmybill.app.BuildConfig
import com.fixmybill.app.data.remote.api.AiAnalysisService
import com.fixmybill.app.data.remote.api.TariffApiService
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val TARIFF_BASE_URL = "https://api.fixmybill.com/"
    private const val ANTHROPIC_BASE_URL = "https://api.anthropic.com/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Provides
    @Singleton
    @Named("anthropicInterceptor")
    fun provideAnthropicHeadersInterceptor(): Interceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("x-api-key", BuildConfig.ANTHROPIC_API_KEY)
            .addHeader("anthropic-version", "2023-06-01")
            .addHeader("content-type", "application/json")
            .build()
        chain.proceed(request)
    }

    @Provides
    @Singleton
    @Named("tariffClient")
    fun provideTariffOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @Named("anthropicClient")
    fun provideAnthropicOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @Named("anthropicInterceptor") anthropicInterceptor: Interceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(anthropicInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    @Named("tariffRetrofit")
    fun provideTariffRetrofit(
        @Named("tariffClient") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit = Retrofit.Builder()
        .baseUrl(TARIFF_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    @Named("anthropicRetrofit")
    fun provideAnthropicRetrofit(
        @Named("anthropicClient") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit = Retrofit.Builder()
        .baseUrl(ANTHROPIC_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun provideTariffApiService(
        @Named("tariffRetrofit") retrofit: Retrofit
    ): TariffApiService = retrofit.create(TariffApiService::class.java)

    @Provides
    @Singleton
    fun provideAiAnalysisService(
        @Named("anthropicRetrofit") retrofit: Retrofit
    ): AiAnalysisService = retrofit.create(AiAnalysisService::class.java)
}
