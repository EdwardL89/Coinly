package com.eightnineapps.coinly.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.UserProfileActivity
import com.eightnineapps.coinly.classes.User
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.user_list_view_layout.view.*

/**
 * An adapter class to populate the recycler view within a tab in the home tab layout
 */
class UsersRecyclerViewAdapter(_items: List<DocumentSnapshot>, _context: Context): RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder>() {

    private var userList = _items
    private var context = _context

    /**
     * Explicitly defines the UI elements belonging to a single list element in the recycler view
     */
    class ViewHolder(view: View, _items: List<DocumentSnapshot>): RecyclerView.ViewHolder(view), View.OnClickListener {
        private var context: Context = view.context
        private var ViewHolderUserList = _items
        val singleUserName: TextView = view.display_name_text_view
        val singleUserProfilePicture: ImageView = view.user_profile_picture

        init {
            view.isClickable = true
            view.setOnClickListener(this)
        }

        /**
         * Retrieves the DocumentSnapshot of the user that was tapped on and sends it with the intent
         * to the UserProfileActivity
         */
        override fun onClick(view: View?) {
            val displayName = view!!.display_name_text_view!!.text.toString()
            val userDocument = ViewHolderUserList.first { it.data?.get("displayName") == displayName }
            val intentWithUserDocument = Intent(context, UserProfileActivity::class.java)
                .putExtra("user_object", userDocument.toObject(User::class.java))
            context.startActivity(intentWithUserDocument)
        }
    }

    /**
     * Inflates each row of the recycler view with the proper layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(context)
                .inflate(R.layout.user_list_view_layout, parent, false), userList)
    }

    /**
     * Returns the number of items in the recycler view
     */
    override fun getItemCount(): Int {
        return userList.size
    }

    /**
     * Defines what each UI element (defined in the ViewHolder class above) maps to
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentUser = userList[position]
        Glide.with(context).load(currentUser.data!!["profilePictureUri"]).into(holder.singleUserProfilePicture)
        holder.singleUserName.text = currentUser.data?.get("displayName").toString()
    }
}