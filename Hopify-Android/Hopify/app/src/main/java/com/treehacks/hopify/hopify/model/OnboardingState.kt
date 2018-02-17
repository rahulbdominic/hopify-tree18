package com.treehacks.hopify.hopify.model

/**
 * Created by rahul on 17/02/2018.
 */

data class OnboardingState(
        val interests: List<Interest> = arrayListOf(),
        val hours: Int? = null,
        val lat: Double? = null,
        val lng: Double? = null,
        val radius: Int? = null,
        val maxPrice: Int? = null
        // Add other information here
) {
    fun withParams(
            interests: List<Interest> = this.interests,
            hours: Int? = this.hours,
            lat: Double? = this.lat,
            lng: Double? = this.lng,
            radius: Int? = this.radius,
            maxPrice: Int? = this.maxPrice
    ): OnboardingState = OnboardingState(interests, hours, lat, lng, radius, maxPrice)
}