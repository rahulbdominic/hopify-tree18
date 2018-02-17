package com.treehacks.hopify.hopify.server

import com.treehacks.hopify.hopify.model.OnboardingState
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET

/**
 * Created by rahul on 17/02/2018.
 */

interface HopifyApi {
    @GET("hopify@dev")
    fun postData(@Body body: OnboardingState): Observable<HopifyOnboardingResponse>
}
