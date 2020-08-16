package com.eightnineapps.coinly.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
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
import kotlinx.android.synthetic.main.claim_prize_dialogue_layout.view.*
import kotlinx.android.synthetic.main.prize_list_view_layout.view.*
import kotlinx.android.synthetic.main.prize_list_view_layout.view.prize_picture
import kotlinx.android.synthetic.main.set_new_prize_dialogue_layout.view.*

class PrizesRecyclerViewAdapter(_items: List<Prize>, _context: Context, _claimable: Boolean): RecyclerView.Adapter<PrizesRecyclerViewAdapter.ViewHolder>() {

    private var prizeList = _items
    private var claimablePrizes = _claimable
    private var recyclerView: RecyclerView? = null
    var context = _context

    class ViewHolder(_view: View, _items: List<Prize>, _claimable: Boolean, _recyclerView: RecyclerView): RecyclerView.ViewHolder(_view), View.OnClickListener {

        private var context: Context = _view.context
        private var claimablePrizes = _claimable
        private var viewHolderPrizeList = _items
        private var recyclerView = _recyclerView

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
            if (claimablePrizes) {
                val pos = recyclerView.getChildLayoutPosition(v!!)
                Log.d("INFO", pos.toString())
                openDialogue(context, context.applicationContext, viewHolderPrizeList[pos])
            }
        }

        /**
         * Open a dialogue for the user to set the title and price of the new prize
         */
        @SuppressLint("InflateParams")
        private fun openDialogue(context: Context, appContext: Context, prize: Prize) {
            val builder = AlertDialog.Builder(context)
            val view = (context as Activity).layoutInflater.inflate(R.layout.claim_prize_dialogue_layout, null)
            Glide.with(appContext).load(prize.uri).into(view.prize_picture)
            builder.setView(view)
            val dialog = builder.create()
            setUpDialogButtons(view, dialog)
            view.claimed_prize_title.text = prize.name
            view.claimed_prize_price.text = prize.price.toString()
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
            dialog.window!!.attributes = setDialogDimensions(dialog)
        }

        /**
         * Sets the actions the buttons in the set new prize dialog will do
         */
        private fun setUpDialogButtons(view: View, dialog: AlertDialog) {
            view.claim_button.setOnClickListener {

            }
            view.cancel_claim_button.setOnClickListener {
                dialog.cancel()
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
            .inflate(R.layout.prize_list_view_layout, parent, false), prizeList, claimablePrizes, recyclerView!!)
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
}