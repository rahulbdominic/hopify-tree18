package com.treehacks.hopify.hopify.model

import android.util.Log
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.treehacks.hopify.hopify.server.HopifyApiManager
import com.treehacks.hopify.hopify.server.HopifyOnboardingResponse
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy

class RouterViewModel {
    private val manager = HopifyApiManager()

    val onboardingRelay: Relay<Unit> = PublishRelay.create<Unit>()
    val deepLinkRelay: Relay<String> = PublishRelay.create<String>()
    val submitDataRelay: Relay<Unit> = PublishRelay.create<Unit>()
    val interestContinueClicked: Relay<List<Interest>> = PublishRelay.create<List<Interest>>()
    val questionnaireContinueClicked: Relay<QuestionnaireViewModel> = PublishRelay.create<QuestionnaireViewModel>()

    private var state = OnboardingState(currentScreen = Screens.ONBOARDING_INTEREST_SELECTION)

    val stateStream: Observable<OnboardingState>
        get() = Observable.mergeArray(
                deepLinkRelay.flatMap {
                    manager.fetchData(it).map {
                        state.withParams(
                                currentScreen = Screens.MAIN_MAP,
                                hopifyOnboardingResponse = it
                        )
                    }
                },
                onboardingRelay.map {
                    state.withParams(currentScreen = Screens.ONBOARDING_INTEREST_SELECTION)
                },
                submitDataRelay.flatMap {
                    manager.postData(state).map {
                        state.withParams(
                                currentScreen = Screens.MAIN_MAP,
                                hopifyOnboardingResponse = it
                        )
                    }
                },
                interestContinueClicked.map {
                    state.withParams(
                            currentScreen = Screens.ONBOARDING_QUESTIONNAIRE,
                            interests = it
                    )
                },
                questionnaireContinueClicked.map {
                    state.withParams(
                            currentScreen = Screens.LOADING,
                            hours = it.hours,
                            lat = it.lat,
                            lng = it.lng,
                            radius = it.radius,
                            maxPrice = it.maxPrice
                    )
                }.startWith(OnboardingState(currentScreen = Screens.REFRESH))
        )

    init {
        stateStream.subscribeBy(
                onError = {
                    Log.e(RouterViewModel::class.java.name, it.message)
                    Unit
                },
                onComplete = {},
                onNext = {
                    state = it
                }
        )
    }

    fun getMapsActivityViewModel(): MapsViewModel {
        print(state.hopifyOnboardingResponse.toString())
        return MapsViewModel(state.hopifyOnboardingResponse ?: HopifyOnboardingResponse())
    }
}

enum class Screens {
    ONBOARDING_INTEREST_SELECTION,
    ONBOARDING_QUESTIONNAIRE,
    LOADING,
    REFRESH,
    MAIN_MAP,
}
