package com.treehacks.hopify.hopify.model

/**
 * Created by rahul on 17/02/2018.
 */
class QuestionnaireViewModel(
        val lat: Double? = null,
        val lng: Double? = null,
        val radius: Int? = null,
        val maxPrice: Int? = null
) {
    val nextButtonEnabled = (lat != null && lng != null && radius != null && maxPrice != null)
    fun withParams(
            lat: Double? = this.lat,
            lng: Double? = this.lng,
            radius: Int? = this.radius,
            maxPrice: Int? = this.maxPrice
    ): QuestionnaireViewModel = QuestionnaireViewModel(lat, lng, radius, maxPrice)
}