package com.eightnineapps.coinly.views.fragments.profiles.little

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.adapters.PrizesRecyclerViewAdapter
import com.eightnineapps.coinly.classes.objects.Prize
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.LittleProfileViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_little_profile.*
import kotlinx.android.synthetic.main.fragment_little_profile.view.*
import kotlinx.android.synthetic.main.set_new_prize_dialogue_layout.view.*

class LittleProfileFragment: Fragment() {

    private val littleProfileViewModel: LittleProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        littleProfileViewModel.observedUserInstance = activity!!.intent.getSerializableExtra("observed_user") as User
        littleProfileViewModel.startQueryForPrizesSet()
        littleProfileViewModel.startQueryForPrizesClaimed()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_little_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfile()
        setUpButtons()
        addPrizesSetToRecycler(view)
        addPrizesClaimedToRecycler(view)
    }

    private fun addPrizesSetToRecycler(view: View) {
        if (littleProfileViewModel.hasLoadedPrizesSet()) {
            attachPrizesSetAdapter(view)
        } else {
            val prizesSetQueryTask = littleProfileViewModel.getPrizesSetQuery()!!
            if (prizesSetQueryTask.isComplete) {
                handlePrizesSetQueryTask(prizesSetQueryTask, view)
            } else {
                prizesSetQueryTask.addOnCompleteListener {
                    handlePrizesSetQueryTask(prizesSetQueryTask, view)
                }
            }
        }
    }

    private fun addPrizesClaimedToRecycler(view: View) {
        if (littleProfileViewModel.hasLoadedPrizesClaimed()) {
            attachPrizesClaimedAdapter(view)
        } else {
            val prizesClaimedQueryTask = littleProfileViewModel.getPrizesClaimedQuery()!!
            if (prizesClaimedQueryTask.isComplete) {
                handlePrizesClaimedQueryTask(prizesClaimedQueryTask, view)
            } else {
                prizesClaimedQueryTask.addOnCompleteListener {
                    handlePrizesClaimedQueryTask(prizesClaimedQueryTask, view)
                }
            }
        }
    }

    /**
     * Gathers all the prizes set and sets up the recyclerview to place them in
     */
    private fun handlePrizesSetQueryTask(prizesSetQueryTask: Task<QuerySnapshot>, view: View) {
        littleProfileViewModel.compilePrizesSet(prizesSetQueryTask.result!!)
        littleProfileViewModel.createPrizesSetAdapter()
        attachPrizesSetAdapter(view)
    }

    /**
     * Gathers all the prizes claimed and sets up the recyclerview to place them in
     */
    private fun handlePrizesClaimedQueryTask(prizesClaimedQueryTask: Task<QuerySnapshot>, view: View) {
        littleProfileViewModel.compilePrizesClaimed(prizesClaimedQueryTask.result!!)
        littleProfileViewModel.createPrizesClaimedAdapter()
        attachPrizesClaimedAdapter(view)
    }

    private fun attachPrizesSetAdapter(view: View) {
        view.setPrizesRecyclerView.adapter = littleProfileViewModel.getPrizesSetAdapter()
        if (littleProfileViewModel.getPrizesSetAdapter().itemCount == 0) {
            view.no_prizes_set_image.visibility = View.INVISIBLE
        } else {
            view.no_prizes_set_image.visibility = View.VISIBLE
        }
    }

    private fun attachPrizesClaimedAdapter(view: View) {
        view.claimedPrizesFromYouRecyclerView.adapter = littleProfileViewModel.getPrizesClaimedAdapter()
        if (littleProfileViewModel.getPrizesClaimedAdapter().itemCount == 0) {
            view.no_prizes_claimed_from_you_image.visibility = View.INVISIBLE
        } else {
            view.no_prizes_claimed_from_you_image.visibility = View.VISIBLE
        }
    }

    /**
     * Loads the observe user's profile picture
     */
    private fun loadProfile() {
        val observedUser = littleProfileViewModel.observedUserInstance
        Glide.with(view!!).load(observedUser.profilePictureUri).into(view!!.findViewById(R.id.user_profile_picture))
        my_display_name_textView.text = observedUser.displayName
        bio_text_view.text = observedUser.bio
        coin_count.text = observedUser.coins.toString()
        bigs_count.text = observedUser.numOfBigs.toString()
        littles_count.text = observedUser.numOfLittles.toString()
    }

    /**
     * Attaches the on click listeners to all buttons
     */
    private fun setUpButtons() {
        give_coins_button.setOnClickListener {
            findNavController().navigate(R.id.action_littleProfileFragment_to_giveCoinsFragment, null)
        }
        remove_little_button.setOnClickListener {
            littleProfileViewModel.removeLittleAndSendBack(context!!)
        }
        set_prize_button.setOnClickListener {
            val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(openGallery, 1)
        }
    }

    /**
     * Catches the result of the intent that opens the gallery to select a profile picture image
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            littleProfileViewModel.handleGallerySelectionCompletion(requestCode, resultCode, data, context!!)
            openDialogue(context!!, context!!.applicationContext, data)
        }
    }

    /**
     * Open a dialogue for the user to set the title and price of the new prize
     */
    @SuppressLint("InflateParams")
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
            val title = view.prize_title.text.toString()
            val price = view.prize_price.text.toString()
            if (hasReachedSetPrizeLimit()) {
                Toast.makeText(context!!, "You've reached the prizes set limit!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else if (title == "" || price == "") {
                Toast.makeText(context!!, "Missing Fields", Toast.LENGTH_SHORT).show()
            } else if (price != "" && price[0] == '0') {
                Toast.makeText(context!!, "Price cannot be 0", Toast.LENGTH_SHORT).show()
            } else {
                uploadSetPrize(title, Integer.parseInt(price))
                dialog.dismiss()
            }
        }
    }

    private fun uploadSetPrize(prizeTitle: String, prizePrice: Int) {
        val prizeId = littleProfileViewModel.generateId()
        val prizePath = littleProfileViewModel.generatePrizePath(prizeId)
        littleProfileViewModel.insertPrizeImageToStorage(prizePath).addOnCompleteListener {
            littleProfileViewModel.downloadImageUri(prizePath).addOnCompleteListener {
                uri -> saveSetPrizeToFirestore(Prize(prizeTitle, prizePrice, uri.toString(), prizeId))
            }
        }
    }

    private fun saveSetPrizeToFirestore(prize: Prize) {
        Toast.makeText(context!!, "Uploading prize...", Toast.LENGTH_SHORT).show()
        littleProfileViewModel.savePrizeInFireStore(prize).addOnCompleteListener {
            Toast.makeText(context, "Prize Set!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Determines whether the user has set a number of prizes equal to the limit of how many one
     * can set.
     */
    private fun hasReachedSetPrizeLimit(): Boolean {
        return (setPrizesRecyclerView.adapter as PrizesRecyclerViewAdapter).itemCount == 10
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

}