package com.treehacks.hopify.hopify

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Places
import com.treehacks.hopify.hopify.model.MapsViewModel
import com.treehacks.hopify.hopify.model.OnboardingState
import com.treehacks.hopify.hopify.model.RouterViewModel
import com.treehacks.hopify.hopify.model.Screens
import com.treehacks.hopify.hopify.model.Screens.*
import com.treehacks.hopify.hopify.view.LoadingFragment
import com.treehacks.hopify.hopify.view.OnboardingInterestSelectionFragment
import com.treehacks.hopify.hopify.view.OnboardingQuestionnaireFragment
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.util.LinkProperties
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable


class MainActivity : AppCompatActivity(), Observer<OnboardingState> {

    private var mGeoDataClient: GeoDataClient? = null
    var viewModel = RouterViewModel()
    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mGeoDataClient = Places.getGeoDataClient(this, null)
        // Initialize the Branch object
        Branch.getAutoInstance(this)
    }

    public override fun onStart() {
        super.onStart()
        val branch = Branch.getInstance()

        branch.initSession({ branchUniversalObject, linkProperties, error ->
            if (error == null) {
                Log.i("BRANCH_MY", branchUniversalObject.toString())
                Log.i("BRANCH_MY", linkProperties.toString())
            } else {
                Log.i("BRANCH_MY", error.message)
            }
        }, this.intent.data, this)

        viewModel.stateStream
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this)
        branch.initSession { referringParams, error ->
            if (error == null) {
                Log.i("BRANCH_MY", referringParams.toString())
                val uuid = referringParams.getString("uuid")

                // knock yourself out
            } else {
                Log.i("BRANCH_MY", error.message)
            }
        }

    }

    public override fun onNewIntent(intent: Intent) {
        this.intent = intent
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

    override fun onNext(t: OnboardingState) {
        val fragment: Fragment? = when (t.currentScreen) {
            ONBOARDING_INTEREST_SELECTION -> OnboardingInterestSelectionFragment.newInstance(viewModel.interestContinueClicked)
            ONBOARDING_QUESTIONNAIRE -> OnboardingQuestionnaireFragment.newInstance(viewModel.questionnaireContinueClicked)
            LOADING -> {
                viewModel.submitDataRelay.accept(Unit)
                LoadingFragment.newInstance()
            }
            MAIN_MAP -> {
                val viewModel = MapsViewModel(t.hopifyOnboardingResponse!!)
                val intent = MapsActivity.createIntent(this, viewModel)
                startActivity(intent)
                null
            }
        }

        fragment?.let {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, it, t.currentScreen.name)
                    .commit()
        }
    }

    companion object {
        val LOG_TAG: String = MainActivity::class.java.name
    }
}
