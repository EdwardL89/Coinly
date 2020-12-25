package com.eightnineapps.coinly.classes.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.objects.Prize
import kotlinx.android.synthetic.main.dialog_claim_prize_layout.view.*
import kotlinx.android.synthetic.main.dialog_prize_info_layout.view.*
import kotlinx.android.synthetic.main.dialog_prize_info_layout.view.prize_info_name
import kotlinx.android.synthetic.main.prize_list_view_layout.view.prize_picture
import kotlinx.android.synthetic.main.dialog_set_new_prize_layout.view.*

class PrizeDialogCreator {

    private var imageDataFromIntent: Intent? = null

    /**
     * Displays the dialog on the screen and sets its background and color
     */
    fun showDialog(dialog: AlertDialog) {
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = setDialogDimensions(dialog)
    }

    /**
     * Creates the alert dialogue
     */
    fun createAlertDialog(prize: Prize?, context: Context, layoutResource: Int): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val dialogueView = createViewForAlertDialogue(prize, context, layoutResource)
        builder.setView(dialogueView)
        return builder.create()
    }

    /**
     * Creates the view to go in the Alert Dialog
     */
    @SuppressLint("InflateParams")
    private fun createViewForAlertDialogue(prize: Prize?, context: Context, layoutResource: Int): View {
        val dialogueView = (context as Activity).layoutInflater.inflate(layoutResource, null)
        when (layoutResource) {
            R.layout.dialog_prize_info_layout -> setPrizeInfoContent(dialogueView, prize!!)
            R.layout.dialog_set_new_prize_layout -> setNewPrizeInfo(dialogueView)
            else -> setClaimPrizeContent(dialogueView, prize!!)
        }
        return dialogueView
    }

    /**
     * Lets the user enter the new prize's title and price
     */
    private fun setNewPrizeInfo(dialogView: View) {
        Glide.with(dialogView.context.applicationContext).load(imageDataFromIntent!!.data).into(dialogView.new_prize_picture)
    }

    /**
     * Sets the content of the prize that can be claimed dialogue when it's tapped from the recycler view
     */
    private fun setClaimPrizeContent(dialogView: View, prize: Prize) {
        dialogView.claimed_prize_title.text = prize.name
        dialogView.claimed_prize_price.text = String.format(dialogView.context.getString(R.string.price_with_coins), prize.price)
        Glide.with(dialogView.context.applicationContext).load(prize.uri).into(dialogView.prize_picture)
    }

    /**
     * Sets the content of the set prize dialogue when it's tapped from the recycler view
     */
    private fun setPrizeInfoContent(dialogView: View, prize: Prize) {
        dialogView.prize_info_name.text = prize.name
        dialogView.prize_info_price.text = String.format(dialogView.context.getString(R.string.price_with_coins), prize.price)
        Glide.with(dialogView.context.applicationContext).load(prize.uri).into(dialogView.prize_picture)
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
     * Saves the data of the image selected from the gallery intent for setting a new prize
     */
    fun setImageDataForDialog(data: Intent?) {
        imageDataFromIntent = data
    }
}