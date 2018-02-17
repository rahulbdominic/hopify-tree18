package com.treehacks.hopify.hopify.server

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class OpeningHours : Serializable {

    @SerializedName("open_now")
    @Expose
    var openNow: Boolean? = null
    @SerializedName("weekday_text")
    @Expose
    var weekdayText: List<Any>? = null

}
