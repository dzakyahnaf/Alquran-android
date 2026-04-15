package com.azhar.alquran.networking

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import com.azhar.alquran.model.main.ModelPrayerResult
import com.azhar.alquran.model.main.ModelResult
import com.azhar.alquran.model.main.ModelSurah
import com.azhar.alquran.model.main.ModelSuratDetail
import com.azhar.alquran.model.main.ShalatRequest
import com.azhar.alquran.model.nearby.ModelResultOSM
import com.azhar.alquran.model.response.ModelResultNearby
import retrofit2.Call
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.ArrayList

/**
 * Created by Azhar Rivaldi on 18-05-2021
 * Youtube Channel : https://bit.ly/2PJMowZ
 * Github : https://github.com/AzharRivaldi
 * Twitter : https://twitter.com/azharrvldi_
 * Instagram : https://www.instagram.com/azhardvls_
 * Linkedin : https://www.linkedin.com/in/azhar-rivaldi
 */

interface ApiInterface {
    @GET("surat")
    fun getListSurah(): Call<ModelResult<ArrayList<ModelSurah>>>

    @GET("surat/{nomor}")
    fun getDetailSurah(
        @Path("nomor") nomor: String
    ): Call<ModelResult<ModelSuratDetail>>

    @POST("shalat")
    fun getJadwalSholat(
        @Body request: ShalatRequest
    ): Call<ModelResult<ModelPrayerResult>>

    @GET("place/nearbysearch/json")
    fun getDataResult(
        @Query("key") key: String,
        @Query("keyword") keyword: String,
        @Query("location") location: String,
        @Query("rankby") rankby: String
    ): Call<ModelResultNearby>

    @GET("interpreter")
    fun getMasjidOSM(
        @Query("data") data: String
    ): Call<ModelResultOSM>
}