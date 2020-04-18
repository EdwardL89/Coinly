package com.eightnineapps.coinly.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.User

/**
 * Displays the prizes a little can claim from this big as well as the prizes already claimed
 */
class BigProfileActivity : AppCompatActivity() {

    private lateinit var currentUser: User
    private lateinit var observedUser: User
    private lateinit var appealButton: Button
    private lateinit var requestButton: Button
    private lateinit var removeBigButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_big_profile)
        appealButton = findViewById(R.id.appeal_button)
        removeBigButton = findViewById(R.id.remove_big_button)
        requestButton = findViewById(R.id.request_coins_button)
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
     * Sets up the on click listeners for all buttons
     */
    private fun setUpButtons() {
        appealButton.setOnClickListener {
            val intent = Intent(this, AppealActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        requestButton.setOnClickListener {
            val intent = Intent(this, RequestCoinsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }
        removeBigButton.setOnClickListener {
            removeBigAndSendBack()
        }
    }

    /**
     * Removes the observed Big and navigates to the previous page
     */
    private fun removeBigAndSendBack() {
        currentUser.bigs.remove(observedUser.email)
        HomeActivity.database.collection("users").document(currentUser.email!!).update("bigs", currentUser.bigs)
        HomeActivity.database.collection("users").document(observedUser.email!!).get().addOnCompleteListener {
                task ->
                    if (task.isSuccessful) {
                        val mostUpdatedObservedUser = task.result!!.toObject(User::class.java)!!
                        val successfullyRemoved = mostUpdatedObservedUser.littles.remove(currentUser.email.toString())
                        if (successfullyRemoved) HomeActivity.database.collection("users").document(observedUser.email!!).update("littles", mostUpdatedObservedUser.littles)
                    }
        }
        Toast.makeText(this, "Removed ${observedUser.displayName} as a big", Toast.LENGTH_SHORT).show()
        //TODO: add a listener to the bigs recycler view to refresh it after the removal
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
