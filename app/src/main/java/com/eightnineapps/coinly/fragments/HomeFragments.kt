package com.eightnineapps.coinly.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity.Companion.database
import com.eightnineapps.coinly.interfaces.CallBack
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.user_list_view_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * A Single fragment class. Represents a single tab in the tab layout on the home activity
 * Can represent the Big, Little, or Linkup tab
 */
class HomeFragments : Fragment() {

    private val BIGS_TAB = 0
    private val LITTLES_TAB = 1
    private val LINKUP_TAB = 2
    private var currentTabPosition = 0
    private lateinit var allUsersRecyclerViewList: RecyclerView
    private var allUserNames: MutableList<String> = ArrayList()
    private var allUserNamesOnToDisplay: MutableList<String> = ArrayList()
    private lateinit var searchItem: MenuItem

    /**
     * Use the passed in position Int from the ViewPagerAdapter class to determine the correct
     * layout to inflate
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        currentTabPosition = arguments?.getInt("FRAGMENT_ID")!!
        val fragmentLayoutId = getCurrentFragmentResourceId(currentTabPosition)
        val view = inflater.inflate(fragmentLayoutId, container, false)
        return createFragmentLayout(view, currentTabPosition)
    }

    /**
     * Override the onCreateOptionsMenu to inflate it with our custom layout
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragments_app_bar_menu, menu)
        searchItem = menu.findItem(R.id.menu_search)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Determines what the item menus in the app bar option items do
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_search) { // Programs the search button for the recycler view
            if (currentTabPosition == LINKUP_TAB) { // Search different lists for the 3 tabs
                val searchView = searchItem.actionView as SearchView
                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText!!.isNotEmpty()) {
                            allUserNamesOnToDisplay.clear()
                            val search = newText.toLowerCase(Locale.ROOT)
                            allUserNames.forEach {
                                if (it.toLowerCase(Locale.ROOT).contains(search)) {
                                    allUserNamesOnToDisplay.add(it)
                                }
                            }
                            allUsersRecyclerViewList.adapter?.notifyDataSetChanged()
                        } else {
                            allUserNamesOnToDisplay.clear()
                            allUserNamesOnToDisplay.addAll(allUserNames)
                            allUsersRecyclerViewList.adapter?.notifyDataSetChanged()
                        }
                        return true
                    }
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Overrides the onCreate method to allow the fragments to have an options menu
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    /**
     * Returns the resource Id of the fragment layout that needs to be created based on the position
     */
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
        allUserNamesOnToDisplay.clear()
        getAllUserNames(allUserNames, database, object: CallBack {
            override fun onCallBack(usernames: MutableList<String>) {
                allUsersRecyclerViewList.layoutManager = LinearLayoutManager(context)
                allUsersRecyclerViewList.adapter = AllUserNamesAdapter(allUserNamesOnToDisplay, context!!)
            }
        })
        return view
    }

    /**
     * Queries the Firestore for all user names
     */
    private fun getAllUserNames(allUserNames: MutableList<String>, database: FirebaseFirestore, callback: CallBack) {
        database.collection("users").get().addOnCompleteListener{
                task -> addUserNamesToList(task, allUserNames, callback)
        }
    }

    /**
     * Adds the retrieved user names to the allUserNames list upon successful task completion
     */
    private fun addUserNamesToList(task: Task<QuerySnapshot>, allUserNames: MutableList<String>, callBack: CallBack) {
        if (task.isSuccessful) {
            for (users in task.result!!) {
                val name = users.data["displayName"].toString()
                allUserNames.add(name)
            }
            allUserNamesOnToDisplay.addAll(allUserNames)
            callBack.onCallBack(allUserNames)
        }
    }

    /**
     * An adapter class to populate the recycler view
     */
    class AllUserNamesAdapter(_items: List<String>, _context: Context): RecyclerView.Adapter<AllUserNamesAdapter.ViewHolder>() {
        
        private var list = _items
        private var context = _context

        /**
         * Explicitly defines the UI elements belonging to a single list element in the recycler view
         */
        class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
            val singleUserName: TextView = view.user_name_text_view
        }

        /**
         * Inflates each row of the recycler view with the proper layout
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater
                .from(context)
                .inflate(R.layout.user_list_view_layout, parent, false))
        }

        /**
         * Returns the number of items in the recycler view
         */
        override fun getItemCount(): Int {
            return list.size
        }

        /**
         * Defines what each UI element (defined in the ViewHolder class above) maps to
         */
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.singleUserName.text = list[position]
        }
    }
    
}
