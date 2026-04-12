package com.azhar.alquran.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiOSM {
    companion object {
        private const val BASE_URL = "https://overpass-api.de/api/"
        fun getOSM(): ApiInterface {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiInterface::class.java)
        }
    }
}
