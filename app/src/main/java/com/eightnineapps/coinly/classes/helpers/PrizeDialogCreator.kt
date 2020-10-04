package com.eightnineapps.coinly.classes.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.classes.objects.Prize
import kotlinx.android.synthetic.main.prize_info_dialogue_layout.view.*
import kotlinx.android.synthetic.main.prize_list_view_layout.view.prize_picture

class PrizeDialogCreator {

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
    fun createAlertDialog(prize: Prize, view: View, layoutResource: Int): AlertDialog {
        val builder = AlertDialog.Builder(view.context)
        val dialogueView = createViewForAlertDialogue(prize, view, layoutResource)
        builder.setView(dialogueView)
        return builder.create()
    }

    /**
     * Creates the view to go in the Alert Dialog
     */
    @SuppressLint("InflateParams")
    private fun createViewForAlertDialogue(prize: Prize, view: View, layoutResource: Int): View {
        val dialogueView = (view.context as Activity).layoutInflater.inflate(
            layoutResource, null)
        setPrizeContent(dialogueView, prize)
        return dialogueView
    }

    /**
     * Sets the content of the notification dialogue when it's tapped from the recycler view
     */
    private fun setPrizeContent(dialogueView: View, prize: Prize) {
        dialogueView.prize_info_name.text = prize.name
        dialogueView.prize_info_price.text = prize.price.toString()
        Glide.with(dialogueView.context.applicationContext).load(prize.uri).into(dialogueView.prize_picture)
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