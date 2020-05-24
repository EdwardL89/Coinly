package com.eightnineapps.coinly.views.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.adapters.ViewPagerAdapter
import com.eightnineapps.coinly.classes.AuthHelper
import com.eightnineapps.coinly.viewmodels.activityviewmodels.HomeViewModel
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_home.*
import kotlin.system.exitProcess

/**
 * Represents the home page the user lands on after logging in. Provides access to Bigs, Littles,
 * and the search bar for link ups
 */
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var homeViewModel: HomeViewModel
    private val authHelper = AuthHelper()

    /**
     * Provides access to data structures for all the below methods
     */
    companion object {
        val database = FirebaseFirestore.getInstance()
        lateinit var tabLayout: TabLayout
    }

    /**
     * Initializes required elements of the home page
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel.setCurrentUser(intent)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        addCoinlyActionBarTitle()
        addTabLayout()
        setUpDrawer()
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
                authHelper.signOut(this, applicationContext)
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
     * Sets up the navigation drawer
     */
    private fun setUpDrawer() {
        drawerToggle = ActionBarDrawerToggle(this, drawer_layout, 0, 0)
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        navigation_view.setNavigationItemSelectedListener(this)
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
        home_pager.adapter = ViewPagerAdapter(supportFragmentManager)
        tabLayout = findViewById(R.id.tab_layout)
        tabLayout.setupWithViewPager(home_pager)
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
     * Highlights the icon of the currently selected tab of the tab layout
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
}
