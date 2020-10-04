package com.seiko.danmaku.di

import com.seiko.danmaku.BuildConfig
import com.seiko.danmaku.data.api.DanDanApi
import com.seiko.danmaku.data.api.DownloadApi
import com.seiko.danmaku.data.api.GzipInterceptor
import com.seiko.danmaku.util.DANDAN_API_BASE_URL
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @DanDanClientQualifier
    fun provideDanDanClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(GzipInterceptor())
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }
        return builder.build()
    }

    @Provides
    @Singleton
    @DanDanRetrofitQualifier
    fun provideDanDanRetrofit(moshi: Moshi, @DanDanClientQualifier client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .callFactory(client)
            .baseUrl(DANDAN_API_BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideDanDanApiService(@DanDanRetrofitQualifier retrofit: Retrofit): DanDanApi {
        return retrofit.create()
    }


    @Provides
    @Singleton
    @DownloadClientQualifier
    fun provideDownloadClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @DownloadRetrofitQualifier
    fun provideDownloadRetrofit(moshi: Moshi, @DownloadClientQualifier client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .callFactory(client)
            .baseUrl("http://www.example.com")
            .build()
    }

    @Provides
    @Singleton
    fun provideDownloadApiService(@DownloadRetrofitQualifier retrofit: Retrofit): DownloadApi {
        return retrofit.create()
    }

}