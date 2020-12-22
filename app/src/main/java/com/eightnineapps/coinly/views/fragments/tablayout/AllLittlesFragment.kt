package com.eightnineapps.coinly.views.fragments.tablayout

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.viewmodels.fragmentviewmodels.AllLittlesFragmentViewModel
import com.eightnineapps.coinly.views.activities.startup.HomeActivity.Companion.tabLayout
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_bigs.view.*
import kotlinx.android.synthetic.main.fragment_littles.view.*

class AllLittlesFragment : Fragment() {

    private lateinit var allLittlesFragmentViewModel: AllLittlesFragmentViewModel

    /**
     * Overrides the onCreate method to allow the fragments to have an options menu
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        allLittlesFragmentViewModel = ViewModelProvider(this).get(AllLittlesFragmentViewModel::class.java)
        allLittlesFragmentViewModel.startQueryForAllLittles()
    }

    /**
     * Inflates the my profile fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return createLittlesTab(inflater.inflate(R.layout.fragment_littles, container, false))
    }

    /**
     * Override the onCreateOptionsMenu to inflate it with our custom layout
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragments_app_bar_menu, menu)
        val searchIcon = menu.findItem(R.id.menu_search)
        searchIcon.isVisible = true
        allLittlesFragmentViewModel.setUpSearchView(searchIcon.actionView as SearchView)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Checks for changes to the members of the recyclerview for when the user swipes back to
     * this tab in the tablayout
     */
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible && this::allLittlesFragmentViewModel.isInitialized) checkForRemovalOrAdditionOfLittle()
    }

    /**
     * Determines whether or not a little needs to be added or removed
     */
    private fun checkForRemovalOrAdditionOfLittle() {
        if (CurrentUser.littleToBeRemoved != null) {
            allLittlesFragmentViewModel.removeUser(CurrentUser.littleToBeRemoved!!)
            CurrentUser.littleToBeRemoved = null
        }
        if (CurrentUser.littleToBeAdded != null) {
            allLittlesFragmentViewModel.addUser(CurrentUser.littleToBeAdded!!)
            CurrentUser.littleToBeAdded = null
        }
    }

    /**
     * Sets up the little tab fragment for the user
     */
    private fun createLittlesTab(view: View): View {
        addAllLittlesToRecycler(view)
        addSpaceBetweenItemsInRecycler(context, view)
        return view
    }

    /**
     * Makes sure the query task is completed before continuing
     */
    private fun addAllLittlesToRecycler(view: View) {
        if (allLittlesFragmentViewModel.hasLoadedUsers()) {
            attachAdapter(view)
        } else {
            val allLittlesQueryTask = allLittlesFragmentViewModel.getAllLittlesQuery()!!
            if (allLittlesQueryTask.isComplete) {
                handleQueryTask(allLittlesQueryTask, view)
            } else {
                allLittlesQueryTask.addOnCompleteListener {
                    handleQueryTask(allLittlesQueryTask, view)
                }
            }
        }
    }

    /**
     * Attaches the recyclerview's adapter from when it was scrolled off screen
     */
    private fun attachAdapter(view: View) {
        view.allLittlesRecyclerView.adapter = allLittlesFragmentViewModel.getAdapter()
        showOrHideEmptyLittlesListMessage(view)
    }

    /**
     * If the user has no littles, show the message to direct the user to the linkup page
     */
    private fun showOrHideEmptyLittlesListMessage(view: View) {
        view.no_littles_text_view.visibility = if (allLittlesFragmentViewModel.getAdapter().itemCount == 0) View.VISIBLE else View.GONE
    }

    /**
     * Gathers all the littles and sets up the recyclerview to place them in
     */
    private fun handleQueryTask(allLittlesQueryTask: Task<QuerySnapshot>, view: View) {
        allLittlesFragmentViewModel.compileUserDataToList(allLittlesQueryTask.result!!)
        allLittlesFragmentViewModel.createAdapter()
        allLittlesFragmentViewModel.setUpDataForSearchView()
        attachAdapter(view)
    }

    /**
     * Adds space between recycler view list itemsF
     */
    private fun addSpaceBetweenItemsInRecycler(context: Context?, view: View) {
        val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.space_between_list_items)!!)
        view.allLittlesRecyclerView.addItemDecoration(itemDecorator)
    }
}