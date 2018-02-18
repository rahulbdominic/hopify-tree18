package com.treehacks.hopify.hopify.model

import com.treehacks.hopify.hopify.server.HopifyOnboardingResponse

/**
 * Created by rahul on 17/02/2018.
 */

data class OnboardingState(
        val currentScreen: Screens = Screens.ONBOARDING_INTEREST_SELECTION,
        val interests: List<Interest> = arrayListOf(),
        val hours: Int? = null,
        val lat: Double? = null,
        val lng: Double? = null,
        val radius: Int? = null,
        val maxPrice: Int? = null,
        val hopifyOnboardingResponse: HopifyOnboardingResponse? = null
        // Add other information here
) {
    fun withParams(
            currentScreen: Screens = this.currentScreen,
            interests: List<Interest> = this.interests,
            hours: Int? = this.hours,
            lat: Double? = this.lat,
            lng: Double? = this.lng,
            radius: Int? = this.radius,
            maxPrice: Int? = this.maxPrice,
            hopifyOnboardingResponse: HopifyOnboardingResponse? = this.hopifyOnboardingResponse
    ): OnboardingState = OnboardingState(
            currentScreen,
            interests,
            hours,
            lat,
            lng,
            radius,
            maxPrice,
            hopifyOnboardingResponse
    )
}