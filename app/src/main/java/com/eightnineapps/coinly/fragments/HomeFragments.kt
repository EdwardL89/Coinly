package com.eightnineapps.coinly.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity.Companion.database
import com.eightnineapps.coinly.interfaces.CallBack
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_create_profile.view.*
import kotlinx.android.synthetic.main.user_list_view_layout.view.*
import kotlinx.coroutines.runBlocking

/**
 * A Single fragment class. Represents a single tab in the tab layout on the home activity
 * Can represent the Big, Little, or Linkup tab
 */
class HomeFragments : Fragment() {

    private var currentTabPosition = 0
    private val BIGS_TAB = 0
    private val LITTLES_TAB = 1
    private lateinit var allUsersRecyclerViewList: RecyclerView
    private var allUserNames: MutableList<String> = ArrayList()

    /**
     * Use the passed in position Int from the ViewPagerAdapter class to determine the correct
     * layout to inflate
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        currentTabPosition = arguments?.getInt("FRAGMENT_ID")!!
        var fragmentLayoutId = getCurrentFragmentResourceId(currentTabPosition)
        val view = inflater.inflate(fragmentLayoutId, container, false)
        return createFragmentLayout(view, currentTabPosition)
    }

    private fun getCurrentFragmentResourceId(position: Int): Int {
        return when (position) {
            BIGS_TAB -> R.layout.fragment_bigs
            LITTLES_TAB -> R.layout.fragment_littles
            else -> R.layout.fragment_linkup
        }
    }

    /**
     * Determines which tab fragment's UI we need to set up
     */
    private fun createFragmentLayout(view: View, currentTabPosition: Int): View {
        return when (currentTabPosition) {
            BIGS_TAB -> createBigTab(view)
            LITTLES_TAB -> createLittleTab(view)
            else -> createLinkupTab(view)
        }
    }

    /**
     * Sets up the big tab fragment for the user
     */
    private fun createBigTab(view: View): View {
        return view
    }

    /**
     * Sets up the little tab fragment for the user
     */
    private fun createLittleTab(view: View): View {
        return view
    }

    /**
     * Sets up the linkup tab fragment for the user
     */
    private fun createLinkupTab(view: View): View {
        allUsersRecyclerViewList = view.findViewById(R.id.allUsersRecyclerView)
        allUserNames.clear()
        getAllUserNames(allUserNames, database, object: CallBack {
            override fun onCallBack(usernames: MutableList<String>) {
                allUsersRecyclerViewList.layoutManager = LinearLayoutManager(context)
                allUsersRecyclerViewList.adapter = AllUserNamesAdapter(allUserNames, context!!)
            }
        })
        return view
    }

    private fun getAllUserNames(allUserNames: MutableList<String>, database: FirebaseFirestore, callback: CallBack) {
        database.collection("users").get().addOnCompleteListener{
                task -> addUserNamesToList(task, allUserNames, callback)
        }
    }

    private fun addUserNamesToList(task: Task<QuerySnapshot>, allUserNames: MutableList<String>, callBack: CallBack) {
        if (task.isSuccessful) {
            for (users in task.result!!) {
                val name = users.data["displayName"].toString()
                allUserNames.add(name)
            }
            callBack.onCallBack(allUserNames)
        } else {
            //TODO: Throw an exception
        }
    }
    
    class AllUserNamesAdapter(_items: List<String>, _context: Context): RecyclerView.Adapter<AllUserNamesAdapter.ViewHolder>() {
        
        private var list = _items
        private var context = _context
        
        class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            // view here is user_list_view_layout.xml
            // and .user is the text view we have in the above file
            // so basically, this method is where you set the content of 
            // each item in the list
            val singleUserName: TextView = view.user_name_text_view
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater
                .from(context)
                .inflate(R.layout.user_list_view_layout, parent, false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.singleUserName.text = list[position]
        }
    }
    
}
