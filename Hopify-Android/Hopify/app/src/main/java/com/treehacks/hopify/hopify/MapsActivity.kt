package com.treehacks.hopify.hopify

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import com.treehacks.hopify.hopify.model.MapsViewModel
import com.google.android.gms.maps.model.PolylineOptions



class MapsActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var viewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        viewModel = intent.extras.get(MAPS_VIEW_MODEL) as MapsViewModel

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney, Australia, and move the camera.
        viewModel.data.forEach {
            Log.d(LOG_TAG, it.toString())
            val point = LatLng(
                    it.geometry?.location?.lat ?: 0.toDouble(),
                    it.geometry?.location?.lng ?: 0.toDouble()
            )
            mMap.addMarker(MarkerOptions().position(point).title(it.name ?: ""))
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(
                viewModel.data[0].geometry?.location?.lat ?: 0.toDouble(),
                viewModel.data[0].geometry?.location?.lng ?: 0.toDouble()
        )))
        (0..(viewModel.data.size - 2)).forEach {
            val pt1 = viewModel.data[it].geometry!!.location!!
            val pt2 = viewModel.data[it + 1].geometry!!.location!!
            mMap.addPolyline(PolylineOptions()
                    .add(LatLng(pt1.lat!!, pt1.lng!!), LatLng(pt2.lat!!, pt2.lng!!))
                    .width(5f)
                    .color(Color.RED))
        }
    }

    companion object {
        val LOG_TAG: String = MapsActivity::class.java.name
        private const val MAPS_VIEW_MODEL = "MAPS_VIEW_MODEL_ID"

        fun createIntent(context: Context, viewModel: MapsViewModel): Intent {
            val intent = Intent(context, MapsActivity::class.java)
            intent.putExtra(MAPS_VIEW_MODEL, viewModel)

            return intent
        }
    }
}
