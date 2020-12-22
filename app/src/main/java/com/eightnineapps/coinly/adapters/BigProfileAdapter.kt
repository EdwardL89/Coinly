package com.eightnineapps.coinly.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.eightnineapps.coinly.views.fragments.prizes.PrizesClaimedFragment
import com.eightnineapps.coinly.views.fragments.prizes.PrizesToClaimFragment

class BigProfileAdapter(_fragment: Fragment): FragmentStateAdapter(_fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) PrizesToClaimFragment()
        else return PrizesClaimedFragment()
    }

}