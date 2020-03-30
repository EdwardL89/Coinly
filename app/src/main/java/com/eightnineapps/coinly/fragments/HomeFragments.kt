package com.eightnineapps.coinly.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity.Companion.database
import com.eightnineapps.coinly.activities.LoginActivity.Companion.TAG
import com.eightnineapps.coinly.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.adapters.NotificationsRecyclerViewAdapter
import com.eightnineapps.coinly.adapters.UsersRecyclerViewAdapter
import com.eightnineapps.coinly.classes.User
import com.eightnineapps.coinly.interfaces.CallBack
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

/**
 * A Single fragment class. Represents a single tab in the tab layout on the home activity
 * Can represent the Big, Little, or Linkup tab
 */
class HomeFragments : Fragment() {

    private val BIGS_TAB = 0
    private val LINKUP_TAB = 2
    private val LITTLES_TAB = 1
    private var currentTabPosition = 0

    private var numOfBigEmails = 1
    private var bigEmailsCounter = 1
    private var numOfLittleEmails = 1
    private var littleEmailsCounter = 1

    private var SHOW_SEARCH_ICON = true
    private lateinit var searchIcon: MenuItem

    private lateinit var currentBigsEmails: MutableList<*>
    private lateinit var currentLittlesEmails: MutableList<*>

    private lateinit var allBigsRecyclerViewList: RecyclerView
    private lateinit var allUsersRecyclerViewList: RecyclerView
    private lateinit var notificationsRecyclerView: RecyclerView
    private lateinit var allLittlesRecyclerViewList: RecyclerView

    private var allUsers: MutableList<DocumentSnapshot> = ArrayList()

    private var allBigsToDisplay: MutableList<DocumentSnapshot> = ArrayList()
    private var allUsersToDisplay: MutableList<DocumentSnapshot> = ArrayList()
    private var allLittlesToDisplay: MutableList<DocumentSnapshot> = ArrayList()

    companion object {
        var allLittles: MutableList<DocumentSnapshot> = ArrayList()
        var allBigs: MutableList<DocumentSnapshot> = ArrayList()
    }

    /**
     * Use the passed in position Int from the ViewPagerAdapter class to determine the correct
     * layout to inflate
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        currentTabPosition = arguments?.getInt("FRAGMENT_ID")!!
        val fragmentLayoutId = getCurrentFragmentResourceId(currentTabPosition)
        val view = inflater.inflate(fragmentLayoutId, container, false)
        return createFragmentLayout(view, currentTabPosition)
    }

    /**
     * Override the onCreateOptionsMenu to inflate it with our custom layout
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragments_app_bar_menu, menu)
        searchIcon = menu.findItem(R.id.menu_search)
        searchIcon.isVisible = SHOW_SEARCH_ICON
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Determines what the item menus in the app bar option items do
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_search) { // Programs the search button for the recycler view
            val searchView = searchIcon.actionView as SearchView
            when (currentTabPosition) {
                BIGS_TAB -> setUpSearchView(searchView,
                    allBigs,
                    allBigsToDisplay,
                    allBigsRecyclerViewList)
                LITTLES_TAB -> setUpSearchView(searchView,
                    allLittles,
                    allLittlesToDisplay,
                    allLittlesRecyclerViewList)
                else -> setUpSearchView(searchView,
                    allUsers,
                    allUsersToDisplay,
                    allUsersRecyclerViewList)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Overrides the onCreate method to allow the fragments to have an options menu
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    /**
     * Returns the resource Id of the fragment layout that needs to be created based on the position
     */
    private fun getCurrentFragmentResourceId(position: Int): Int {
        return when (position) {
            BIGS_TAB -> R.layout.fragment_bigs
            LITTLES_TAB -> R.layout.fragment_littles
            LINKUP_TAB -> R.layout.fragment_linkup
            else -> R.layout.fragment_my_profile
        }
    }

    /**
     * Determines which tab fragment's UI we need to set up
     */
    private fun createFragmentLayout(view: View, currentTabPosition: Int): View {
        return when (currentTabPosition) {
            BIGS_TAB -> createBigTab(view)
            LITTLES_TAB -> createLittleTab(view)
            LINKUP_TAB -> createLinkupTab(view)
            else -> createMyProfileTab(view)
        }
    }

