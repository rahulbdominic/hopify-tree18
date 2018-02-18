package com.treehacks.hopify.hopify.server

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class HopifyOnboardingResponse : Serializable {
    @SerializedName("uuid")
    @Expose
    lateinit var uuid: String

    @SerializedName("data")
    @Expose
    lateinit var data: List<Datum>
}
