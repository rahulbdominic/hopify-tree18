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
class LoadingFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.loading_fragment, container, false) as LinearLayout
    }

    companion object {
        fun newInstance(): LoadingFragment {
            return LoadingFragment()
        }
    }
}