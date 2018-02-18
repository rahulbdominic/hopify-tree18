package com.treehacks.hopify.hopify.model

import android.util.Log
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import com.treehacks.hopify.hopify.server.HopifyApiManager
import com.treehacks.hopify.hopify.server.HopifyOnboardingResponse
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import java.io.Serializable

/**
 * Created by rahul on 17/02/2018.
 */

class MapsViewModel(private val responseData: HopifyOnboardingResponse) : Serializable {
    val id by lazy { responseData.uuid }
    val data by lazy { responseData.data }
    val raw by lazy { responseData }

    val listClickRelay: Relay<Unit> = PublishRelay.create<Unit>()
    val mapsClickRelay: Relay<Unit> = PublishRelay.create<Unit>()

    val screenStream: Observable<MapsScreens>
        get() = Observable.mergeArray(
                listClickRelay.map { MapsScreens.LIST },
                mapsClickRelay.map { MapsScreens.MAP }
        ).startWith(MapsScreens.MAP)
}

enum class MapsScreens {
    MAP,
    LIST,
}
