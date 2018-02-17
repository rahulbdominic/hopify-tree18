package com.treehacks.hopify.hopify.utils

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

class RecyclerItemClickObservable(private val recyclerView: RecyclerView, private val context: Context) : Observable<View>() {

    override fun subscribeActual(observer: Observer<in View>) {
        val listener = ItemListener(recyclerView, observer, context)
        observer.onSubscribe(listener)
        recyclerView.addOnItemTouchListener(listener)
    }

    internal class ItemListener(private val view: RecyclerView, private val observer: Observer<in View>, context: Context) : MainThreadDisposable(), RecyclerView.OnItemTouchListener {
        private var mGestureDetector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }
        })

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            if (this.mGestureDetector.onTouchEvent(e)) {
                val childView = rv.findChildViewUnder(e.x, e.y)
                if (childView != null) {
                    observer.onNext(childView)
                    return true
                } else {
                    return false
                }
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }

        override fun onDispose() {
            view.removeOnItemTouchListener(this)
        }
    }
}
