package com.azhar.alquran.model.main

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Azhar Rivaldi on 10-11-2021
 * Youtube Channel : https://bit.ly/2PJMowZ
 * Github : https://github.com/AzharRivaldi
 * Twitter : https://twitter.com/azharrvldi_
 * Instagram : https://www.instagram.com/azhardvls_
 * LinkedIn : https://www.linkedin.com/in/azhar-rivaldi
 */

class ModelSurah : Serializable {
    @SerializedName("nomor")
    var nomor: Int = 0

    @SerializedName("nama")
    var asma: String? = null

    @SerializedName("namaLatin")
    var nama: String? = null

    @SerializedName("jumlahAyat")
    var ayat: Int = 0

    @SerializedName("tempatTurun")
    var type: String? = null

    @SerializedName("arti")
    var arti: String? = null

    @SerializedName("deskripsi")
    var keterangan: String? = null

    @SerializedName("audioFull")
    var audioFull: Map<String, String>? = null

    // Computed property for audio compatibility
    val audio: String
        get() = audioFull?.get("01") ?: ""
}
