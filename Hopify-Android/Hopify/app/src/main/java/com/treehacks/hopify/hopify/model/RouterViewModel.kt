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

    val deepLinkRelay: Relay<String> = PublishRelay.create<String>()
    val submitDataRelay: Relay<Unit> = PublishRelay.create<Unit>()
    val loadingRelay: Relay<Unit> = PublishRelay.create<Unit>()
    val interestContinueClicked: Relay<List<Interest>> = PublishRelay.create<List<Interest>>()
    val questionnaireContinueClicked: Relay<QuestionnaireViewModel> = PublishRelay.create<QuestionnaireViewModel>()

    private var state = OnboardingState()

    private val stateStream: Observable<OnboardingState>
        get() = Observable.mergeArray(
                deepLinkRelay.flatMap {
                    manager.fetchData(it).map {
                        state.withParams(
                                currentScreen = Screens.MAIN_MAP,
                                hopifyOnboardingResponse = it
                        )
                    }
                },
                submitDataRelay.flatMap {
                    manager.postData(state).map {
                        state.withParams(
                                currentScreen = Screens.MAIN_MAP,
                                hopifyOnboardingResponse = it
                        )
                    }
                },
                loadingRelay.map { state.withParams(currentScreen = Screens.LOADING) },
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
                }
        ).startWith(OnboardingState())

    val screenStream: Observable<Screens>
        get() = stateStream.map { it.currentScreen }
                .startWith(Screens.ONBOARDING_INTEREST_SELECTION) // TODO(Rahul): Start with refresh later

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
        return MapsViewModel(state.hopifyOnboardingResponse ?: HopifyOnboardingResponse())
    }
}

enum class Screens {
    ONBOARDING_INTEREST_SELECTION,
    ONBOARDING_QUESTIONNAIRE,
    LOADING,
    MAIN_MAP,
}
