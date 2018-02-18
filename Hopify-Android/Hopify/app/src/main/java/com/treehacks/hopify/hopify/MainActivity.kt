package com.treehacks.hopify.hopify

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.Places
import com.treehacks.hopify.hopify.model.RouterViewModel
import com.treehacks.hopify.hopify.model.Screens
import com.treehacks.hopify.hopify.model.Screens.*
import com.treehacks.hopify.hopify.view.LoadingFragment
import com.treehacks.hopify.hopify.view.OnboardingInterestSelectionFragment
import com.treehacks.hopify.hopify.view.OnboardingQuestionnaireFragment
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.util.LinkProperties


class MainActivity : AppCompatActivity(), Observer<Screens> {

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

        branch.initSession(object : Branch.BranchUniversalReferralInitListener {
            override fun onInitFinished(branchUniversalObject: BranchUniversalObject, linkProperties: LinkProperties, error: BranchError?) {
                if (error == null) {
                    Log.i("BRANCH_MY", branchUniversalObject.toString())
                    Log.i("BRANCH_MY", linkProperties.toString())
                } else {
                    Log.i("BRANCH_MY", error.message)
                }
            }
        }, this.intent.data, this)

        branch.initSession { referringParams, error ->
            if (error == null) {
                Log.i("BRANCH_MY", referringParams.toString())
                val uuid = referringParams.getString("uuid")

                // knock yourself out
            } else {
                Log.i("BRANCH_MY", error.message)
            }
        }

        viewModel.screenStream
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this)
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

    override fun onNext(t: Screens) {
        val fragment: Fragment? = when (t) {
            ONBOARDING_INTEREST_SELECTION -> OnboardingInterestSelectionFragment.newInstance(viewModel.interestContinueClicked)
            ONBOARDING_QUESTIONNAIRE -> OnboardingQuestionnaireFragment.newInstance(viewModel.questionnaireContinueClicked)
            LOADING -> {
                viewModel.submitDataRelay.accept(Unit)
                LoadingFragment.newInstance()
            }
            MAIN_MAP -> {
                val viewModel = viewModel.getMapsActivityViewModel()
                val intent = MapsActivity.createIntent(this, viewModel)
                startActivity(intent)
                null
            }
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
