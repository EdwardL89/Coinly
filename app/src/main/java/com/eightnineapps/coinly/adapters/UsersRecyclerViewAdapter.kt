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
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.models.Firestore
import com.eightnineapps.coinly.views.activities.profiles.BigProfileHost
import com.eightnineapps.coinly.views.activities.profiles.LinkupProfileActivity
import com.eightnineapps.coinly.views.activities.profiles.LittleProfileHost
import com.eightnineapps.coinly.views.activities.startup.HomeActivity.Companion.tabLayout
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.synthetic.main.user_list_view_layout.view.*

/**
 * An adapter class to populate the recycler view within a tab in the home tab layout
 */
class UsersRecyclerViewAdapter(_items: List<Triple<String, String, String>>): RecyclerView.Adapter<UsersRecyclerViewAdapter.ViewHolder>() {

    private var userList = _items.toMutableList()
    private var recyclerView: RecyclerView? = null

    /**
     * Explicitly defines the UI elements belonging to a single list element in the recycler view
     */
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private var context: Context = view.context
        val singleUserName: TextView = view.display_name_text_view
        val singleUserProfilePicture: ImageView = view.user_profile_picture

        init {
            view.isClickable = true
            view.setOnClickListener(this)
        }

        /**
         * Retrieves the DocumentSnapshot of the user that was tapped on and sends it with the intent
         * to the UserProfileActivity along with the current user as an object
         */
        override fun onClick(view: View?) {
            val pos = recyclerView!!.getChildLayoutPosition(view!!)
            val email = userList[pos].third
            val currentTab = tabLayout.selectedTabPosition
            Firestore.read(email).get().addOnSuccessListener {
                launchAppropriateActivity(currentTab, it.toObject(User::class.java)!!)
            }
        }

        /**
         * Launches the appropriate activity based on which fragment was tapped
         */
        private fun launchAppropriateActivity(currentTab: Int, observedUser: User) {
            val intent = when (currentTab) {
                0 -> Intent(context, BigProfileHost::class.java)
                1 -> Intent(context, LittleProfileHost::class.java)
                else -> Intent(context, LinkupProfileActivity::class.java)
            }
            intent.putExtra("observed_user", observedUser)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            context.startActivity(intent)
        }
    }

    /**
     * Inflates each row of the recycler view with the proper layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
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
        val currentUserInfo = userList[position]
        Glide.with(holder.itemView.context).load(currentUserInfo.first).into(holder.singleUserProfilePicture)
        holder.singleUserName.text = currentUserInfo.second
    }

    /**
     * Removes a user from the recycler view
     */
    fun removeUser(user: User) {
        userList.remove(userList.first { it.second == user.displayName })
        notifyDataSetChanged()
    }

    /**
     * Adds a user from the recycler view
     */
    fun addUser(document: DocumentSnapshot) {
        userList.add(Triple(document["profilePictureUri"].toString(),
            document["displayName"].toString(),
            document["email"].toString()))
        notifyDataSetChanged()
    }

    /**
     * Saves the recyclerview upon attachment to the adapter
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }
}