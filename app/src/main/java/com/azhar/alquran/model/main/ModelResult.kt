package com.azhar.alquran.model.main

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelResult<T> : Serializable {
    @SerializedName("code")
    var code: Int = 0

    @SerializedName("message")
    var message: String? = null

    @SerializedName("data")
    var data: T? = null
}

