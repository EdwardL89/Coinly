package com.eightnineapps.coinly.viewmodels.fragmentviewmodels

import android.content.Context
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.adapters.UsersRecyclerViewAdapter
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.classes.helpers.AuthHelper
import com.eightnineapps.coinly.models.Firestore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import java.util.*
import kotlin.collections.ArrayList

abstract class TabLayoutFragmentViewModel: ViewModel() {

    private var numOfEmails = 1
    private var emailsCounter = 1
    private val authHelper =
        AuthHelper()
    private lateinit var allAssociatesRecyclerViewList: RecyclerView
    private var allAssociates: MutableList<DocumentSnapshot> = ArrayList()
    private var allAssociatesToDisplay: MutableList<DocumentSnapshot> = ArrayList()

    /**
     * Instantiates the recycler view
     */
    fun setupRecycler(recyclerView: RecyclerView) {
        allAssociatesRecyclerViewList = recyclerView
        allAssociatesRecyclerViewList.removeAllViews()
    }

    fun removeUserFromAssociates(user: User) {
        allAssociates.remove(allAssociates.first { it["id"].toString() == user.id })
        allAssociatesToDisplay.remove(allAssociatesToDisplay.first { it["id"].toString() == user.id })
    }

    /**
     * Adds the user objects from the given email list to the recycler view
     */
    fun addUsersToRecyclerView(emails: MutableList<String>, context: Context?) {
        allAssociates.clear()
        allAssociatesToDisplay.clear()
        numOfEmails = emails.size
        emailsCounter = 1
        for (email in emails) queryFirestoreForSingleProfile(email, context)
    }

    /**
     * Queries the Firestore for all users
     */
    fun getAllUsers(context: Context?) {
        Firestore.getInstance().collection("users").get().addOnCompleteListener{ task -> addUsersToList(task, context) }
    }

    /**
     * Sets up the search action bar item to filter the raw data list and place matching items
     * into a displayed list to show in the recycler view
     */
    fun setUpSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) populateDisplayedListBasedOnSearchText(allAssociates, allAssociatesToDisplay, newText)
                else resetDisplayedList(allAssociates, allAssociatesToDisplay)
                allAssociatesRecyclerViewList.adapter?.notifyDataSetChanged()
                return true
            }
        })
    }

    /**
     * Adds space between recycler view list itemsF
     */
    fun addSpaceBetweenItems(context: Context?) {
        val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.space_between_list_items)!!)
        allAssociatesRecyclerViewList.addItemDecoration(itemDecorator)
    }

    /**
     * Assigns the given recycler view's layout manager and adapter using the list whose data is being displayed
     */
    fun updateRecyclerViewAdapterAndLayoutManager(context: Context?) {
        if (allAssociatesRecyclerViewList.layoutManager == null) allAssociatesRecyclerViewList.layoutManager = LinearLayoutManager(context)
        if (allAssociatesRecyclerViewList.adapter == null) allAssociatesRecyclerViewList.adapter = UsersRecyclerViewAdapter(allAssociatesToDisplay, context!!)
    }

    /**
     * Adds the retrieved users to the allUsers list upon successful task completion
     */
    private fun addUsersToList(task: Task<QuerySnapshot>, context: Context?) {
        if (task.isSuccessful) {
            for (users in task.result!!) if (users.data["email"] != authHelper.getAuthUserEmail()) allAssociates.add(users)
            allAssociatesToDisplay.addAll(allAssociates)
            updateRecyclerViewAdapterAndLayoutManager(context)
        }
    }

    /**
     * Queries the firestore for a user profile by email and adds them to the respective list
     * in the involved helper methods
     */
    private fun queryFirestoreForSingleProfile(email: String, context: Context?) {
        val user = User()
        user.email = email
        Firestore.read(user).get().addOnCompleteListener { subtask -> addObservedUserToList(subtask, context) }
    }

    /**
     * Upon successful completion of the second query, add the retrieved users to the list to be displayed
     * and update the recycler view once this has been done for all emails of the current user's associates.
     */
    private fun addObservedUserToList(task: Task<DocumentSnapshot>, context: Context?) {
        if (task.isSuccessful) {
            val currentUser = task.result!!
            allAssociates.add(currentUser)
            allAssociatesToDisplay.add(currentUser)
            if (emailsCounter == numOfEmails) updateRecyclerViewAdapterAndLayoutManager(context)
            else emailsCounter += 1
        } else {
            Log.w("INFO", "User read on Firestore unsuccessful")
        }
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
            if (it["displayName"].toString().toLowerCase(Locale.ROOT).contains(search)) displayedList.add(it)
        }
    }

    /**
     * Clears the displayed list and adds back in all the original data
     */
    private fun resetDisplayedList(rawDataList: MutableList<DocumentSnapshot>, displayedList: MutableList<DocumentSnapshot>) {
        displayedList.clear()
        displayedList.addAll(rawDataList)
    }
}