    /**
     * Sets up the search action bar item to filter the raw data list and place matching items
     * into a displayed list to show in the recycler view
     */
    private fun setUpSearchView(searchView: SearchView,
                                rawDataList: MutableList<DocumentSnapshot>,
                                displayedList: MutableList<DocumentSnapshot>,
                                recyclerViewList: RecyclerView) {
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.w(TAG, "CALLED!!!!")
                if (newText!!.isNotEmpty()) {
                    populateDisplayedListBasedOnSearchText(rawDataList, displayedList, newText)
                } else {
                    resetDisplayedList(rawDataList, displayedList)
                }
                recyclerViewList.adapter?.notifyDataSetChanged()
                return true
            }
        })
    }

    /**
     * Adds to the displayed list the matching raw data items based on the search bar text
     */
    private fun populateDisplayedListBasedOnSearchText(rawDataList: MutableList<DocumentSnapshot>,
                                                       displayedList: MutableList<DocumentSnapshot>,
                                                       newText: String) {
        displayedList.clear()
        val search = newText.toLowerCase(Locale.ROOT)
        rawDataList.forEach {
            if (it["displayName"].toString().toLowerCase(Locale.ROOT).contains(search)) {
                displayedList.add(it)
            }
        }
    }

    /**
     * Clears the displayed list and adds back in all the original data
     */
    private fun resetDisplayedList(rawDataList: MutableList<DocumentSnapshot>, displayedList: MutableList<DocumentSnapshot>) {
        displayedList.clear()
        displayedList.addAll(rawDataList)
    }

    /**
     * Sets up the big tab fragment for the user
     */
    private fun createBigTab(view: View): View {
        showSearchIcon(true)
        allBigsRecyclerViewList = view.findViewById(R.id.allBigsRecyclerView)
        allBigs.clear()
        allBigsToDisplay.clear()
        queryFirestoreForAllAssociates(true, object: CallBack {
            override fun secondQueryCallBack(userEmails: MutableList<*>) {
                bigEmailsCounter = 1
                numOfBigEmails = userEmails.size
                for (email in userEmails) queryFirestoreForSingleProfile(true, email)
            }
        })
        return view
    }

    /**
     * Sets up the little tab fragment for the user
     */
    private fun createLittleTab(view: View): View {
        showSearchIcon(true)
        allLittlesRecyclerViewList = view.findViewById(R.id.allLittlesRecyclerView)
        allLittles.clear()
        allLittlesToDisplay.clear()
        queryFirestoreForAllAssociates(false, object: CallBack {
            override fun secondQueryCallBack(userEmails: MutableList<*>) {
                littleEmailsCounter = 1
                numOfLittleEmails = userEmails.size
                for (email in userEmails) queryFirestoreForSingleProfile(false, email)
            }
        })
        return view
    }

    /**
     * Queries the firestore for a user profile by email and adds them to the respective list
     * in the involved helper methods
     */
    private fun queryFirestoreForSingleProfile(queryForBigs: Boolean, email: Any?) {
        database.collection("users").document(email.toString()).get().addOnCompleteListener {
                subtask -> addObservedUserToList(queryForBigs, subtask)
        }
    }

    /**
     * Queries the Firestore to retrieve all Bigs or Littles (associates) the current user has
     */
    private fun queryFirestoreForAllAssociates(queryForBigs: Boolean, callback: CallBack) {
        val currentUserEmail = auth.currentUser?.email!!
        database.collection("users").document(currentUserEmail).get().addOnCompleteListener {
                task -> addAssociatesToList(queryForBigs, task, callback)
            }
    }

    /**
     * Begin the callback for the second query (upon successful completion of the first query) that
     * gets the users with the matching emails
     */
    private fun addAssociatesToList(queryForBigs: Boolean, task: Task<DocumentSnapshot>, callBack: CallBack) {
        try {
            if (task.isSuccessful) {
                if (queryForBigs) {
                    currentBigsEmails = task.result!!.data?.get("bigs") as MutableList<*>
                    callBack.secondQueryCallBack(currentBigsEmails)
                } else {
                    currentLittlesEmails = task.result!!.data?.get("littles") as MutableList<*>
                    callBack.secondQueryCallBack(currentLittlesEmails)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, e.message.toString())
        }
    }

    /**
     * Upon successful completion of the second query, add the retrieved users to the list to be displayed
     * and update the recycler view once this has been done for all emails of the current user's associates.
     */
    private fun addObservedUserToList(queryForBigs: Boolean, task: Task<DocumentSnapshot>) {
        if (task.isSuccessful) {
            val currentUser = task.result!!
            if (queryForBigs) {
                allBigs.add(currentUser)
                allBigsToDisplay.add(currentUser)
                if (bigEmailsCounter == numOfBigEmails) updateRecyclerViewAdapterAndLayoutManager(allBigsRecyclerViewList, allBigsToDisplay)
                else bigEmailsCounter += 1
            } else {
                allLittles.add(currentUser)
                allLittlesToDisplay.add(currentUser)
                if (littleEmailsCounter == numOfLittleEmails) updateRecyclerViewAdapterAndLayoutManager(allLittlesRecyclerViewList, allLittlesToDisplay)
                else littleEmailsCounter += 1
            }
        }
    }

    /**
     * Sets up the linkup tab fragment for the user
     */
    private fun createLinkupTab(view: View): View {
        showSearchIcon(true)
        allUsersRecyclerViewList = view.findViewById(R.id.allUsersRecyclerView)
        allUsers.clear()
        allUsersToDisplay.clear()
        getAllUsers()
        return view
    }

    /**
     * Queries the Firestore for all users
     */
    private fun getAllUsers() {
        database.collection("users").get().addOnCompleteListener{
                task -> addUsersToList(task)
        }
    }

    /**
     * Adds the retrieved users to the allUsers list upon successful task completion
     */
    private fun addUsersToList(task: Task<QuerySnapshot>) {
        if (task.isSuccessful) {
            for (users in task.result!!) {
                if (users.data["email"] != auth.currentUser!!.email) {
                    allUsers.add(users)
                }
            }
            allUsersToDisplay.addAll(allUsers)
            updateRecyclerViewAdapterAndLayoutManager(allUsersRecyclerViewList, allUsersToDisplay)
        }
    }

    /**
     * Assigns the given recycler view's layout manager and adapter using the list whose data is being displayed
     */
    private fun updateRecyclerViewAdapterAndLayoutManager(recyclerViewList: RecyclerView, listToDisplay: MutableList<DocumentSnapshot>) {
        recyclerViewList.layoutManager = LinearLayoutManager(context)
        recyclerViewList.adapter = UsersRecyclerViewAdapter(listToDisplay, context!!)
    }

    /**
     * Sets up the My Profile tab fragment for the user
     */
    private fun createMyProfileTab(view: View): View {
        showSearchIcon(false)
        notificationsRecyclerView = view.findViewById(R.id.notificationsRecyclerView)
        database.collection("users").document(auth.currentUser?.email!!).get().addOnCompleteListener {
                task -> populateMyProfileUI(task, view)
        }
        return view
    }

    /**
     * Retrieves all current notifications of the user
     */
    private fun getMyNotifications(currentUser: DocumentSnapshot): MutableList<String> {
        val currentUserObject = currentUser.toObject(User::class.java)
        return currentUserObject!!.notifications
    }

    /**
     * Assigns the given recycler view's layout manager and adapter using the list whose data is being displayed,
     * but for notifications, where String is the type
     */
    private fun updateNotifications(recyclerViewList: RecyclerView, listToDisplay: MutableList<String>) {
        recyclerViewList.layoutManager = LinearLayoutManager(context)
        recyclerViewList.adapter = NotificationsRecyclerViewAdapter(listToDisplay, context!!)
    }

    /**
     * Populates the current User's profile activity tab
     */
    private fun populateMyProfileUI(task: Task<DocumentSnapshot>, view: View) {
        val currentUserDocument = task.result
        val myProfilePicture = view.findViewById<ImageView>(R.id.my_profile_picture)
        val myDisplayName = view.findViewById<TextView>(R.id.my_display_name_textView)
        Glide.with(activity!!).load(currentUserDocument?.data!!["profilePictureUri"]).into(myProfilePicture)
        myDisplayName.text = currentUserDocument.data!!["displayName"].toString()
        updateNotifications(notificationsRecyclerView, getMyNotifications(currentUserDocument))
    }

    /**
     * Toggle the search bar icon
     */
    private fun showSearchIcon(show: Boolean) {
        SHOW_SEARCH_ICON = show
        activity?.invalidateOptionsMenu()
    }
}