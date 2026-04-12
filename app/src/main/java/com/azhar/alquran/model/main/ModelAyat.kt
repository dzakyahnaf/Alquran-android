package com.azhar.alquran.model.main

import com.google.gson.annotations.SerializedName

/**
 * Created by Azhar Rivaldi on 10-11-2021
 * Youtube Channel : https://bit.ly/2PJMowZ
 * Github : https://github.com/AzharRivaldi
 * Twitter : https://twitter.com/azharrvldi_
 * Instagram : https://www.instagram.com/azhardvls_
 * LinkedIn : https://www.linkedin.com/in/azhar-rivaldi
 */

class ModelAyat {
    @SerializedName("teksArab")
    var arab: String? = null

    @SerializedName("teksIndonesia")
    var indo: String? = null

    @SerializedName("nomorAyat")
    var nomor: Int = 0
}