package com.treehacks.hopify.hopify.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.gms.maps.SupportMapFragment
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxrelay2.Relay
import com.treehacks.hopify.hopify.MapsActivity
import com.treehacks.hopify.hopify.R
import com.treehacks.hopify.hopify.model.MapsViewModel
import com.treehacks.hopify.hopify.server.Datum
import com.treehacks.hopify.hopify.server.HopifyOnboardingResponse
import io.branch.referral.Branch
import kotterknife.bindView

/**
 * Created by rahul on 18/02/2018.
 */

class MapsListActivity : AppCompatActivity() {

    private val recyclerView by bindView<RecyclerView>(R.id.recycler_view)
    private val mapViewButton by bindView<Button>(R.id.map_view_button)

    private lateinit var data: HopifyOnboardingResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps_list)

        data = intent.extras.get(MAPS_DATA) as HopifyOnboardingResponse
    }

    override fun onStart() {
        super.onStart()
        setUpUiElements()
    }

    private fun setUpUiElements() {
        val layoutManager = LinearLayoutManager(this)
        val adapter = MapListViewAdapter(data.data)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        mapViewButton.clicks()
                .subscribe {
                    val intent = MapsActivity.createIntent(this, data)
                    startActivity(intent)
                }
    }

    companion object {
        private val LOG_TAG: String = MapsListActivity::class.java.name
        private const val MAPS_DATA = "MAP_DATA_URL"

        fun createIntent(context: Context, data: HopifyOnboardingResponse): Intent {
            val intent = Intent(context, MapsListActivity::class.java)
            intent.putExtra(MAPS_DATA, data)

            return intent
        }
    }
}
