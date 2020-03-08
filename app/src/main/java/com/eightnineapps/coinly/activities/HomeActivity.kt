package com.eightnineapps.coinly.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.eightnineapps.coinly.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.LoginActivity.Companion.TAG
import com.eightnineapps.coinly.classes.User
import com.eightnineapps.coinly.classes.ViewPagerAdapter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

/**
 * Represents the home page the user lands on after logging in. Provides access to Bigs, Littles,
 * and the search bar for link ups
 */
class HomeActivity : AppCompatActivity() {

    /**
     * Provides access to data structures for all the below methods
     */
    companion object {
        val database = FirebaseFirestore.getInstance()
        var bigs:  MutableList<User> = mutableListOf()
        var littles:  MutableList<User> = mutableListOf()
    }

    /**
     * Initializes required elements of the home page
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar = findViewById<Toolbar>(R.id.home_toolbar)
        setSupportActionBar(toolbar)

        val viewPager = findViewById<ViewPager>(R.id.home_pager)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = viewPagerAdapter

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)
    }

    /**
     * Create the triple dots on the action bar to give options to the user, like signing out
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.home_action_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Assign actions to when the menu items in the action bar are tapped
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sign_out -> signOut()
            else -> print("Action unknown")
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Determine whether or not the user needs to be redirected to the profile creation activity
     */
    override fun onStart() {
        super.onStart()
        runBlocking { // (Temporary fix) Block the current thread to prevent setting the content view in onCreate
            val usersEmail = auth.currentUser?.email!!
            handleWhetherUserHasCreatedProfile(usersEmail)
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
            if (task.result?.exists()!!) populateBigsAndLittlesList(task.result!!)
            else startActivity(Intent(this, CreateProfileActivity::class.java))
        } else {
            Log.w(TAG, task.exception)
        }
    }

    /**
     * Populates the Bigs and Littles recycler list views
     */
    private fun populateBigsAndLittlesList(document: DocumentSnapshot) {
        Log.w(TAG, "CURRENT BIGS: ${document.data?.get("bigs")}")
        //bigs.addAll(document.data?.get("bigs") )
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
