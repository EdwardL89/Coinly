package com.eightnineapps.coinly.views.activities.actions

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.viewmodels.activityviewmodels.actions.RequestCoinsViewModel
import kotlinx.android.synthetic.main.activity_request.*

/**
 * Allows a little send a request to a big for coins
 */
class RequestCoinsActivity : AppCompatActivity() {

    private lateinit var requestCoinsViewModel: RequestCoinsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        requestCoinsViewModel = ViewModelProvider(this).get(RequestCoinsViewModel::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)
        addCoinlyActionBarTitle()
        setUpButtons()
    }

    /**
     * Makes for a clean transition back to the previous activity with no animation or flashes
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
    /**
     * Sets the title of the action bar to the app name in the custom font through an image view
     */
    @SuppressLint("InflateParams")
    private fun addCoinlyActionBarTitle() {
        val actionBar = this.supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false)
        val v: View = LayoutInflater.from(this).inflate(R.layout.app_bar_title, null)
        actionBar.customView = v
        actionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))
    }

    /**
     * Sets the on click listeners of all buttons of this activity
     */
    private fun setUpButtons() {
        cancel_request_coins_button.setOnClickListener {
            finish()
        }
    }
}
