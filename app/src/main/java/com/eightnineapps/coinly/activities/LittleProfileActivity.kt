package com.eightnineapps.coinly.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.User
import kotlinx.android.synthetic.main.activity_little_profile.*

/**
 * Displays the prizes the little has claim from you as well as the prizes you have set for the little
 */
class LittleProfileActivity : AppCompatActivity() {

    private lateinit var currentUser: User
    private lateinit var observedUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_little_profile)
        currentUser = intent.getSerializableExtra("current_user") as User
        observedUser = intent.getSerializableExtra("observed_user") as User
        setUpButtons()
        addCoinlyActionBarTitle()
    }

    /**
     * Makes for a clean transition back to the previous activity with no animation or flashes
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    /**
     * Attaches the on click listeners to all buttons
     */
    private fun setUpButtons() {
        give_coins_button.setOnClickListener {
            val intent = Intent(this, GiveCoinsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        revoke_coins_button.setOnClickListener {
            val intent = Intent(this, RevokeCoinsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        remove_little_button.setOnClickListener {
            removeLittleAndSendBack()
        }
    }

    /**
     * Removes the observed Little and navigates to the previous page
     */
    private fun removeLittleAndSendBack() {
        currentUser.littles.remove(observedUser.email)
        HomeActivity.database.collection("users").document(currentUser.email!!).update("littles", currentUser.littles)
        HomeActivity.database.collection("users").document(observedUser.email!!).get().addOnCompleteListener {
                task ->
            if (task.isSuccessful) {
                val mostUpdatedObservedUser = task.result!!.toObject(User::class.java)!!
                val successfullyRemoved = mostUpdatedObservedUser.bigs.remove(currentUser.email.toString())
                if (successfullyRemoved) HomeActivity.database.collection("users").document(observedUser.email!!).update("bigs", mostUpdatedObservedUser.bigs)
            }
        }
        Toast.makeText(this, "Removed ${observedUser.displayName} as a little", Toast.LENGTH_SHORT).show()
        //TODO: add a listener to the littles recycler view to refresh it after the removal
        finish()
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
}
