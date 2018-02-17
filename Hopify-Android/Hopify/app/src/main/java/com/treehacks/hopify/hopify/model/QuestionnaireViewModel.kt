package com.treehacks.hopify.hopify.model

/**
 * Created by rahul on 17/02/2018.
 */
data class QuestionnaireViewModel(
        val hours: Int? = null,
        val lat: Double? = null,
        val lng: Double? = null,
        val radius: Int? = null,
        val maxPrice: Int? = null
) {
    val nextButtonEnabled = (hours != null && lat != null && lng != null && radius != null && maxPrice != null)
    fun withParams(
            hours: Int? = this.hours,
            lat: Double? = this.lat,
            lng: Double? = this.lng,
            radius: Int? = this.radius,
            maxPrice: Int? = this.maxPrice
    ): QuestionnaireViewModel = QuestionnaireViewModel(hours, lat, lng, radius, maxPrice)
}
