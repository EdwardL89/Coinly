package com.eightnineapps.coinly.views.fragments.profiles.big

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.BigProfileViewModel
import com.eightnineapps.coinly.views.activities.profiles.BigProfileHost
import kotlinx.android.synthetic.main.fragment_request.*

/**
 * Allows a little send a request to a big for coins
 */
class RequestCoinsFragment : Fragment() {

    private val bigProfileViewModel: BigProfileViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfilePicture()
        setUpButtons()
    }

    /**
     * Determines actions based on what items in the action bar are selected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            //onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * Loads the observe user's profile picture
     */
    private fun loadProfilePicture() {
        //Glide.with(view).load(intent.getSerializableExtra("profile_picture_uri") as String).into(view.findViewById(R.id.user_profile_picture))
    }

    /**
     * Sets the on click listeners of all buttons of this activity
     */
    private fun setUpButtons() {
        cancel_request_coins_button.setOnClickListener {
            activity!!.onBackPressed()
        }
    }
}
