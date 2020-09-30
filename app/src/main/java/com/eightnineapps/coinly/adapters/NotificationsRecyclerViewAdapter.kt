package com.eightnineapps.coinly.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.objects.Notification
import com.eightnineapps.coinly.enums.NotificationType
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.android.synthetic.main.notification_dialogue_layout.view.*
import kotlinx.android.synthetic.main.notification_layout.view.*
import kotlinx.android.synthetic.main.notification_layout.view.accept_button

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

        private val context = view.context
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
        @SuppressLint("InflateParams")
        override fun onClick(view: View?) { //Go to review page when a notification is clicked on
            val notification = notificationList[recyclerView!!.getChildLayoutPosition(view!!)]
            val builder = AlertDialog.Builder(context)
            val dialogueView = (context as Activity).layoutInflater.inflate(R.layout.notification_dialogue_layout, null)
            setNotificationContext(dialogueView, notification)
            builder.setView(dialogueView)
            val dialog = builder.create()
            setUpDialogButtons(dialogueView, dialog, view, notification.type)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            dialog.window!!.attributes = setDialogDimensions(dialog)
        }

        /**
         * Sets the content of the notification dialogue when it's tapped from the recycler view
         */
        private fun setNotificationContext(dialogueView: View, notification: Notification) {
            if (notification.type == NotificationType.GIVING_COINS) {
                dialogueView.cancel_notification_button.visibility = View.GONE
                dialogueView.accept_button.text = context.getString(R.string.OK)
                var dialogueContent = notification.message
                if (notification.moreInformation != "") {
                    dialogueContent += "\n\n Your Big left a note:\n${notification.moreInformation}"
                }
                dialogueView.notification_content.text = dialogueContent
            } else {
                dialogueView.notification_content.text = notification.moreInformation
            }
        }

        /**
         * Sets the dimensions of the set new prize dialogue
         */
        private fun setDialogDimensions(dialog: AlertDialog): WindowManager.LayoutParams {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window!!.attributes)
            layoutParams.width = 900
            layoutParams.height = 1200
            return layoutParams
        }

        private fun setUpDialogButtons(dialogueView: View, dialog: AlertDialog, notificationView: View, notificationType: NotificationType) {
            dialogueView.cancel_notification_button.setOnClickListener {
                val position = recyclerView!!.getChildLayoutPosition(notificationView)
                removeNotification(position)
                if (notificationList.isEmpty()) (context as Activity).no_notifications_image.visibility = View.VISIBLE
                dialog.cancel()
            }
            dialogueView.accept_button.setOnClickListener {
                val position = recyclerView!!.getChildLayoutPosition(notificationView)
                val notification = notificationList[position]
                if (notification.type == NotificationType.REQUESTING_COINS && notification.coins > CurrentUser.instance!!.coins) {
                    Toast.makeText(context, "You don't have enough coins! Denying Request", Toast.LENGTH_LONG).show()
                } else if (notificationType != NotificationType.GIVING_COINS) {
                    notification.execute()
                }
                removeNotification(position)
                if (notificationList.isEmpty()) (context as Activity).no_notifications_image.visibility = View.VISIBLE
                dialog.cancel()
            }
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
     * Defines what each UI element (defined in the ViewHolder class above) maps to
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.notificationContent.text = notification.message
        Glide.with(holder.itemView.context).load(notificationList[position].profilePictureUri).into(holder.profilePicture)
        if (notification.type == NotificationType.GIVING_COINS) {
            holder.acceptButton.text = holder.itemView.context.getString(R.string.OK)
            holder.acceptButton.setOnClickListener {
                removeNotification(position)
                if (notificationList.isEmpty()) (holder.itemView.context as Activity).no_notifications_image.visibility = View.VISIBLE
            }
        } else {
            holder.acceptButton.text = holder.itemView.context.getString(R.string.accept)
            holder.acceptButton.setOnClickListener {
                if (notification.type == NotificationType.REQUESTING_COINS && notification.coins > CurrentUser.instance!!.coins) {
                    Toast.makeText(holder.itemView.context, "You don't have enough coins! Denying Request", Toast.LENGTH_LONG).show()
                } else {
                    notification.execute()
                }
                removeNotification(position)
                if (notificationList.isEmpty()) (holder.itemView.context as Activity).no_notifications_image.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Removes the notification at the given position
     */
    private fun removeNotification(position: Int) {
        Firestore.removeNotification(CurrentUser.instance!!.email!!, notificationList[position])
        notificationList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, notificationList.size)
    }

    /**
     * Grabs an instance of the attached recycler view
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }
}