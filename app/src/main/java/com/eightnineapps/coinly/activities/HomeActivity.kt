package com.eightnineapps.coinly.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.viewpager.widget.ViewPager
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.LoginActivity.Companion.TAG
import com.eightnineapps.coinly.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.adapters.ViewPagerAdapter
import com.eightnineapps.coinly.classes.FirestoreHelper
import com.eightnineapps.coinly.classes.FragmentBehaviors
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import kotlin.system.exitProcess

/**
 * Represents the home page the user lands on after logging in. Provides access to Bigs, Littles,
 * and the search bar for link ups
 */
class HomeActivity : FragmentBehaviors(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerToggle: ActionBarDrawerToggle

    /**
     * Provides access to data structures for all the below methods
     */
    companion object {
        val firestoreHelper = FirestoreHelper()
        val database = FirebaseFirestore.getInstance()
        lateinit var tabLayout: TabLayout
    }

    /**
     * Initializes required elements of the home page
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        drawerToggle = ActionBarDrawerToggle(this, drawer_layout, 0, 0)
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        navigation_view.setNavigationItemSelectedListener(this)

        try { // (Temporary fix)
            val usersEmail = auth.currentUser?.email!!
            handleWhetherUserHasCreatedProfile(usersEmail)
        } catch (e: Exception) {
            Log.w(TAG, "Navigating to createProfile activity..?")
        }

        addCoinlyActionBarTitle()
        addTabLayout()
    }

    /**
     * Determines what actions to take when the user clicks on an item in the drawer layout
     */
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.settings -> {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
            }
            R.id.sign_out -> {
                signOut()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Close the app when the user hits "back"
     */
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            finishAffinity()
            exitProcess(0)
        }
    }

    /**
     * Makes for a clean transition back to the previous activity with no animation or flashes
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    /**
     * Set up the sign out option in the action bar menu item
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
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
     * Adds the tab layout to the screen and sets the viewPager
     */
    private fun addTabLayout() {
        val viewPager = findViewById<ViewPager>(R.id.home_pager)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        tabLayout = findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)
        setTabLayoutIcons(tabLayout)
        setTabLayoutSelectedListener(tabLayout)
    }

    /**
     * Sets the icons for each tab of the tab layout
     */
    @SuppressLint("InflateParams")
    private fun setTabLayoutIcons(tabLayout: TabLayout) {
        tabLayout.getTabAt(0)!!.setIcon(R.drawable.bigs_icon)
        tabLayout.getTabAt(1)!!.setIcon(R.drawable.littles_icon)
        tabLayout.getTabAt(2)!!.setIcon(R.drawable.linkup_icon)
        tabLayout.getTabAt(3)!!.setIcon(R.drawable.profile_icon)

        for (i in 0..3) {
            tabLayout.getTabAt(i)!!.customView = layoutInflater.inflate(R.layout.custom_tab_icon_size, null)
        }
    }

    /**
     * Highlights the icon of the currently selected tab of the tablayout
     */
    private fun setTabLayoutSelectedListener(tabLayout: TabLayout) {
        val tabSelectedIconColor = ContextCompat.getColor(applicationContext, R.color.lightGreen)
        val tabUnselectedIconColor = ContextCompat.getColor(applicationContext, R.color.black)
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab!!.icon!!.colorFilter = PorterDuffColorFilter(tabSelectedIconColor, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab!!.icon!!.colorFilter = PorterDuffColorFilter(tabUnselectedIconColor, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                tab!!.icon!!.colorFilter = PorterDuffColorFilter(tabSelectedIconColor, PorterDuff.Mode.SRC_IN)
            }
        })
        tabLayout.getTabAt(0)?.select()
    }

    /**
     * Starts a query for a document with the current user's email
     */
    private fun handleWhetherUserHasCreatedProfile(usersEmail: String) {
        firestoreHelper.getUser(usersEmail).addOnCompleteListener { task -> handleQueryTask(task) }
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
