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
import com.eightnineapps.coinly.classes.helpers.NotificationDialogCreator
import com.eightnineapps.coinly.classes.objects.Notification
import com.eightnineapps.coinly.enums.NotificationType
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.android.synthetic.main.notification_layout.view.*

/**
 * An adapter class to populate the user's notification recycler view
 */
class NotificationsRecyclerViewAdapter(_notifications: MutableList<Notification>): RecyclerView.Adapter<NotificationsRecyclerViewAdapter.ViewHolder>() {

    private var notificationList = _notifications
    private var recyclerView: RecyclerView? = null

    /**
     * Explicitly defines the UI elements belonging to a single list element in the recycler view
     */
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private val dialogCreator = NotificationDialogCreator()
        val acceptButton: Button = view.accept_button
        val deleteButton: Button = view.delete_button
        val profilePicture: ImageView = view.my_profile_picture
        val notificationContent: TextView = view.notificationInfoTextView

        init {
            view.isClickable = true
            view.setOnClickListener(this)
        }

        /**
         * opens an alert dialogue where further action can be taken
         */
        override fun onClick(view: View?) {
            val notification = notificationList[recyclerView!!.getChildLayoutPosition(view!!)]
            dialogCreator.showDialog(dialogCreator.createAlertDialog(notification, view))
        }
    }

    /**
     * Inflates each row of the recycler view with the proper layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.notification_layout, parent, false))
    }

    /**
     * Returns the number of items in the recycler view
     */
    override fun getItemCount(): Int {
        return notificationList.size
    }

    /**
     * Grabs an instance of the attached recycler view
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    /**
     * Defines what each UI element (defined in the ViewHolder class above) maps to
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notificationList[position]
        setTextPeekAndImage(holder, notification)
        setupNotificationButtons(holder, notification)
    }

    /**
     * Sets the UI of the notification when seen inside the recyclerview
     */
    private fun setTextPeekAndImage(holder: ViewHolder, notification: Notification) {
        holder.notificationContent.text = notification.message
        Glide.with(holder.itemView.context).load(notification.profilePictureUri).into(holder.profilePicture)
    }

    /**
     * Sets the UI for the buttons on the notifications and what actions to be taken when they're tapped
     */
    private fun setupNotificationButtons(holder: ViewHolder, notification: Notification) {
        if (notification.type == NotificationType.GIVING_COINS || notification.type == NotificationType.CLAIMING_PRIZE) {
            setupCoinsGivenNotification(holder, notification)
        } else {
            setupNonCoinsGivenNotification(holder, notification)
        }
    }

    /**
     * Changes the accept button's text to "OK" and removes the notification when tapped
     */
    private fun setupCoinsGivenNotification(holder: ViewHolder, notification: Notification) {
        holder.deleteButton.visibility = View.GONE
        holder.acceptButton.text = holder.itemView.context.getString(R.string.OK)
        holder.acceptButton.setOnClickListener { removeNotification(notificationList.indexOf(notification), holder.itemView.context) }
    }

    /**
     * Changes the accept button's text to "ACCEPT" and executes the notification when tapped
     */
    private fun setupNonCoinsGivenNotification(holder: ViewHolder, notification: Notification) {
        holder.acceptButton.text = holder.itemView.context.getString(R.string.accept)
        holder.acceptButton.setOnClickListener { handleNonCoinsGivenNotification(holder, notification) }
        holder.deleteButton.setOnClickListener { removeNotification(notificationList.indexOf(notification), holder.itemView.context) }
    }

    /**
     * Executes the non "coins given" notification
     */
    private fun handleNonCoinsGivenNotification(holder: ViewHolder, notification: Notification) {
        if (notification.type == NotificationType.REQUESTING_COINS && notification.coins > CurrentUser.instance!!.coins) {
            Toast.makeText(holder.itemView.context, holder.itemView.context.getString(R.string.Request_Denial_Message), Toast.LENGTH_LONG).show()
        } else {
            notification.execute()
        }
        removeNotification(notificationList.indexOf(notification), holder.itemView.context)
    }

    /**
     * Removes the notification at the given position
     */
    private fun removeNotification(position: Int, context: Context) {
        Firestore.removeNotification(CurrentUser.instance!!.email!!, notificationList[position].id)
        notificationList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, notificationList.size)
        showEmptyNotificationsIconIfNecessary(context)
    }

    /**
     * After a notification is removed, checks if the list is empty to display the empty logo
     */
    private fun showEmptyNotificationsIconIfNecessary(context: Context) {
        if (notificationList.isEmpty()) (context as Activity).no_notifications_image.visibility = View.VISIBLE
    }
}