package com.eightnineapps.coinly.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.helpers.PrizeDialogCreator
import com.eightnineapps.coinly.classes.objects.Prize
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.enums.PrizeTapLocation
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.eightnineapps.coinly.models.ImgStorage
import kotlinx.android.synthetic.main.dialog_claim_prize_layout.*
import kotlinx.android.synthetic.main.dialog_prize_info_layout.*
import kotlinx.android.synthetic.main.fragment_big_profile.*
import kotlinx.android.synthetic.main.fragment_prizes_to_claim.view.*
import kotlinx.android.synthetic.main.fragment_your_set_prizes.view.*
import kotlinx.android.synthetic.main.prize_list_view_layout.view.*

class PrizesRecyclerViewAdapter(_items: List<Prize>, _prizeTapLocation: PrizeTapLocation, _observedUser: User, view: View):
    RecyclerView.Adapter<PrizesRecyclerViewAdapter.ViewHolder>() {

    private val prizeList = _items.toMutableList()
    private val prizeTapLocation = _prizeTapLocation
    private var recyclerView: RecyclerView? = null
    private val observedUser = _observedUser
    private val fragmentView = view

    inner class ViewHolder(_view: View): RecyclerView.ViewHolder(_view), View.OnClickListener {

        val singlePrizePictureImageView: ImageView = _view.prize_picture
        val singlePrizePrice: TextView = _view.price
        private val dialogCreator = PrizeDialogCreator()
        private val context = _view.context

        init {
            _view.isClickable = true
            _view.setOnClickListener(this)
        }

        /**
         * Start a shared element transition to an AlertDialog where the user can confirm actions
         * they want to take on a prize
         */
        override fun onClick(v: View?) {
            val selectedPrize = prizeList[recyclerView!!.getChildLayoutPosition(v!!)]
            when (prizeTapLocation) {
                PrizeTapLocation.BIG_PRIZES_SET -> openDialogueToClaimPrize(selectedPrize, v)
                else -> openDialogueToShowPrizeInfo(selectedPrize, v)
            }
        }

        /**
         * Open a dialogue to show the prize title and price, with no other functionality
         */
        private fun openDialogueToShowPrizeInfo(prize: Prize, view: View) {
            val dialog = dialogCreator.createAlertDialog(prize, view.context, R.layout.dialog_prize_info_layout)
            dialogCreator.showDialog(dialog)
            if (prizeTapLocation == PrizeTapLocation.LITTLE_PRIZES_SET) setUpButtonForDeletion(prize.id, dialog)
            else dialog.cancel_button.setOnClickListener { dialog.cancel() }
        }

        /**
         * Sets up the negative button of the dialog to delete the selected set prize
         */
        private fun setUpButtonForDeletion(prizeId: String, dialog: AlertDialog) {
            dialog.cancel_button.text = context.getString(R.string.delete)
            dialog.cancel_button.setTextColor(ContextCompat.getColor(context, R.color.redDelete))
            dialog.cancel_button.setOnClickListener {
                removeItem(prizeId)
                deleteSetPrize(prizeId)
                dialog.cancel()
            }
        }

        /**
         * Deletes the given prize from the firestore and updates deletes its image from storage
         */
        private fun deleteSetPrize(prizeId: String) {
            Firestore.deletePrize(observedUser.email!!, CurrentUser.getEmail()!!, prizeId).addOnSuccessListener {
                Toast.makeText(context, "Prize deleted", Toast.LENGTH_SHORT).show()
                ImgStorage.delete("set_prizes/${CurrentUser.getEmail()!!}/${observedUser.email}/${prizeId}")
            }
        }

        /**
         * Open a dialogue for the user to set the title and price of the new prize
         */
        private fun openDialogueToClaimPrize(prize: Prize, view: View) {
            val dialog = dialogCreator.createAlertDialog(prize, view.context, R.layout.dialog_claim_prize_layout)
            dialogCreator.showDialog(dialog)
            setUpDialogButtons(dialog, prize)
        }

        /**
         * Sets the actions the buttons in the set new prize dialog will do
         */
        private fun setUpDialogButtons(dialog: AlertDialog, prize: Prize) {
            dialog.claim_button.setOnClickListener { setupClaimButton(prize, dialog) }
            dialog.cancel_claim_button.setOnClickListener { dialog.cancel() }
        }

        /**
         * Sets up the positive button of the dialog to claim the selected prize
         */
        private fun setupClaimButton(prize: Prize, dialog: AlertDialog) {
            if (CurrentUser.coins.value!! >= prize.price) claimPrize(prize)
            else Toast.makeText(context, "Not enough coins!", Toast.LENGTH_SHORT).show()
            dialog.cancel()
        }

        /**
         * Places a prize document in the little's claimed list and deletes it from the big's set list, and from it's spot in Firebase Storage.
         */
        private fun claimPrize(prize: Prize) {
            Firestore.claimNewPrize(CurrentUser.getEmail()!!, observedUser.email!!, prize).addOnSuccessListener {
                Firestore.deletePrize(CurrentUser.getEmail()!!, observedUser.email!!, prize.id).addOnSuccessListener {
                    relocateImageInStorage(prize.id)
                    spendCoins(prize.price)
                    updateUIAfterClaimingPrize(prize)
                    Toast.makeText(context, "Congratulations! You claimed a prize!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        /**
         * Relocates the prize's image location within Firebase storage from the set_prizes folder to the
         * claimed_prizes folder.
         */
        private fun relocateImageInStorage(prizeId: String) { //Copy first, then delete
            val prizePath = "${observedUser.email!!}/${CurrentUser.getEmail()!!}/$prizeId"
            ImgStorage.getReference("set_prizes/$prizePath").getBytes(5000000).addOnSuccessListener {
                ImgStorage.insert(it, "claimed_prizes/$prizePath").addOnSuccessListener {
                    ImgStorage.delete("set_prizes/$prizePath")
                }
            }
        }

        /**
         * Handles the coin transaction for when a user claims a prize
         */
        private fun spendCoins(price: Int) {
            observedUser.coins += price
            CurrentUser.subtractCoins(price)
            Firestore.update(CurrentUser.instance!!, "coins", CurrentUser.coins.value.toString())
            Firestore.update(observedUser, "coins", observedUser.coins.toString())
            updateClaimedStats(price)
            updateGivenStats(price)
            updateSpendingPower()
        }

        /**
         * Updates the spending power text at the bottom of the profile
         */
        private fun updateSpendingPower() {
            (context as Activity).spending_power.text = CurrentUser.coins.value.toString()
        }

        /**
         * Removes the empty recycler icon, removes the prize from the set prizes recycler, and
         * adds the claimed prize to the recycler
         */
        private fun updateUIAfterClaimingPrize(prize: Prize) {
            removeItem(prize.id)
        }

        /**
         * Calculates new values for the average price of prizes given and the number of prizes given.
         * Updates these values in the Firestore.
         */
        private fun updateGivenStats(prizePrice: Int) {
            val newTotalPrice = (observedUser.avgPriceOfPrizesGiven*observedUser.numOfPrizesGiven) + prizePrice
            observedUser.numOfPrizesGiven++
            val newAverage = newTotalPrice / observedUser.numOfPrizesGiven
            observedUser.avgPriceOfPrizesGiven = newAverage
            Firestore.update(observedUser, "avgPriceOfPrizesGiven", observedUser.avgPriceOfPrizesGiven.toString())
            Firestore.update(observedUser, "numOfPrizesGiven", observedUser.numOfPrizesGiven.toString())
        }

        /**
         * Calculates new values for the average price of prizes claimed and the number of prizes claimed.
         * Updates these values in the Firestore.
         */
        private fun updateClaimedStats(prizePrice: Int) {
            val newTotalPrice = (CurrentUser.avgPriceOfPrizesClaimed.value!!*CurrentUser.numOfPrizesClaimed.value!!) + prizePrice
            CurrentUser.incrementPrizesClaimed()
            val newAverage = newTotalPrice / CurrentUser.numOfPrizesClaimed.value!!
            CurrentUser.updateAveragePriceOfPrizesClaimed(newAverage)
            Firestore.update(CurrentUser.instance!!, "avgPriceOfPrizesClaimed", CurrentUser.avgPriceOfPrizesClaimed.value.toString())
            Firestore.update(CurrentUser.instance!!, "numOfPrizesClaimed", CurrentUser.numOfPrizesClaimed.value.toString())
        }
    }

    /**
     * Inflates each row of the recycler view with the proper layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater
            .from(parent.context)
            .inflate(R.layout.prize_list_view_layout, parent, false))
    }

    /**
     * Returns the number of prizes
     */
    override fun getItemCount(): Int {
        return prizeList.size
    }

    /**
     * Loads the prize picture uri into the ImageView
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPrize = prizeList[position]
        Glide.with(holder.itemView.context).load(currentPrize.uri).into(holder.singlePrizePictureImageView)
        holder.singlePrizePrice.text = String.format(holder.itemView.context.getString(R.string.coins_price), currentPrize.price)
    }

    /**
     * Grabs an instance of the attached recycler view
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    /**
     * Removes a prize from the recycler and updates the UI
     */
    fun removeItem(id: String) {
        prizeList.remove(prizeList.first { it.id == id })
        notifyDataSetChanged()
        updateUIForNoPrizesSet()
    }

    /**
     * Displays the "no prizes set" icon when the recycler is empty
     */
    private fun updateUIForNoPrizesSet() {
        if (prizeList.size == 0 && prizeTapLocation == PrizeTapLocation.LITTLE_PRIZES_SET) {
            fragmentView.no_set_prizes_image.visibility = View.VISIBLE
        } else if (prizeList.size == 0) {
            fragmentView.no_prizes_set_by_big_image.visibility = View.VISIBLE
        }
    }

    /**
     * Adds an item to the recycler
     */
    fun addItem(prize: Prize) {
        prizeList.add(prize)
        notifyDataSetChanged()
    }
}