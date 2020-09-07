package com.eightnineapps.coinly.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.views.activities.startup.HomeActivity.Companion.database
import com.eightnineapps.coinly.classes.objects.Notification
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.android.synthetic.main.notification_layout.view.*

/**
 * An adapter class to populate the user's notification recycler view
 */
class NotificationsRecyclerViewAdapter(_notifications: List<Notification>, _context: Context): RecyclerView.Adapter<NotificationsRecyclerViewAdapter.ViewHolder>() {

    private var notificationList = _notifications as MutableList<Notification>
    private var context = _context
    private var auth = FirebaseAuth.getInstance()

    /**
     * Explicitly defines the UI elements belonging to a single list element in the recycler view
     */
    class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        private var context: Context = view.context
        val notificationContent: TextView = view.notificationInfoTextView
        val acceptButton: Button = view.accept_button
        val profilePicture: ImageView = view.my_profile_picture

        init {
            view.isClickable = true
            view.setOnClickListener(this)
        }

        /**
         * Goes to a notification review activity where further action can be taken
         */
        override fun onClick(view: View?) { //Go to review page when a notification is clicked on
            Toast.makeText(context, "Go to review page", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Inflates each row of the recycler view with the proper layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.notification_layout, parent, false))
    }

    /**
     * Returns the number of items in the recycler view
     */
    override fun getItemCount(): Int {
        return notificationList.size
    }

    /**
     * Defines what each UI element (defined in the ViewHolder class above) maps to
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.notificationContent.text = notificationList[position].message
        Glide.with(context).load(notificationList[position].profilePictureUri).into(holder.profilePicture)
        holder.acceptButton.setOnClickListener {
            notificationList[position].execute()
            removeNotification(position)
            if (notificationList.isEmpty()) (context as Activity).no_notifications_image.visibility = View.VISIBLE
        }
    }

    /**
     * Removes the notification at the given position
     */
    private fun removeNotification(position: Int) {
        notificationList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, notificationList.size)
        database.collection("users").document(auth.currentUser?.email!!).update("notifications", notificationList)
    }
}