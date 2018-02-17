package com.treehacks.hopify.hopify.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.treehacks.hopify.hopify.R
import com.treehacks.hopify.hopify.model.Interest
import kotterknife.bindView

/**
 * Created by rahul on 17/02/2018.
 */
class OnboardingInterestsRecyclerViewAdapter(
        private val dataset: List<Interest>
) : RecyclerView.Adapter<OnboardingInterestsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.interests_item, parent, false) as LinearLayout

        return OnboardingInterestsRecyclerViewAdapter.ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]
        holder.title.text = item.value
        holder.rootView.tag = item.value
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    class ViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
        val title by bindView<TextView>(R.id.interests_title)
    }
}
