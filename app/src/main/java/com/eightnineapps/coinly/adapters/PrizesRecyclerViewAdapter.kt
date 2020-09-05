package com.eightnineapps.coinly.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.objects.Prize
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.enums.PrizeTapLocation
import com.eightnineapps.coinly.models.Firestore
import kotlinx.android.synthetic.main.claim_prize_dialogue_layout.view.*
import kotlinx.android.synthetic.main.fragment_big_profile.*
import kotlinx.android.synthetic.main.prize_info_dialogue_layout.view.*
import kotlinx.android.synthetic.main.prize_list_view_layout.view.prize_picture
import kotlinx.android.synthetic.main.set_new_prize_dialogue_layout.view.cancel_button

class PrizesRecyclerViewAdapter(_items: List<Prize>, _context: Context, _prizeTapLocation: PrizeTapLocation, _currentUser: User, _observedUser: User): RecyclerView.Adapter<PrizesRecyclerViewAdapter.ViewHolder>() {

    private var prizeList = _items.toMutableList()
    private var prizeTapLocation = _prizeTapLocation
    private var recyclerView: RecyclerView? = null
    private var currentUser = _currentUser
    private var observedUser = _observedUser
    var context = _context

    class ViewHolder(_view: View, _items: List<Prize>, _prizeTapLocation: PrizeTapLocation, _recyclerView: RecyclerView, _currentUser: User, _observedUser: User): RecyclerView.ViewHolder(_view), View.OnClickListener {

        private var context: Context = _view.context
        private var prizeTapLocation = _prizeTapLocation
        private var viewHolderPrizeList = _items
        private var recyclerView = _recyclerView
        private var currentUser = _currentUser
        private var observedUser = _observedUser

        val singlePrizePictureImageView: ImageView = _view.prize_picture

        init {
            _view.isClickable = true
            _view.setOnClickListener(this)
        }

        /**
         * Start a shared element transition to an AlertDialog where the user can confirm actions
         * they want to take on a prize
         */
        override fun onClick(v: View?) {
            when (prizeTapLocation) {
                PrizeTapLocation.BIG_PRIZES_SET -> openDialogueToClaimPrize(context, viewHolderPrizeList[recyclerView.getChildLayoutPosition(v!!)])
                PrizeTapLocation.BIG_PRIZES_CLAIMED -> openDialogueToShowPrizeInfo(context, viewHolderPrizeList[recyclerView.getChildLayoutPosition(v!!)])
                PrizeTapLocation.LITTLE_PRIZES_SET -> openDialogueToShowPrizeInfo(context, viewHolderPrizeList[recyclerView.getChildLayoutPosition(v!!)])
                else -> openDialogueToShowPrizeInfo(context, viewHolderPrizeList[recyclerView.getChildLayoutPosition(v!!)])
            }
        }

        /**
         * Open a dialogue to show the prize title and price, with no other functionality
         */
        @SuppressLint("InflateParams")
        private fun openDialogueToShowPrizeInfo(context: Context, prize: Prize) {
            val builder = AlertDialog.Builder(context)
            val view = (context as Activity).layoutInflater.inflate(R.layout.prize_info_dialogue_layout, null)
            Glide.with(context.applicationContext).load(prize.uri).into(view.prize_picture)
            builder.setView(view)
            val dialog = builder.create()
            view.cancel_button.setOnClickListener { dialog.cancel() }
            view.prize_info_name.text = prize.name
            view.prize_info_price.text = prize.price.toString()
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            dialog.window!!.attributes = setDialogDimensions(dialog)
        }

        /**
         * Open a dialogue for the user to set the title and price of the new prize
         */
        @SuppressLint("InflateParams")
        private fun openDialogueToClaimPrize(context: Context, prize: Prize) {
            val builder = AlertDialog.Builder(context)
            val view = (context as Activity).layoutInflater.inflate(R.layout.claim_prize_dialogue_layout, null)
            Glide.with(context.applicationContext).load(prize.uri).into(view.prize_picture)
            builder.setView(view)
            val dialog = builder.create()
            setUpDialogButtons(view, dialog, prize)
            view.claimed_prize_title.text = prize.name
            view.claimed_prize_price.text = prize.price.toString()
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            dialog.window!!.attributes = setDialogDimensions(dialog)
        }

        /**
         * Sets the actions the buttons in the set new prize dialog will do
         */
        private fun setUpDialogButtons(view: View, dialog: AlertDialog, prize: Prize) {
            view.claim_button.setOnClickListener {
                if (currentUser.coins >= prize.price) {
                    claimPrize(prize)
                    //TODO: Subtract coints
                    dialog.cancel()
                } else {
                    Toast.makeText(context, "Not enough coins!", Toast.LENGTH_SHORT).show()
                }
            }
            view.cancel_claim_button.setOnClickListener {
                dialog.cancel()
            }
        }

        /**
         * Places a prize document in the little's claimed list and deletes it from the big's set list
         */
        private fun claimPrize(prize: Prize) {
            Firestore.claimNewPrize(currentUser.email!!, observedUser.email!!, prize).addOnSuccessListener {
                Firestore.deletePrize(currentUser.email!!, observedUser.email!!, prize.id).addOnSuccessListener {
                    Toast.makeText(context, "Congratulations! You claimed prize!", Toast.LENGTH_SHORT).show()
                    (context as Activity).no_prizes_claimed_image.visibility = View.INVISIBLE
                    ((context as Activity).prizesYouveClaimedRecyclerView.adapter as PrizesRecyclerViewAdapter).addItem(prize)
                    //Now you just need to call THIS adapter's remove method to remove the claimed item from the set prizes list
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

    }

    /**
     * Inflates each row of the recycler view with the proper layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater
            .from(context)
            .inflate(R.layout.prize_list_view_layout, parent, false), prizeList, prizeTapLocation, recyclerView!!, currentUser, observedUser)
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
        Glide.with(context).load(currentPrize.uri).into(holder.singlePrizePictureImageView)
    }

    /**
     * Grabs an instance of the attached recycler view
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    fun removeItem(id: String) {
        prizeList.remove(prizeList.first { it.id == id })
        notifyDataSetChanged()
    }

    fun addItem(prize: Prize) {
        prizeList.add(prize)
        notifyDataSetChanged()
    }
}