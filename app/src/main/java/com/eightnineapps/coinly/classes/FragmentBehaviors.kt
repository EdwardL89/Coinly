package com.eightnineapps.coinly.classes

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity
import com.eightnineapps.coinly.activities.LoginActivity
import com.eightnineapps.coinly.adapters.UsersRecyclerViewAdapter
import com.eightnineapps.coinly.fragments.AllBigsFragment.Companion.allBigs
import com.eightnineapps.coinly.fragments.AllBigsFragment.Companion.allBigsRecyclerViewList
import com.eightnineapps.coinly.fragments.AllBigsFragment.Companion.allBigsToDisplay
import com.eightnineapps.coinly.fragments.AllBigsFragment.Companion.bigEmailsCounter
import com.eightnineapps.coinly.fragments.AllBigsFragment.Companion.currentBigsEmails
import com.eightnineapps.coinly.fragments.AllBigsFragment.Companion.numOfBigEmails
import com.eightnineapps.coinly.fragments.AllLittlesFragment.Companion.allLittles
import com.eightnineapps.coinly.fragments.AllLittlesFragment.Companion.allLittlesRecyclerViewList
import com.eightnineapps.coinly.fragments.AllLittlesFragment.Companion.allLittlesToDisplay
import com.eightnineapps.coinly.fragments.AllLittlesFragment.Companion.currentLittlesEmails
import com.eightnineapps.coinly.fragments.AllLittlesFragment.Companion.littleEmailsCounter
import com.eightnineapps.coinly.fragments.AllLittlesFragment.Companion.numOfLittleEmails
import com.eightnineapps.coinly.fragments.LinkupFragment
import com.eightnineapps.coinly.fragments.LinkupFragment.Companion.allUsersRecyclerViewList
import com.eightnineapps.coinly.fragments.LinkupFragment.Companion.allUsersToDisplay
import com.eightnineapps.coinly.interfaces.CallBack
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import java.lang.Exception
import java.util.*

/**
 * Defines all common behaviors of the existing fragments
 */
abstract class FragmentBehaviors : AppCompatActivity() {

    /**
     * Sets up the search action bar item to filter the raw data list and place matching items
     * into a displayed list to show in the recycler view
     */
    fun setUpSearchView(searchView: SearchView,
                                rawDataList: MutableList<DocumentSnapshot>,
                                displayedList: MutableList<DocumentSnapshot>,
                                recyclerViewList: RecyclerView
    ) {
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
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
    fun populateDisplayedListBasedOnSearchText(rawDataList: MutableList<DocumentSnapshot>,
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
     * Queries the firestore for a user profile by email and adds them to the respective list
     * in the involved helper methods
     */
    fun queryFirestoreForSingleProfile(queryForBigs: Boolean, email: Any?, context: Context?) {
        HomeActivity.database.collection("users").document(email.toString()).get().addOnCompleteListener {
                subtask -> addObservedUserToList(queryForBigs, subtask, context)
        }
    }

    /**
     * Queries the Firestore to retrieve all Bigs or Littles (associates) the current user has
     */
    fun queryFirestoreForAllAssociates(queryForBigs: Boolean, callback: CallBack) {
        HomeActivity.database.collection("users").document(LoginActivity.auth.currentUser?.email!!).get().addOnCompleteListener {
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
            Log.w(LoginActivity.TAG, e.message.toString())
        }
    }

    /**
     * Upon successful completion of the second query, add the retrieved users to the list to be displayed
     * and update the recycler view once this has been done for all emails of the current user's associates.
     */
    private fun addObservedUserToList(queryForBigs: Boolean, task: Task<DocumentSnapshot>, context: Context?) {
        if (task.isSuccessful) {
            val currentUser = task.result!!
            if (queryForBigs) {
                allBigs.add(currentUser)
                allBigsToDisplay.add(currentUser)
                if (bigEmailsCounter == numOfBigEmails) updateRecyclerViewAdapterAndLayoutManager(allBigsRecyclerViewList, allBigsToDisplay, context)
                else bigEmailsCounter += 1
            } else {
                allLittles.add(currentUser)
                allLittlesToDisplay.add(currentUser)
                if (littleEmailsCounter == numOfLittleEmails) updateRecyclerViewAdapterAndLayoutManager(allLittlesRecyclerViewList, allLittlesToDisplay, context)
                else littleEmailsCounter += 1
            }
        }
    }

    /**
     * Assigns the given recycler view's layout manager and adapter using the list whose data is being displayed
     */
    private fun updateRecyclerViewAdapterAndLayoutManager(recyclerViewList: RecyclerView, listToDisplay: MutableList<DocumentSnapshot>, context: Context?) {
        recyclerViewList.layoutManager = LinearLayoutManager(context)
        recyclerViewList.adapter = UsersRecyclerViewAdapter(listToDisplay, context!!)
    }

    /**
     * Queries the Firestore for all users
     */
    fun getAllUsers(context: Context?) {
        HomeActivity.database.collection("users").get().addOnCompleteListener{
                task -> addUsersToList(task, context)
        }
    }

    /**
     * Adds the retrieved users to the allUsers list upon successful task completion
     */
    private fun addUsersToList(task: Task<QuerySnapshot>, context: Context?) {
        if (task.isSuccessful) {
            for (users in task.result!!) {
                if (users.data["email"] != LoginActivity.auth.currentUser!!.email) {
                    LinkupFragment.allUsers.add(users)
                }
            }
            allUsersToDisplay.addAll(LinkupFragment.allUsers)
            updateRecyclerViewAdapterAndLayoutManager(allUsersRecyclerViewList, allUsersToDisplay, context)
        }
    }

    /**
     * Adds space between recycler view list itemsF
     */
    fun addSpaceBetweenItems(recyclerView: RecyclerView, context: Context?) {
        val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.space_between_list_items)!!)
        recyclerView.addItemDecoration(itemDecorator)
    }
}