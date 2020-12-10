package com.robert.quickreturntoolbar

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import android.view.View

class QuickReturnHandler private constructor(private val mQuickReturnView: View,
                                             private val mPlaceholderView: View,
                                             private val mObservableScrollView: ObservableScrollView) : ObservableScrollView.Callbacks {
    private val mScrollSettleHandler = ScrollSettleHandler()
    private var mMinRawY = 0
    private var mState = STATE_ONSCREEN
    private var mQuickReturnHeight = 0
    private var mMaxScrollY = 0
    override fun onScrollChanged(scrollY: Int) {
        var scrollY = scrollY
        scrollY = Math.min(mMaxScrollY, scrollY)
        mScrollSettleHandler.onScroll(scrollY)
        val rawY = mPlaceholderView.top - scrollY
        var translationY = 0
        when (mState) {
            STATE_OFFSCREEN -> {
                if (rawY <= mMinRawY) {
                    mMinRawY = rawY
                } else {
                    mState = STATE_RETURNING
                }
                translationY = rawY
            }
            STATE_ONSCREEN -> {
                if (rawY < -mQuickReturnHeight) {
                    mState = STATE_OFFSCREEN
                    mMinRawY = rawY
                }
                translationY = rawY
            }
            STATE_RETURNING -> {
                translationY = rawY - mMinRawY - mQuickReturnHeight
                if (translationY > 0) {
                    translationY = 0
                    mMinRawY = rawY - mQuickReturnHeight
                }
                if (rawY > 0) {
                    mState = STATE_ONSCREEN
                    translationY = rawY
                }
                if (translationY < -mQuickReturnHeight) {
                    mState = STATE_OFFSCREEN
                    mMinRawY = rawY
                }
            }
        }
        mQuickReturnView.animate().cancel()
        mQuickReturnView.translationY = (translationY + scrollY).toFloat()
    }

    override fun onDownMotionEvent() {
        mScrollSettleHandler.setSettleEnabled(false)
    }

    override fun onUpOrCancelMotionEvent() {
        mScrollSettleHandler.setSettleEnabled(true)
        mScrollSettleHandler.onScroll(mObservableScrollView.scrollY)
    }

    @SuppressLint("HandlerLeak")
    private inner class ScrollSettleHandler : Handler() {
        private var mSettledScrollY = Int.MIN_VALUE
        private var mSettleEnabled = false
        fun onScroll(scrollY: Int) {
            if (mSettledScrollY != scrollY) {
                // Clear any pending messages and post delayed
                removeMessages(0)
                sendEmptyMessageDelayed(0, Companion.SETTLE_DELAY_MILLIS.toLong())
                mSettledScrollY = scrollY
            }
        }

        fun setSettleEnabled(settleEnabled: Boolean) {
            mSettleEnabled = settleEnabled
        }

        override fun handleMessage(msg: Message) {
            // Handle the scroll settling.
            if (STATE_RETURNING == mState && mSettleEnabled) {
                val mDestTranslationY: Int
                if (mSettledScrollY - mQuickReturnView.translationY > mQuickReturnHeight / 2) {
                    mState = STATE_OFFSCREEN
                    mDestTranslationY = Math.max(mSettledScrollY - mQuickReturnHeight, mPlaceholderView.top)
                } else {
                    mDestTranslationY = mSettledScrollY
                }
                mMinRawY = mPlaceholderView.top - mQuickReturnHeight - mDestTranslationY
                mQuickReturnView.animate().translationY(mDestTranslationY.toFloat())
            }
            mSettledScrollY = Int.MIN_VALUE // reset
        }
    }

    companion object {
        private const val STATE_ONSCREEN = 0
        private const val STATE_OFFSCREEN = 1
        private const val STATE_RETURNING = 2
        private const val SETTLE_DELAY_MILLIS = 100
        fun setup(quickReturnView: View,
                  placeholderView: View,
                  scrollView: ObservableScrollView) {
            QuickReturnHandler(quickReturnView, placeholderView, scrollView)
        }
    }

    init {
        mObservableScrollView.setCallbacks(this)
        mObservableScrollView.viewTreeObserver.addOnGlobalLayoutListener {
            onScrollChanged(mObservableScrollView.scrollY)
            mMaxScrollY = mObservableScrollView.computeVerticalScrollRange() - mObservableScrollView.height
            mQuickReturnHeight = mQuickReturnView.height
        }
    }
}