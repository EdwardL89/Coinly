package com.eightnineapps.coinly.classes

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.eightnineapps.coinly.fragments.HomeFragment

class ViewPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val homeFragment = HomeFragment()
        val bundle = Bundle()
        bundle.putString("message", "fragment: $position")
        homeFragment.arguments = bundle
        return homeFragment
    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        //return super.getPageTitle(position)
        return "Fragment: $position"
    }
}