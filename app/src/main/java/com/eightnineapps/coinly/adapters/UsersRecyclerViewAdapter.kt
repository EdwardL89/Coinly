package com.eightnineapps.coinly.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.CreateProfileActivity.Companion.imageStorage
import com.eightnineapps.coinly.activities.LoginActivity.Companion.TAG
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
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val singleUserName: TextView = view.user_name_text_view
        val singleUserProfilePicture: ImageView = view.user_profile_picture
    }

    /**
     * Inflates each row of the recycler view with the proper layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(context)
                .inflate(R.layout.user_list_view_layout, parent, false))
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

        holder.singleUserName.text = currentUser.data?.get("displayName").toString()

        val profilePicture = imageStorage.reference
            .child("profile_pictures")
            .child(currentUser["id"].toString()).downloadUrl

        profilePicture
            .addOnSuccessListener {
                Glide.with(context).load(it).into(holder.singleUserProfilePicture)
        }
            .addOnFailureListener {
            Log.w(TAG, "Could not retrieve profile picture")
        }
    }
}