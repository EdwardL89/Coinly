package com.eightnineapps.coinly.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity.Companion.database
import com.eightnineapps.coinly.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.adapters.AllNamesAdapter
import com.eightnineapps.coinly.interfaces.CallBack
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import java.util.*
import kotlin.collections.ArrayList

/**
 * A Single fragment class. Represents a single tab in the tab layout on the home activity
 * Can represent the Big, Little, or Linkup tab
 */
class HomeFragments : Fragment() {

    private val BIGS_TAB = 0
    private val LITTLES_TAB = 1
    private var currentTabPosition = 0

    private var numOfBigEmails = 1
    private var bigEmailsCounter = 1
    private var numOfLittleEmails = 1
    private var littleEmailsCounter = 1

    private lateinit var searchItem: MenuItem

    private lateinit var currentBigsEmails: MutableList<*>
    private lateinit var currentLittlesEmails: MutableList<*>

    private var allBigNames: MutableList<String> = ArrayList()
    private var allUserNames: MutableList<String> = ArrayList()
    private var allLittleNames: MutableList<String> = ArrayList()

    private lateinit var allBigsRecyclerViewList: RecyclerView
    private lateinit var allUsersRecyclerViewList: RecyclerView
    private lateinit var allLittlesRecyclerViewList: RecyclerView

