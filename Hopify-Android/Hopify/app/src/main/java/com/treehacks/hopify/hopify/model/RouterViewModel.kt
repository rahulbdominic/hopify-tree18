package com.treehacks.hopify.hopify.model

import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.treehacks.hopify.hopify.server.HopifyApiManager
import io.reactivex.Observable

class RouterViewModel {
    private val manager = HopifyApiManager()

    private val refreshRelay: Relay<Unit> = PublishRelay.create<Unit>()
    private val submitDataRelay: Relay<Unit> = PublishRelay.create<Unit>()
    private val loadingRelay: Relay<Unit> = PublishRelay.create<Unit>()
    val interestContinueClicked: Relay<List<Interest>> = PublishRelay.create<List<Interest>>()
    val questionnaireContinueClicked: Relay<QuestionnaireViewModel> = PublishRelay.create<QuestionnaireViewModel>()

    private var state = OnboardingState()

    private val stateStream: Observable<OnboardingState>
        get() = Observable.mergeArray(
                interestContinueClicked.map {
                    state.withParams(interests = it)
                },
                questionnaireContinueClicked.map {
                    state.withParams(
                            hours = it.hours,
                            lat = it.lat,
                            lng = it.lng,
                            radius = it.radius,
                            maxPrice = it.maxPrice
                    )
                }
        ).startWith(OnboardingState())

    val screenStream: Observable<Screens>
        get() = Observable.mergeArray(
                refreshRelay.map<Screens> {
                    // Make initial call
                    // Default to onboarding for now
                    Screens.ONBOARDING_INTEREST_SELECTION
                },
                submitDataRelay.flatMap {
                    manager.postData(state).map {
                        Screens.MAIN_MAP
                    }
                },
                loadingRelay.map { Screens.MAIN_MAP },
                interestContinueClicked.map { Screens.ONBOARDING_QUESTIONNAIRE },
                questionnaireContinueClicked.map {
                    submitDataRelay.accept(Unit)
                    Screens.LOADING
                }
        ).startWith(Screens.ONBOARDING_INTEREST_SELECTION) // TODO(Rahul): Start with refresh later

    init {
        stateStream.subscribe {
            state = it
        }
    }
}

enum class Screens {
    ONBOARDING_INTEREST_SELECTION,
    ONBOARDING_QUESTIONNAIRE,
    LOADING,
    MAIN_MAP,
}
