package com.robert.quickreturntoolbar

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity

/**
 * Demo activity for a quick returning toolbar.
 *
 */
class MainActivity : FragmentActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.my_toolbar) as Toolbar
        toolbar.title = "App Title"
        toolbar.subtitle = "Subtitle"
        val mObservableScrollView = findViewById<View>(R.id.scroll_view) as ObservableScrollView
        //val placeholderView = findViewById<View>(R.id.my_toolbar)
        QuickReturnHandler.Companion.setup(toolbar, toolbar, mObservableScrollView)
    }
}