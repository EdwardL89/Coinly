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
import com.eightnineapps.coinly.viewmodels.fragmentviewmodels.AllBigsFragmentViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_bigs.view.*

class AllBigsFragment : Fragment() {

    private lateinit var allBigsFragmentViewModel: AllBigsFragmentViewModel

    /**
     * Overrides the onCreate method to allow the fragments to have an options menu and starts a
     * query
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        allBigsFragmentViewModel = ViewModelProvider(this).get(AllBigsFragmentViewModel::class.java)
        allBigsFragmentViewModel.startQueryForAllBigs()
        super.onCreate(savedInstanceState)
    }

    /**
     * Inflates the all bigs fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return createBigsTab(inflater.inflate(R.layout.fragment_bigs, container, false))
    }

    /**
     * Override the onCreateOptionsMenu to inflate it with our custom layout
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragments_app_bar_menu, menu)
        val searchIcon = menu.findItem(R.id.menu_search)
        searchIcon.isVisible = true
        allBigsFragmentViewModel.setUpSearchView(searchIcon.actionView as SearchView)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Checks for changes to the members of the recyclerview
     */
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible && this::allBigsFragmentViewModel.isInitialized) checkForRemovalOrAdditionOfBig()
    }

    /**
     * Determines whether or not a big needs to be added or removed
     */
    private fun checkForRemovalOrAdditionOfBig() {
        if (CurrentUser.bigToBeRemoved != null) {
            allBigsFragmentViewModel.removeUser(CurrentUser.bigToBeRemoved!!)
            CurrentUser.bigToBeRemoved = null
        }
        if (CurrentUser.bigToBeAdded != null) {
            allBigsFragmentViewModel.addUser(CurrentUser.bigToBeAdded!!)
            CurrentUser.bigToBeAdded = null
        }
    }

    /**
     * Sets up the big tab fragment for the user
     */
    private fun createBigsTab(view: View): View {
        addAllBigsToRecycler(view)
        addSpaceBetweenItemsInRecycler(context, view)
        return view
    }

    /**
     * Makes sure the query task is completed before continuing
     */
    private fun addAllBigsToRecycler(view: View) {
        if (allBigsFragmentViewModel.hasLoadedUsers()) {
            attachAdapter(view)
        } else {
            val allBigsQueryTask = allBigsFragmentViewModel.getAllBigsQuery()!!
            if (allBigsQueryTask.isComplete) {
                handleQueryTask(allBigsQueryTask, view)
            } else {
                allBigsQueryTask.addOnCompleteListener {
                    handleQueryTask(allBigsQueryTask, view)
                }
            }
        }
    }

    /**
     * Attaches the recyclerview's adapter from when it was scrolled off screen
     */
    private fun attachAdapter(view: View) {
        view.allBigsRecyclerView.adapter = allBigsFragmentViewModel.getAdapter()
        showOrHideEmptyBigsListMessage(view)
    }

    /**
     * If the user has no bigs, show the message to direct the user to the linkup page
     */
    private fun showOrHideEmptyBigsListMessage(view: View) {
        view.no_bigs_text_view.visibility = if (allBigsFragmentViewModel.getAdapter().itemCount == 0) View.VISIBLE else View.GONE
    }

    /**
     * Gathers all the bigs and sets up the recyclerview to place them in
     */
    private fun handleQueryTask(allBigsQueryTask: Task<QuerySnapshot>, view: View) {
        allBigsFragmentViewModel.compileUserDataToList(allBigsQueryTask.result!!)
        allBigsFragmentViewModel.createAdapter()
        allBigsFragmentViewModel.setUpDataForSearchView()
        attachAdapter(view)
    }

    /**
     * Adds space between recycler view list itemsF
     */
    private fun addSpaceBetweenItemsInRecycler(context: Context?, view: View) {
        val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.space_between_list_items)!!)
        view.allBigsRecyclerView.addItemDecoration(itemDecorator)
    }
}