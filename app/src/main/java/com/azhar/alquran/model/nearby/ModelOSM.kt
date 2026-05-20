package com.azhar.alquran.model.nearby

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelResultOSM : Serializable {
    @SerializedName("elements")
    var elements: List<ModelOSM>? = null
}

class ModelOSM : Serializable {
    @SerializedName("lat")
    var lat: Double = 0.0

    @SerializedName("lon")
    var lon: Double = 0.0

    @SerializedName("tags")
    var tags: ModelOSMTags? = null

    // For way/relation elements, coordinates are in a nested "center" object
    @SerializedName("center")
    var center: ModelOSMCenter? = null
}

class ModelOSMCenter : Serializable {
    @SerializedName("lat")
    var lat: Double = 0.0

    @SerializedName("lon")
    var lon: Double = 0.0
}

class ModelOSMTags : Serializable {
    @SerializedName("name")
    var name: String? = null
}

