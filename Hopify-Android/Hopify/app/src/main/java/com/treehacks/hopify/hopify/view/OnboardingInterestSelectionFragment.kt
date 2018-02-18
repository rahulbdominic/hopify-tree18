package com.treehacks.hopify.hopify.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxrelay2.Relay
import com.treehacks.hopify.hopify.R
import com.treehacks.hopify.hopify.defaultInterests
import com.treehacks.hopify.hopify.model.Interest
import com.treehacks.hopify.hopify.utils.RecyclerItemClickObservable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotterknife.bindView

class OnboardingInterestSelectionFragment : Fragment(), Observer<View> {

    private val recyclerView by bindView<RecyclerView>(R.id.interests_recycler_view)
    private val nextButton by bindView<Button>(R.id.interests_next_button)

    private lateinit var relay: Relay<List<Interest>>
    private val selectedInterests = mutableListOf<Interest>()

    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.interest_selection_fragment, container, false) as LinearLayout
    }

    override fun onStart() {
        super.onStart()
        setUpUiElements()
    }

    private fun setUpUiElements() {
        val layoutManager = GridLayoutManager(context, 4)
        val adapter = OnboardingInterestsRecyclerViewAdapter(defaultInterests, selectedInterests.toList())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        RecyclerItemClickObservable(recyclerView, context)
                .subscribe(this)

        nextButton.clicks()
                .subscribe { relay.accept(selectedInterests.toList()) }
    }

    override fun onComplete() {

    }

    override fun onSubscribe(d: Disposable) {
        disposable?.dispose()
        disposable = d
    }

    override fun onStop() {
        super.onStop()
        disposable?.dispose()
    }

    override fun onNext(t: View) {
        val item = Interest(t.tag as String)
        if (item in selectedInterests) {
            selectedInterests.remove(item)
        } else {
            selectedInterests.add(item)
        }

        val layoutManager = GridLayoutManager(context, 4)
        val adapter = OnboardingInterestsRecyclerViewAdapter(defaultInterests, selectedInterests.toList())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    override fun onError(e: Throwable) {
        Log.e(LOG_TAG, "Stream error: ${e.message}")
    }

    companion object {
        private val LOG_TAG: String = OnboardingInterestSelectionFragment::class.java.name

        fun newInstance(relay: Relay<List<Interest>>): OnboardingInterestSelectionFragment {
            val fragment = OnboardingInterestSelectionFragment()
            fragment.relay = relay
            return fragment
        }
    }
}
