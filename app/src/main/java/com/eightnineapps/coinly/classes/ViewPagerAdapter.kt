package com.eightnineapps.coinly.classes

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.eightnineapps.coinly.fragments.HomeFragments

/**
 * Gets and displays the fragments within the tab layout on the home activity
 */
class ViewPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    /**
     * Creates and returns an instance of a new fragment
     * Passes identifying information to the arguments of the fragment instance to return the correct one
     */
    override fun getItem(position: Int): Fragment {
        val homeFragment = HomeFragments()
        return createCorrespondingFragment(homeFragment, position)
    }

    /**
     * Returns the number of fragments in this adapter if responsible for
     */
    override fun getCount(): Int {
        return 4
    }

    /**
     * Sets the name of the tabs in the tab layout
     */
    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Bigs"
            1 -> "Littles"
            2 -> "Linkup"
            else -> "Profile"
        }
    }

    /**
     * Returns the corresponding fragment based on the current position number by passing that number
     * to the fragment class so it can create the right one
     */
    private fun createCorrespondingFragment(fragment: HomeFragments, position: Int): Fragment {
        val bundle = Bundle()
        bundle.putInt("FRAGMENT_ID", position)
        fragment.arguments = bundle
        return fragment
    }
}