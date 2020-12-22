package com.eightnineapps.coinly.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eightnineapps.coinly.views.fragments.prizes.PrizesClaimedFromYouFragment
import com.eightnineapps.coinly.views.fragments.prizes.YourSetPrizesFragment

class LittleProfileAdapter(_fragment: Fragment): FragmentStateAdapter(_fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) PrizesClaimedFromYouFragment()
        else return YourSetPrizesFragment()
    }

}