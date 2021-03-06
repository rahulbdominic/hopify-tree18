package com.treehacks.hopify.hopify.server

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class HopifyOnboardingResponse : Serializable {
    @SerializedName("uuid")
    @Expose
    var uuid: String = ""

    @SerializedName("data")
    @Expose
    var data: List<Datum> = listOf()
}
