package com.eightnineapps.coinly.views.activities.profiles

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.databinding.ActivityLittleProfileBinding
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.LittleProfileViewModel
import com.eightnineapps.coinly.views.activities.actions.GiveCoinsActivity
import com.eightnineapps.coinly.views.activities.actions.RevokeCoinsActivity
import kotlinx.android.synthetic.main.activity_little_profile.*
import kotlinx.android.synthetic.main.activity_little_profile.view.*
import kotlinx.android.synthetic.main.set_new_prize_dialogue_layout.view.*

/**
 * Displays the prizes the little has claim from you as well as the prizes you have set for the little
 */
class LittleProfileActivity : AppCompatActivity() {

    private lateinit var littleProfileViewModel: LittleProfileViewModel
    private lateinit var observedUserInstance: User
    private lateinit var binding: ActivityLittleProfileBinding
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        littleProfileViewModel = ViewModelProvider(this).get(LittleProfileViewModel::class.java)
        littleProfileViewModel.observedUserInstance = intent.getSerializableExtra("observed_user") as User
        observedUserInstance = littleProfileViewModel.observedUserInstance
        binding = DataBindingUtil.setContentView(this, R.layout.activity_little_profile)
        binding.littleProfileViewModel = littleProfileViewModel
        view = binding.root
        littleProfileViewModel.loadSetPrizes(view.setPrizesRecyclerView, this)
        addCoinlyActionBarTitle()
        addBackArrowToActionBar()
        loadProfilePicture()
        setUpButtons()
    }

    /**
     * Makes for a clean transition back to the previous activity with no animation or flashes
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    /**
     * Determines actions based on what items in the action bar are selected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * Catches the result of the intent that opens the gallery to select a profile picture image
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        littleProfileViewModel.handleGallerySelectionCompletion(requestCode, resultCode, data, this)
        openDialogue(this, applicationContext, data)
    }

    /**
     * Open a dialogue for the user to set the title and price of the new prize
     */
    private fun openDialogue(context: Context, appContext: Context, data: Intent?) {
        val builder = AlertDialog.Builder(context)
        val view = (context as Activity).layoutInflater.inflate(R.layout.set_new_prize_dialogue_layout, null)
        Glide.with(appContext).load(data!!.data).into(view.new_prize_picture)
        builder.setView(view)
        val dialog = builder.create()
        setUpDialogButtons(view, dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = setDialogDimensions(dialog)
    }

    /**
     * Sets the actions the buttons in the set new prize dialog will do
     */
    private fun setUpDialogButtons(view: View, dialog: AlertDialog) {
        view.cancel_button.setOnClickListener {
            dialog.cancel()
        }
        view.set_button.setOnClickListener {
            val prizeTitle = view.prize_title.text.toString()
            val prizePriceString = view.prize_price.text.toString()

            if (prizeTitle == "" || prizePriceString == "") {
                Toast.makeText(this, "Please add a title and price", Toast.LENGTH_SHORT).show()
            } else {
                littleProfileViewModel.uploadNewSetPrize(prizeTitle, Integer.parseInt(prizePriceString), this)
                Toast.makeText(this, "New Prize Set!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
    }

    /**
     * Sets the dimensions of the set new prize dialogue
     */
    private fun setDialogDimensions(dialog: AlertDialog): WindowManager.LayoutParams {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window!!.attributes)
        layoutParams.width = 900
        layoutParams.height = 1200
        return layoutParams
    }

    /**
     * Loads the observe user's profile picture
     */
    private fun loadProfilePicture() {
        Glide.with(view).load(littleProfileViewModel.observedUserInstance.profilePictureUri).into(view.findViewById(R.id.user_profile_picture))
    }

    /**
     * Attaches the on click listeners to all buttons
     */
    private fun setUpButtons() {
        give_coins_button.setOnClickListener {
            startActivity(Intent(this, GiveCoinsActivity::class.java)
                .putExtra("profile_picture_uri", observedUserInstance.profilePictureUri)
                .putExtra("display_name", observedUserInstance.displayName)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
        revoke_coins_button.setOnClickListener {
            startActivity(Intent(this, RevokeCoinsActivity::class.java)
                .putExtra("profile_picture_uri", observedUserInstance.profilePictureUri)
                .putExtra("display_name", observedUserInstance.displayName)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
        remove_little_button.setOnClickListener {
            littleProfileViewModel.removeLittleAndSendBack(this)
        }
        set_prize_button.setOnClickListener {
            littleProfileViewModel.imageUploadHelper.chooseImageFromGallery(this)
        }
    }

    /**
     * Sets the title of the action bar to the app name in the custom font through an image view
     */
    @SuppressLint("InflateParams")
    private fun addCoinlyActionBarTitle() {
        val actionBar = supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false)
        val v: View = LayoutInflater.from(this).inflate(R.layout.app_bar_title, null)
        actionBar.customView = v
        actionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))
    }

    /**
     * Adds a back arrow to navigate back to the previous activity
     */
    private fun addBackArrowToActionBar() {
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.arrow_back)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
