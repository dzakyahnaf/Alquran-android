package com.azhar.alquran.model.main

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelPrayer : Serializable {
    @SerializedName("imsak")
    var imsak: String? = null

    @SerializedName("subuh")
    var subuh: String? = null

    @SerializedName("terbit")
    var terbit: String? = null

    @SerializedName("dhuha")
    var dhuha: String? = null

    @SerializedName("dzuhur")
    var dzuhur: String? = null

    @SerializedName("ashar")
    var ashar: String? = null

    @SerializedName("maghrib")
    var maghrib: String? = null

    @SerializedName("isya")
    var isya: String? = null

    @SerializedName("tanggal")
    var tanggal: Int = 0

    @SerializedName("tanggal_lengkap")
    var tanggalLengkap: String? = null

    @SerializedName("hari")
    var hari: String? = null
}

class ModelPrayerResult : Serializable {
    @SerializedName("provinsi")
    var provinsi: String? = null

    @SerializedName("kabkota")
    var kabkota: String? = null

    @SerializedName("bulan")
    var bulan: Int = 0

    @SerializedName("tahun")
    var tahun: Int = 0

    @SerializedName("bulan_nama")
    var bulanNama: String? = null

    @SerializedName("jadwal")
    var jadwal: ArrayList<ModelPrayer>? = null
}

data class ShalatRequest(
    @SerializedName("provinsi")
    val provinsi: String,
    @SerializedName("kabkota")
    val kabkota: String,
    @SerializedName("bulan")
    val bulan: Int,
    @SerializedName("tahun")
    val tahun: Int
)
