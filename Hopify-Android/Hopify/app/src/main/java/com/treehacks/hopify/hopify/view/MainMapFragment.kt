package com.treehacks.hopify.hopify.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.treehacks.hopify.hopify.R

/**
 * Created by rahul on 17/02/2018.
 */

class MainMapFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_map_fragment, container, false) as LinearLayout
    }

    override fun onStart() {
        super.onStart()
        setUpUiElements()
    }

    private fun setUpUiElements() {
    }

    companion object {
        private val LOG_TAG: String = MainMapFragment::class.java.name

        fun newInstance(): MainMapFragment {
            return MainMapFragment()
        }
    }
}