    private var allBigNamesToDisplay: MutableList<String> = ArrayList()
    private var allUserNamesToDisplay: MutableList<String> = ArrayList()
    private var allLittleNamesToDisplay: MutableList<String> = ArrayList()

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
        searchItem = menu.findItem(R.id.menu_search)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Determines what the item menus in the app bar option items do
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_search) { // Programs the search button for the recycler view
            val searchView = searchItem.actionView as SearchView
            when (currentTabPosition) { 
                BIGS_TAB -> setUpSearchView(searchView, allBigNames, allBigNamesToDisplay, allBigsRecyclerViewList)
                LITTLES_TAB -> setUpSearchView(searchView, allLittleNames, allLittleNamesToDisplay, allLittlesRecyclerViewList)
                else -> setUpSearchView(searchView, allUserNames, allUserNamesToDisplay, allUsersRecyclerViewList)
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
     * Sets up the search action bar item to filter the raw data list and place matching items
     * into a displayed list to show in the recycler view
     */
    private fun setUpSearchView(searchView: SearchView, rawDataList: MutableList<String>,
                                displayedList: MutableList<String>, recyclerViewList: RecyclerView) {
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    populateDisplayedListBasedOnSearchText(rawDataList, displayedList, newText)
                } else {
                    resetDisplayedList(displayedList, rawDataList)
                }
                recyclerViewList.adapter?.notifyDataSetChanged()
                return true
            }
        })
    }

    /**
     * Adds to the displayed list the matching raw data items based on the search bar text
     */
    private fun populateDisplayedListBasedOnSearchText(rawDataList: MutableList<String>,
                                                       displayedList: MutableList<String>,
                                                       newText: String) {
        displayedList.clear()
        val search = newText.toLowerCase(Locale.ROOT)
        rawDataList.forEach {
            if (it.toLowerCase(Locale.ROOT).contains(search)) {
                displayedList.add(it)
            }
        }
    }

    /**
     * Clears the displayed list and adds back in all the original data
     */
    private fun resetDisplayedList(displayedList: MutableList<String>, rawDataList: MutableList<String>) {
        displayedList.clear()
        displayedList.addAll(rawDataList)
    }

    /**
     * Returns the resource Id of the fragment layout that needs to be created based on the position
     */
    private fun getCurrentFragmentResourceId(position: Int): Int {
        return when (position) {
            BIGS_TAB -> R.layout.fragment_bigs
            LITTLES_TAB -> R.layout.fragment_littles
            else -> R.layout.fragment_linkup
        }
    }

    /**
     * Determines which tab fragment's UI we need to set up
     */
    private fun createFragmentLayout(view: View, currentTabPosition: Int): View {
        return when (currentTabPosition) {
            BIGS_TAB -> createBigTab(view)
            LITTLES_TAB -> createLittleTab(view)
            else -> createLinkupTab(view)
        }
    }

    /**
     * Sets up the big tab fragment for the user
     */
    private fun createBigTab(view: View): View {
        allBigsRecyclerViewList = view.findViewById(R.id.allBigsRecyclerView)
        allBigNames.clear()
        allBigNamesToDisplay.clear()
        getAllAssociatesNames(true, object: CallBack {
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
        allLittlesRecyclerViewList = view.findViewById(R.id.allLittlesRecyclerView)
        allLittleNames.clear()
        allLittleNamesToDisplay.clear()
        getAllAssociatesNames(false, object: CallBack {
            override fun secondQueryCallBack(userEmails: MutableList<*>) {
                littleEmailsCounter = 1
                numOfLittleEmails = userEmails.size
                for (email in userEmails) queryFirestoreForSingleProfile(false, email)
            }
        })
        return view
    }

    /**
     * Queries the firestore for a user profile by email and adds their name to respective list
     * in the involved helper methods
     */
    private fun queryFirestoreForSingleProfile(queryForBigs: Boolean, email: Any?) {
        database.collection("users").document(email.toString()).get().addOnCompleteListener {
                subtask -> addObservedUserDisplayNameToList(queryForBigs, subtask)
        }
    }

    /**
     * Queries the Firestore to retrieve all Bigs or Littles (associates) the current user has
     */
    private fun getAllAssociatesNames(queryForBigs: Boolean, callback: CallBack) {
        val currentUserEmail = auth.currentUser?.email!!
        database.collection("users").document(currentUserEmail).get().addOnCompleteListener {
            task -> addAssociateNamesToList(queryForBigs, task, callback)
        }
    }

    /**
     * Begin the callback for the second query (upon successful completion of the first query) that
     * gets the display names belonging to the users with the matching emails
     */
    private fun addAssociateNamesToList(queryForBigs: Boolean, task: Task<DocumentSnapshot>, callBack: CallBack) {
        if (task.isSuccessful) {
            if (queryForBigs) {
                currentBigsEmails = task.result!!.data?.get("bigs") as MutableList<*>
                callBack.secondQueryCallBack(currentBigsEmails)
            } else {
                currentLittlesEmails = task.result!!.data?.get("littles") as MutableList<*>
                callBack.secondQueryCallBack(currentLittlesEmails)
            }
        }
    }

    /**
     * Upon successful completion of the second query, add the retrieved display names to the list to be displayed
     * and update the recycler view once this has been done for all emails of the current user's Littles.
     */
    private fun addObservedUserDisplayNameToList(queryForBigs: Boolean, task: Task<DocumentSnapshot>) {
        if (task.isSuccessful) {
            val currentUsersDisplayName = task.result!!.data?.get("displayName") as String
            if (queryForBigs) {
                allBigNames.add(currentUsersDisplayName)
                allBigNamesToDisplay.add(currentUsersDisplayName)
                if (bigEmailsCounter == numOfBigEmails) updateRecyclerViewAdapterAndLayoutManager(allBigsRecyclerViewList, allBigNamesToDisplay)
                else bigEmailsCounter += 1
            } else {
                allLittleNames.add(currentUsersDisplayName)
                allLittleNamesToDisplay.add(currentUsersDisplayName)
                if (littleEmailsCounter == numOfLittleEmails) updateRecyclerViewAdapterAndLayoutManager(allLittlesRecyclerViewList, allLittleNamesToDisplay)
                else littleEmailsCounter += 1
            }
        }
    }

    /**
     * Sets up the linkup tab fragment for the user
     */
    private fun createLinkupTab(view: View): View {
        allUsersRecyclerViewList = view.findViewById(R.id.allUsersRecyclerView)
        allUserNames.clear()
        allUserNamesToDisplay.clear()
        getAllUserNames()
        return view
    }

    /**
     * Queries the Firestore for all user names
     */
    private fun getAllUserNames() {
        database.collection("users").get().addOnCompleteListener{
                task -> addUserNamesToList(task)
        }
    }

    /**
     * Adds the retrieved user names to the allUserNames list upon successful task completion
     */
    private fun addUserNamesToList(task: Task<QuerySnapshot>) {
        if (task.isSuccessful) {
            for (users in task.result!!) {
                val name = users.data["displayName"].toString()
                allUserNames.add(name)
            }
            allUserNamesToDisplay.addAll(allUserNames)
            updateRecyclerViewAdapterAndLayoutManager(allUsersRecyclerViewList, allUserNamesToDisplay)
        }
    }

    /**
     * Assigns the given recycler view's layout manager and adapter using the list whose data is being displayed
     */
    private fun updateRecyclerViewAdapterAndLayoutManager(recyclerViewList: RecyclerView, listToDisplay: MutableList<String>) {
        recyclerViewList.layoutManager = LinearLayoutManager(context)
        recyclerViewList.adapter = AllNamesAdapter(listToDisplay, context!!)
    }
}
