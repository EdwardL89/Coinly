package com.eightnineapps.coinly.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.R
import kotlinx.android.synthetic.main.notification_layout.view.*

/**
 * An adapter class to populate the user's notification recycler view
 */
class NotificationsRecyclerViewAdapter(_notifications: List<String>, _context: Context): RecyclerView.Adapter<NotificationsRecyclerViewAdapter.ViewHolder>() {

    private var notificationList = _notifications
    private var context = _context

    /**
     * Explicitly defines the UI elements belonging to a single list element in the recycler view
     */
    class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        private var context: Context = view.context
        val notificationContent: TextView = view.notificationInfoTextView

        init {
            view.isClickable = true
            view.setOnClickListener(this)
        }

        /**
         * Goes to a notification review activity where further action can be taken
         */
        override fun onClick(view: View?) {
            Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show()
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
    }

}