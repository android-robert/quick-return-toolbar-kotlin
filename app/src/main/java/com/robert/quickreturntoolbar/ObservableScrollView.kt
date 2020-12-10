package com.robert.quickreturntoolbar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

/**
 * A custom ScrollView that can accept a scroll listener.
 */
class ObservableScrollView(context: Context?, attrs: AttributeSet?) : ScrollView(context, attrs) {
    private var mCallbacks: Callbacks? = null
    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (mCallbacks != null) {
            mCallbacks!!.onScrollChanged(t)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (mCallbacks != null) {
            when (ev.actionMasked) {
                MotionEvent.ACTION_DOWN -> mCallbacks!!.onDownMotionEvent()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> mCallbacks!!.onUpOrCancelMotionEvent()
            }
        }
        return super.onTouchEvent(ev)
    }

    public override fun computeVerticalScrollRange(): Int {
        return super.computeVerticalScrollRange()
    }

    fun setCallbacks(listener: Callbacks?) {
        mCallbacks = listener
    }

    interface Callbacks {
        fun onScrollChanged(scrollY: Int)
        fun onDownMotionEvent()
        fun onUpOrCancelMotionEvent()
    }
}