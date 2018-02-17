package com.treehacks.hopify.hopify.model

/**
 * Created by rahul on 17/02/2018.
 */

class OnboardingState(
        private val interests: List<Interest> = arrayListOf(),
        private val lat: Double? = null,
        private val lng: Double? = null,
        private val radius: Int? = null,
        private val maxPrice: Int? = null
        // Add other information here
) {
    fun withParams(
            interests: List<Interest> = this.interests,
            lat: Double? = this.lat,
            lng: Double? = this.lng,
            radius: Int? = this.radius,
            maxPrice: Int? = this.maxPrice
    ): OnboardingState = OnboardingState(interests, lat, lng, radius, maxPrice)
}