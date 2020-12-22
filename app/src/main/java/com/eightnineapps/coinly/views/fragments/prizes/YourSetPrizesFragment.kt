package com.eightnineapps.coinly.views.fragments.prizes

import android.annotation.SuppressLint
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
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.helpers.PrizeDialogCreator
import com.eightnineapps.coinly.classes.objects.Prize
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.LittleProfileViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.dialog_set_new_prize_layout.*
import kotlinx.android.synthetic.main.fragment_your_set_prizes.*
import kotlinx.android.synthetic.main.fragment_your_set_prizes.view.*

class YourSetPrizesFragment: Fragment() {

    private val littleProfileViewModel: LittleProfileViewModel by activityViewModels()
    private val dialogCreator = PrizeDialogCreator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        littleProfileViewModel.startQueryForPrizesSet()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_your_set_prizes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addYourSetPrizesToRecycler(view)
        setupFloatingActionButton()
    }

    /**
     * Catches the result of the intent that opens the gallery to select a profile picture image
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            littleProfileViewModel.handleGallerySelectionCompletion(requestCode, resultCode, data, requireContext())
            openDialogue(data)
        }
    }

    /**
     * Open a dialogue for the user to set the title and price of the new prize
     */
    @SuppressLint("InflateParams")
    private fun openDialogue(data: Intent?) {
        dialogCreator.setImageDataForDialog(data)
        val dialog = dialogCreator.createAlertDialog(null, requireContext(), R.layout.dialog_set_new_prize_layout)
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
                no_set_prizes_image.visibility = View.INVISIBLE
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
            Toast.makeText(requireContext(), "You've reached the prizes set limit!", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        } else if (title == "" || price == "") {
            Toast.makeText(requireContext(), "Missing Fields", Toast.LENGTH_SHORT).show()
        } else if (price != "" && price[0] == '0') {
            Toast.makeText(requireContext(), "Price cannot be 0", Toast.LENGTH_SHORT).show()
        } else {
            return true
        }
        return false
    }

    /**
     * Starts the process of saving the set prize to the firestore and storage
     */
    private fun uploadSetPrize(prizeTitle: String, prizePrice: Int) {
        Toast.makeText(requireContext(), "Uploading prize...", Toast.LENGTH_SHORT).show()
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
        return yourSetPrizesRecyclerView.adapter!!.itemCount == 10
    }

    private fun setupFloatingActionButton() {
        set_new_prize_button.setOnClickListener {
            val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(openGallery, 1)
        }
    }

    /**
     * Makes sure the query task is completed before continuing
     */
    private fun addYourSetPrizesToRecycler(view: View) {
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
     * Attaches the adapter to the prizes set recycler view and updates UI if it's empty
     */
    private fun attachPrizesSetAdapter(view: View) {
        view.yourSetPrizesRecyclerView.adapter = littleProfileViewModel.getPrizesSetAdapter()
        if (littleProfileViewModel.getPrizesSetAdapter().itemCount == 0) {
            view.no_set_prizes_image.visibility = View.VISIBLE
        } else {
            view.no_set_prizes_image.visibility = View.INVISIBLE
        }
    }

    /**
     * Gathers all the prizes set and sets up the recyclerview to place them in
     */
    private fun handlePrizesSetQueryTask(prizesSetQueryTask: Task<QuerySnapshot>, view: View) {
        littleProfileViewModel.compilePrizesSet(prizesSetQueryTask.result!!)
        littleProfileViewModel.createPrizesSetAdapter(view)
        attachPrizesSetAdapter(view)
    }
}