package com.eightnineapps.coinly.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity.Companion.database
import com.eightnineapps.coinly.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.classes.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.notification_layout.view.*

/**
 * An adapter class to populate the user's notification recycler view
 */
class NotificationsRecyclerViewAdapter(_notifications: List<String>, _context: Context): RecyclerView.Adapter<NotificationsRecyclerViewAdapter.ViewHolder>() {

    private var notificationList = _notifications as MutableList<String>
    private var context = _context

    /**
     * Explicitly defines the UI elements belonging to a single list element in the recycler view
     */
    class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        private var context: Context = view.context
        val notificationContent: TextView = view.notificationInfoTextView
        val acceptButton: Button = view.accept_button

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
        holder.notificationContent.text = notificationList[position]
        holder.acceptButton.setOnClickListener {
            acceptAddRequest(notificationList[position].split(" "))
            removeNotification(position)
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

    /**
     * Adds the requesting user to the receiving user's big or little list
     */
    private fun acceptAddRequest(info: List<String>) {
        val toAddUserDisplayName = info[0]
        val addingToUserEmail = auth.currentUser?.email!!
        val addingAs = info[info.size - 1].dropLast(1)
        database.collection("users").whereEqualTo("displayName", toAddUserDisplayName).limit(1).get().addOnCompleteListener {
            toAddUserTask -> fulfilMutualAdd(toAddUserTask, addingToUserEmail, addingAs + "s")
        }
    }

    /**
     * Initializes the sequence of events needed to add the two users to eachother's respective bigs and littles list
     */
    private fun fulfilMutualAdd(toAddUserTask: Task<QuerySnapshot>, addingToUserEmail: String, addingAs: String) {
        database.collection("users").document(addingToUserEmail).get().addOnCompleteListener {
            addingToUserTask -> addToEachother(toAddUserTask, addingToUserTask, addingAs)
        }
    }

    /**
     * Update the two user's bigs and littles list in the firestore with the new addition
     */
    private fun addToEachother(toAddUserTask: Task<QuerySnapshot>, addingToUserTask: Task<DocumentSnapshot>, addingAs: String) {
        val toAddUser = toAddUserTask.result?.toObjects(User::class.java)?.get(0)!!
        val addingToUser = addingToUserTask.result?.toObject(User::class.java)!!
        if (addingAs == "bigs") {
            if (!toAddUser.bigs.contains(addingToUser.email!!)) { // For when both users request and accept eachother
                toAddUser.bigs.add(addingToUser.email!!)
                database.collection("users").document(toAddUser.email!!).update(addingAs, toAddUser.bigs)
                addingToUser.littles.add(toAddUser.email!!)
                database.collection("users").document(addingToUser.email!!).update("littles", addingToUser.littles)
            }
        } else {
            if (!toAddUser.littles.contains(addingToUser.email!!)) {
                toAddUser.littles.add(addingToUser.email!!)
                database.collection("users").document(toAddUser.email!!).update(addingAs, toAddUser.littles)
                addingToUser.bigs.add(toAddUser.email!!)
                database.collection("users").document(addingToUser.email!!).update("bigs", addingToUser.bigs)
            }
        }
    }

}