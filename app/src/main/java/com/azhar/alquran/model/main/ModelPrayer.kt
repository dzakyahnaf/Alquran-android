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
    var tanggal: String? = null
}

class ModelPrayerResult : Serializable {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("lokasi")
    var lokasi: String? = null

    @SerializedName("daerah")
    var daerah: String? = null

    @SerializedName("jadwal")
    var jadwal: ArrayList<ModelPrayer>? = null
}
