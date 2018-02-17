package com.treehacks.hopify.hopify.model

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable

class RouterViewModel {
    val refreshRelay: Relay<Unit> = PublishRelay.create<Unit>()
    val submitDataRelay: Relay<Unit> = PublishRelay.create<Unit>()
    val loadingRelay: Relay<Unit> = PublishRelay.create<Unit>()
    val interestContinueClicked: Relay<List<Interest>> = PublishRelay.create<List<Interest>>()
    val questionnaireContinueClicked: Relay<QuestionnaireViewModel> = PublishRelay.create<QuestionnaireViewModel>()
    val shareLaunchRelay: Relay<Unit> = PublishRelay.create<Unit>()
    val shareRelay: Relay<Unit> = PublishRelay.create<Unit>()

    val screenStream: Observable<Screens>
        get() = Observable.mergeArray(
                refreshRelay.map<Screens> {
                    // Make initial call
                    // Default to onboarding for now
                    Screens.ONBOARDING_INTEREST_SELECTION
                },
                submitDataRelay.map {
                    // Temp
                    Screens.LOADING
                },
                loadingRelay.map { Screens.MAIN_MAP },
                interestContinueClicked.map { Screens.ONBOARDING_QUESTIONNAIRE },
                questionnaireContinueClicked.map {
                    submitDataRelay.accept(Unit)
                    Screens.LOADING
                },
                shareLaunchRelay.map { Screens.SHARE },
                shareRelay.map { Screens.MAIN_MAP }
        ).startWith(Screens.ONBOARDING_INTEREST_SELECTION)
}

class OnboardingState(
   val interests: List<Interest> = arrayListOf(),
   val lat: Double? = null,
   val lng: Double? = null,
   val radius: Int? = null,
   val maxPrice: Int? = null
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

enum class Screens {
    ONBOARDING_INTEREST_SELECTION,
    ONBOARDING_QUESTIONNAIRE,
    LOADING,
    MAIN_MAP,
    SHARE,
}
