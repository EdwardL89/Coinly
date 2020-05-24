package com.eightnineapps.coinly.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.eightnineapps.coinly.views.fragments.AllBigsFragment
import com.eightnineapps.coinly.views.fragments.AllLittlesFragment
import com.eightnineapps.coinly.views.fragments.LinkupFragment
import com.eightnineapps.coinly.views.fragments.MyProfileFragment

/**
 * Gets and displays the fragments within the tab layout on the home activity
 */
class ViewPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    /**
     * Creates and returns an instance of a new fragment
     * Passes identifying information to the arguments of the fragment instance to return the correct one
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> AllBigsFragment()
            1 -> AllLittlesFragment()
            2 -> LinkupFragment()
            else -> MyProfileFragment()
        }
    }

    /**
     * Returns the number of fragments in this adapter if responsible for
     */
    override fun getCount(): Int {
        return 4
    }

    /**
     * We don't want a title since we'll have icons to represent the tabs
     */
    override fun getPageTitle(position: Int): CharSequence? {
        return null
    }
}