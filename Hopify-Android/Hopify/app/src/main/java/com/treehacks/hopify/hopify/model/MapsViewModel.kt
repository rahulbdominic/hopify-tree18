package com.treehacks.hopify.hopify.model

import com.treehacks.hopify.hopify.server.HopifyOnboardingResponse
import java.io.Serializable

/**
 * Created by rahul on 17/02/2018.
 */

class MapsViewModel(private val responseData: HopifyOnboardingResponse) : Serializable {
    val data by lazy { responseData.data }
}
