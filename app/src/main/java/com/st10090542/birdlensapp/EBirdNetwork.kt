package com.st10090542.birdlensapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object EBirdNetwork {
    private const val BASE_URL = "https://api.ebird.org/v2/ref/hotspot/geo?lat={{lat}}&lng={{lng}}"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val eBirdService: EBirdService by lazy {
        retrofit.create(EBirdService::class.java)
    }
}



