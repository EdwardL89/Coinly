package com.eightnineapps.coinly.fragments

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
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
    private lateinit var searchIcon: MenuItem
    private lateinit var notificationsRecyclerView: RecyclerView
    private lateinit var prizesRecyclerView: RecyclerView

    /**
     * Inflates the my profile fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)
        notificationsRecyclerView = view.findViewById(R.id.notificationsRecyclerView)
        return createMyProfileTab(view)
    }

    /**
     * Override the onCreateOptionsMenu to inflate it with our custom layout
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragments_app_bar_menu, menu)
        searchIcon = menu.findItem(R.id.menu_search)
        searchIcon.isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Overrides the onCreate method to allow the fragments to have an options menu
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    /**
     * Refresh notifications every time the my profile fragment is revisited
     */
    override fun onResume() {
        if (notificationsRecyclerView.adapter != null) refreshNotifications()
        super.onResume()
    }

    /**
     * Query the firestore to get the most updated notifications
     */
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
        currentUser = task.result!!.toObject(User::class.java)!!
        val myProfilePicture = view.findViewById<ImageView>(R.id.my_profile_picture)
        val myDisplayName = view.findViewById<TextView>(R.id.my_display_name_textView)
        val coinCount = view.findViewById<TextView>(R.id.coin_count)
        val bigsCount = view.findViewById<TextView>(R.id.bigs_count)
        val littlesCount = view.findViewById<TextView>(R.id.littles_count)
        val emptyPrizesImage = view.findViewById<ImageView>(R.id.no_prizes_image)
        val emptyNotificationsImage = view.findViewById<ImageView>(R.id.no_notifications_image)
        if (currentUser.prizesClaimed.isEmpty()) emptyPrizesImage.visibility = View.VISIBLE else emptyPrizesImage.visibility = View.INVISIBLE
        if (currentUser.notifications.isEmpty()) emptyNotificationsImage.visibility = View.VISIBLE else emptyNotificationsImage.visibility = View.INVISIBLE
        coinCount.text = currentUser.coins.toString()
        bigsCount.text = currentUser.bigs.count().toString()
        littlesCount.text = currentUser.littles.count().toString()
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