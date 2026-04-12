package com.azhar.alquran.model.main

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelSuratDetail : Serializable {
    @SerializedName("ayat")
    var verses: ArrayList<ModelAyat>? = null
}

