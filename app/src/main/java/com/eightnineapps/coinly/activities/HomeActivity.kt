package com.eightnineapps.coinly.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.LoginActivity.Companion.TAG
import com.eightnineapps.coinly.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.adapters.ViewPagerAdapter
import com.eightnineapps.coinly.classes.FragmentBehaviors
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.system.exitProcess


/**
 * Represents the home page the user lands on after logging in. Provides access to Bigs, Littles,
 * and the search bar for link ups
 */
class HomeActivity : FragmentBehaviors() {

    /**
     * Provides access to data structures for all the below methods
     */
    companion object {
        val database = FirebaseFirestore.getInstance()
    }

    /**
     * Initializes required elements of the home page
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        addCoinlyActionBarTitle()
        addTabLayout()
    }

    /**
     * Determine whether or not the user needs to be redirected to the profile creation activity
     */
    override fun onStart() {
        super.onStart()
        try { // (Temporary fix)
            val usersEmail = auth.currentUser?.email!!
            handleWhetherUserHasCreatedProfile(usersEmail)
        } catch (e: Exception) {
            Log.w(TAG, "Navigating to createProfile activity..?")
        }
    }

    /**
     * Close the app when the user hits "back"
     */
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
        exitProcess(0)
    }

    /**
     * Set up the sign out option in the action bar menu item
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_sign_out) signOut()
        return super.onOptionsItemSelected(item)
    }

    /**
     * Sets the title of the action bar to the app name in the custom font through an image view
     */
    @SuppressLint("InflateParams")
    private fun addCoinlyActionBarTitle() {
        this.supportActionBar!!.setDisplayShowCustomEnabled(true)
        this.supportActionBar!!.setDisplayShowTitleEnabled(false)
        val v: View = LayoutInflater.from(this).inflate(R.layout.app_bar_title, null)
        this.supportActionBar!!.customView = v
    }

    /**
     * Adds the tab layout to the screen and sets the viewPager
     */
    private fun addTabLayout() {
        val viewPager = findViewById<ViewPager>(R.id.home_pager)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)
    }

    /**
     * Starts a query for a document with the current user's email
     */
    private fun handleWhetherUserHasCreatedProfile(usersEmail: String) {
        database.collection("users").document(usersEmail).get().addOnCompleteListener { task -> handleQueryTask(task) }
    }

    /**
     * Populates home screen or re-directs to the profile creation activity depending on whether a document
     * with the user's email was found
     */
    private fun handleQueryTask(task: Task<DocumentSnapshot>) {
        if (task.isSuccessful) {
            if (!task.result?.exists()!!) startActivity(Intent(this, CreateProfileActivity::class.java))
        } else {
            Log.w(TAG, task.exception)
        }
    }

    /**
     * Creates the sign-out button and initiates the Firebase and Google sign-out process
     */
    private fun signOut() {
        val googleSignInOption: GoogleSignInOptions = buildGoogleSignOutOption()
        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)
        auth.signOut() // Sign out of Firebase
        googleSignInClient.signOut().addOnCompleteListener{ // Sign out of the Google account
            startActivity(Intent(applicationContext, LoginActivity::class.java))
            this.finish()
        }
    }

    /**
     * Builds the Google sign-out option for the user
     */
    private fun buildGoogleSignOutOption(): GoogleSignInOptions {
        return GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
    }
}
