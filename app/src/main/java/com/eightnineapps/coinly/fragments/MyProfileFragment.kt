package com.eightnineapps.coinly.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity.Companion.database
import com.eightnineapps.coinly.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.adapters.NotificationsRecyclerViewAdapter
import com.eightnineapps.coinly.classes.Notification
import com.eightnineapps.coinly.classes.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

class MyProfileFragment : Fragment() {

    private lateinit var currentUser: User
    private lateinit var notificationsRecyclerView: RecyclerView

    /**
     * Inflates the my profile fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)
        notificationsRecyclerView = view.findViewById(R.id.notificationsRecyclerView)
        return createMyProfileTab(view)
    }

    override fun onResume() {
        if (notificationsRecyclerView.adapter != null) {
            refreshNotifications()
        }
        super.onResume()
    }

    private fun refreshNotifications() {
        database.collection("users").document(auth.currentUser?.email!!).get().addOnCompleteListener {
                task -> updateNotifications(notificationsRecyclerView, task.result?.toObject(User::class.java)!!.notifications)
        }
    }

    /**
     * Sets up the My Profile tab fragment for the user
     */
    private fun createMyProfileTab(view: View): View {
        database.collection("users").document(auth.currentUser?.email!!).get().addOnCompleteListener {
                task -> populateMyProfileUI(task, view)
        }
        return view
    }

    /**
     * Populates the current User's profile activity tab
     */
    private fun populateMyProfileUI(task: Task<DocumentSnapshot>, view: View) {
        Log.w("INFO", "CALLLEEDDDDDDD")
        currentUser = task.result!!.toObject(User::class.java)!!
        Log.w("INFO", currentUser.notifications.size.toString())
        val myProfilePicture = view.findViewById<ImageView>(R.id.my_profile_picture)
        val myDisplayName = view.findViewById<TextView>(R.id.my_display_name_textView)
        Glide.with(activity!!).load(currentUser.profilePictureUri).into(myProfilePicture)
        myDisplayName.text = currentUser.displayName
        updateNotifications(notificationsRecyclerView, currentUser.notifications)
    }

    /**
     * Assigns the given recycler view's layout manager and adapter using the list whose data is being displayed,
     * but for notifications, where String is the type
     */
    private fun updateNotifications(recyclerViewList: RecyclerView, listToDisplay: MutableList<Notification>) {
        recyclerViewList.layoutManager = LinearLayoutManager(context)
        recyclerViewList.adapter = NotificationsRecyclerViewAdapter(listToDisplay, context!!)
    }
}