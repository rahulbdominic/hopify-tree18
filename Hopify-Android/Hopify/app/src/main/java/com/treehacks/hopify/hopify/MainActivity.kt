package com.treehacks.hopify.hopify

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log

import com.treehacks.hopify.hopify.model.RouterViewModel
import com.treehacks.hopify.hopify.model.Screens
import com.treehacks.hopify.hopify.model.Screens.*
import com.treehacks.hopify.hopify.view.InterestSelectionFragment
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity(), Observer<Screens> {

    var viewModel = RouterViewModel()
    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        viewModel.screenStream
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this)
    }

    override fun onPause() {
        super.onPause()
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
            ONBOARDING_INTEREST_SELECTION -> InterestSelectionFragment.newInstance(viewModel.interestContinueClicked)
            ONBOARDING_QUESTIONNAIRE -> TODO()
            LOADING -> TODO()
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
