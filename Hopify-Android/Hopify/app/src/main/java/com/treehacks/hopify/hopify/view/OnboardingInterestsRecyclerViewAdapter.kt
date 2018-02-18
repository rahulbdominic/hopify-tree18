package com.treehacks.hopify.hopify.view

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
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

private fun String.underscoreToProper(): String {
    return this.split('_').joinToString(" ") {
        it.toCharArray().foldIndexed("") { index, acc, c ->
            if (index == 0) {
                acc + c.toUpperCase()
            } else {
                acc + c
            }
        }
    }
}

class OnboardingInterestsRecyclerViewAdapter(
        private val dataset: List<Interest>,
        private val selectedInterests: List<Interest>
) : RecyclerView.Adapter<OnboardingInterestsRecyclerViewAdapter.ViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.interests_item, parent, false) as LinearLayout

        context = parent.context

        return OnboardingInterestsRecyclerViewAdapter.ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]
        holder.title.text = item.value.underscoreToProper()
        holder.rootView.tag = item.value
        if (item in selectedInterests) {
            val pl = holder.rootView.paddingLeft
            val pt = holder.rootView.paddingTop
            val pr = holder.rootView.paddingRight
            val pb = holder.rootView.paddingBottom

            holder.rootView.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.interests_tile_background_selected
            )

            holder.rootView.setPadding(pl, pt, pr, pb)
            holder.title.setTextColor(ContextCompat.getColor(context, R.color.grey))
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    class ViewHolder(val rootView: View) : RecyclerView.ViewHolder(rootView) {
        val title by bindView<TextView>(R.id.interests_title)
    }
}
