package com.treehacks.hopify.hopify.server

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by rahul on 17/02/2018.
 */

interface HopifyApi {
    @GET("hopify")
    fun postData(
            @Query("interests") interests: List<String>,
            @Query("hours") hours: Int,
            @Query("lat") lat: Double,
            @Query("lng") lng: Double,
            @Query("radius") radius: Int,
            @Query("maxPrice") maxPrice: Int
    ): Observable<HopifyOnboardingResponse>

    @GET("hopify/get")
    fun fetchByUuid(@Query("uuid") uuid: String): Observable<HopifyOnboardingResponse>

    @GET("hopify/message")
    fun postMessage(@Query("url") url: String, @Query("number") phone: String) : Observable<ResponseBody>
}
