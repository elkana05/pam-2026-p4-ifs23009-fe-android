package org.delcom.pam_p4_ifs23009.network.fish.service

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.delcom.pam_p4_ifs23009.BuildConfig
import java.util.concurrent.TimeUnit

class FishAppContainer : IFishAppContainer {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            addInterceptor(loggingInterceptor)
        }
        connectTimeout(2, TimeUnit.MINUTES)
        readTimeout(2, TimeUnit.MINUTES)
        writeTimeout(2, TimeUnit.MINUTES)
    }.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL_PANTS_API)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    private val retrofitService: FishApiService by lazy {
        retrofit.create(FishApiService::class.java)
    }

    override val fishRepository: IFishRepository by lazy {
        FishRepository(retrofitService)
    }
}
