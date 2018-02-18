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
import com.treehacks.hopify.hopify.server.Datum
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

class MapListViewAdapter(
        private val dataset: List<Datum>
) : RecyclerView.Adapter<MapListViewAdapter.ViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.place_item, parent, false) as LinearLayout

        context = parent.context

        return MapListViewAdapter.ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val datum = dataset[position]
        holder.count.text = (position + 1).toString()
        holder.name.text = datum.name
        holder.types.text = "Categories: ${datum.types?.map { it.underscoreToProper() }?.joinToString(", ") ?: ""}"
        holder.rating.text = "Rating: ${datum.rating}"
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    class ViewHolder(rootView: View) : RecyclerView.ViewHolder(rootView) {
        val count by bindView<TextView>(R.id.count)
        val name by bindView<TextView>(R.id.name)
        val types by bindView<TextView>(R.id.types)
        val rating by bindView<TextView>(R.id.rating)
    }
}
