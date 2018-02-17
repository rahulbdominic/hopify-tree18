package com.treehacks.hopify.hopify.server

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by rahul on 17/02/2018.
 */

interface HopifyApi {
    @GET("hopify@dev")
    fun postData(
            @Query("interests") interests: List<String>,
            @Query("hours") hours: Int,
            @Query("lat") lat: Double,
            @Query("lng") lng: Double,
            @Query("radius") radius: Int,
            @Query("maxPrice") maxPrice: Int
    ): Observable<List<HopifyOnboardingResponse>>
}
