package com.treehacks.hopify.hopify.model

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable

class RouterViewModel {
    val refreshRelay: Relay<Unit> = PublishRelay.create<Unit>()
    val loadingRelay: Relay<Unit> = PublishRelay.create<Unit>()
    val interestContinueClicked: Relay<List<Interest>> = PublishRelay.create<List<Interest>>()
    val questionnaireContinueClicked: Relay<Unit> = PublishRelay.create<Unit>()
    val mainMapRelay: Relay<Unit> = PublishRelay.create<Unit>()
    val shareRelay: Relay<Unit> = PublishRelay.create<Unit>()

    val screenStream: Observable<Screens>
        get() = Observable.mergeArray(
                refreshRelay.map<Screens> {
                    // Make initial call
                    // Default to onboarding for now
                    Screens.ONBOARDING_INTEREST_SELECTION
                },
                interestContinueClicked.map { Screens.ONBOARDING_QUESTIONNAIRE },
                questionnaireContinueClicked.map { Screens.MAIN_MAP }
        ).startWith(Screens.ONBOARDING_INTEREST_SELECTION)
}

class OnboardingState(
   val interests: List<Interest>
   // Add other information here
) {
    fun withInterests(interests: List<Interest>) : OnboardingState =
        OnboardingState(interests)
}

enum class Screens {
    ONBOARDING_INTEREST_SELECTION,
    ONBOARDING_QUESTIONNAIRE,
    LOADING,
    MAIN_MAP,
    SHARE,
}