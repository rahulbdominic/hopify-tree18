package com.treehacks.hopify.hopify.server

import com.treehacks.hopify.hopify.apiBaseUrl
import com.treehacks.hopify.hopify.model.OnboardingState
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
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
        return hopifyApi.postData(
                data.interests.map { it.value },
                data.hours!!,
                data.lat!!,
                data.lng!!,
                data.radius!!,
                data.maxPrice!!
        )
                .subscribeOn(Schedulers.io())
    }

    fun fetchData(uuid: String): Observable<HopifyOnboardingResponse> {
        return hopifyApi.fetchByUuid(uuid).subscribeOn(Schedulers.io())
    }

    fun sendMessage(url: String, phone: String): Observable<ResponseBody> {
        return hopifyApi.postMessage(url, phone).subscribeOn(Schedulers.io())
    }
}