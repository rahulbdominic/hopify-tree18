package com.treehacks.hopify.hopify.server

import com.treehacks.hopify.hopify.apiBaseUrl
import com.treehacks.hopify.hopify.model.OnboardingState
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by rahul on 17/02/2018.
 */

class HopifyApiManager {
    private val retrofit = Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(apiBaseUrl)
            .build()
    private val hopifyApi = retrofit.create(HopifyApi::class.java)

    fun postData(data: OnboardingState): Observable<HopifyOnboardingResponse> {
        return hopifyApi.postData(data)
                .subscribeOn(Schedulers.io())
    }
}