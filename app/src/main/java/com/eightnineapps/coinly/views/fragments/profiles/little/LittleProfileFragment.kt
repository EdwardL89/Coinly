package com.eightnineapps.coinly.views.fragments.profiles.little

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.helpers.PrizeDialogCreator
import com.eightnineapps.coinly.classes.objects.Prize
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.LittleProfileViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_little_profile.*
import kotlinx.android.synthetic.main.fragment_little_profile.view.*
import kotlinx.android.synthetic.main.set_new_prize_dialogue_layout.*

class LittleProfileFragment: Fragment() {

    private val littleProfileViewModel: LittleProfileViewModel by activityViewModels()
    private val dialogCreator = PrizeDialogCreator()

    /**
     * Overrides the onCreate method to allow the fragments to have an options menu and starts the
     * queries
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        littleProfileViewModel.observedUserInstance = activity!!.intent.getSerializableExtra("observed_user") as User
        littleProfileViewModel.startQueryForPrizesSet()
        littleProfileViewModel.startQueryForPrizesClaimed()
    }

    /**
     * Inflates the little's profile
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_little_profile, container, false)
    }

    /**
     * Begin loading the littles's profile information
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfile()
        setUpButtons()
        addPrizesSetToRecycler(view)
        addPrizesClaimedToRecycler(view)
    }

    /**
     * Makes sure the query task is completed before continuing
     */
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

    /**
     * Makes sure the query task is completed before continuing
     */
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

    /**
     * Attaches the adapter to the prizes set recycler view and updates UI if it's empty
     */
    private fun attachPrizesSetAdapter(view: View) {
        view.setPrizesRecyclerView.adapter = littleProfileViewModel.getPrizesSetAdapter()
        if (littleProfileViewModel.getPrizesSetAdapter().itemCount == 0) {
            view.no_prizes_set_image.visibility = View.VISIBLE
        } else {
            view.no_prizes_set_image.visibility = View.INVISIBLE
        }
    }

    /**
     * Attaches the adapter to the prizes claimed recycler view and updates UI if it's empty
     */
    private fun attachPrizesClaimedAdapter(view: View) {
        view.claimedPrizesFromYouRecyclerView.adapter = littleProfileViewModel.getPrizesClaimedAdapter()
        if (littleProfileViewModel.getPrizesClaimedAdapter().itemCount == 0) {
            view.no_prizes_claimed_from_you_image.visibility = View.VISIBLE
        } else {
            view.no_prizes_claimed_from_you_image.visibility = View.INVISIBLE
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
            littleProfileViewModel.removeLittleAndSendBack()
            Toast.makeText(context, "Removed " +
                    "${littleProfileViewModel.observedUserInstance.displayName} as a little", Toast.LENGTH_SHORT).show()
            (context as Activity).finish()
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
            openDialogue(data)
        }
    }

    /**
     * Open a dialogue for the user to set the title and price of the new prize
     */
    @SuppressLint("InflateParams")
    private fun openDialogue(data: Intent?) {
        dialogCreator.setImageDataForDialog(data)
        val dialog = dialogCreator.createAlertDialog(null, context!!, R.layout.set_new_prize_dialogue_layout)
        dialogCreator.showDialog(dialog)
        setUpDialogButtons(dialog)
    }

    /**
     * Sets the actions the buttons in the set new prize dialog will do
     */
    private fun setUpDialogButtons(dialog: AlertDialog) {
        dialog.cancel_set_new_prize_button.setOnClickListener {
            dialog.cancel()
        }
        dialog.set_button.setOnClickListener {
            val title = dialog.prize_title.text.toString()
            val price = dialog.prize_price.text.toString()
            if (verifyPrizeBeforeSetting(title, price, dialog)) {
                no_prizes_set_image.visibility = View.INVISIBLE
                uploadSetPrize(title, Integer.parseInt(price))
                dialog.dismiss()
            }
        }
    }

    /**
     * Verifies that the info entered by the user for the new set prize is valid
     */
    private fun verifyPrizeBeforeSetting(title: String, price: String, dialog: AlertDialog): Boolean {
        if (hasReachedSetPrizeLimit()) {
            Toast.makeText(context!!, "You've reached the prizes set limit!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        } else if (title == "" || price == "") {
            Toast.makeText(context!!, "Missing Fields", Toast.LENGTH_SHORT).show()
        } else if (price != "" && price[0] == '0') {
            Toast.makeText(context!!, "Price cannot be 0", Toast.LENGTH_SHORT).show()
        } else {
            return true
        }
        return false
    }

    /**
     * Starts the process of saving the set prize to the firestore and storage
     */
    private fun uploadSetPrize(prizeTitle: String, prizePrice: Int) {
        Toast.makeText(context!!, "Uploading prize...", Toast.LENGTH_SHORT).show()
        val prizeId = littleProfileViewModel.generateId()
        val prizePath = littleProfileViewModel.generatePrizePath(prizeId)
        littleProfileViewModel.insertPrizeImageToStorage(prizePath).addOnCompleteListener {
            littleProfileViewModel.downloadImageUri(prizePath).addOnCompleteListener {
                uri -> saveSetPrizeToFirestore(Prize(prizeTitle, prizePrice, uri.result!!.toString(), prizeId))
            }
        }
    }

    /**
     * Saves the recently set prize to the firestore
     */
    private fun saveSetPrizeToFirestore(prize: Prize) {
        littleProfileViewModel.savePrizeInFireStore(prize).addOnCompleteListener {
            littleProfileViewModel.addSetPrizeToRecycler(prize)
            Toast.makeText(context, "Prize Set!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Determines whether the user has set a number of prizes equal to the limit of how many one
     * can set.
     */
    private fun hasReachedSetPrizeLimit(): Boolean {
        return setPrizesRecyclerView.adapter!!.itemCount == 10
    }
}