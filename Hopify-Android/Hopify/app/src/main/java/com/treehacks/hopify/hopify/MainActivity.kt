package com.treehacks.hopify.hopify

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Places

import com.treehacks.hopify.hopify.model.RouterViewModel
import com.treehacks.hopify.hopify.model.Screens
import com.treehacks.hopify.hopify.model.Screens.*
import com.treehacks.hopify.hopify.view.LoadingFragment
import com.treehacks.hopify.hopify.view.OnboardingInterestSelectionFragment
import com.treehacks.hopify.hopify.view.OnboardingQuestionnaireFragment
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity(), Observer<Screens> {

    private var mGeoDataClient: GeoDataClient? = null
    var viewModel = RouterViewModel()
    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mGeoDataClient = Places.getGeoDataClient(this, null);
    }

    override fun onStart() {
        super.onStart()
        viewModel.screenStream
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this)
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
    }

    override fun onError(e: Throwable) {
        Log.e(LOG_TAG, "Stream error $e", e)
    }

    override fun onSubscribe(d: Disposable) {
        this.disposable?.dispose()
        this.disposable = d
    }

    override fun onComplete() {
    }

    override fun onNext(t: Screens) {
        val fragment: Fragment? = when (t) {
            ONBOARDING_INTEREST_SELECTION -> OnboardingInterestSelectionFragment.newInstance(viewModel.interestContinueClicked)
            ONBOARDING_QUESTIONNAIRE -> OnboardingQuestionnaireFragment.newInstance(viewModel.questionnaireContinueClicked)
            LOADING -> LoadingFragment.newInstance()
            MAIN_MAP -> TODO()
            SHARE -> TODO()
        }

        fragment?.let {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, it, t.name)
                    .commit()
        }
    }

    companion object {
        val LOG_TAG: String = MainActivity::class.java.name
    }
}
