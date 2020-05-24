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
import com.eightnineapps.coinly.viewmodels.activityviewmodels.actions.RevokeCoins
import kotlinx.android.synthetic.main.activity_revoke_coins.*

/**
 * Allows a big to revoke a specified number of coins from a little
 */
class RevokeCoinsActivity : AppCompatActivity() {

    private lateinit var revokeCoins: RevokeCoins

    override fun onCreate(savedInstanceState: Bundle?) {
        revokeCoins = ViewModelProvider(this).get(RevokeCoins::class.java)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_revoke_coins)
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
        cancel_revoke_coins_button.setOnClickListener {
            finish()
        }
    }
}
