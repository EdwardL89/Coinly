package com.eightnineapps.coinly.classes.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.objects.Notification
import com.eightnineapps.coinly.enums.NotificationType
import kotlinx.android.synthetic.main.notification_dialogue_layout.view.*

class DialogCreator {

    /**
     * Displays the dialog on the screen and sets its background and color
     */
    fun showDialogue(dialog: AlertDialog) {
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        dialog.window!!.attributes = setDialogDimensions(dialog)
    }

    /**
     * Creates the alert dialogue
     */
    fun createAlertDialogue(notification: Notification, view: View): AlertDialog {
        val builder = AlertDialog.Builder(view.context)
        val dialogueView = createViewForAlertDialogue(notification, view)
        builder.setView(dialogueView)
        return builder.create()
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
     * Creates the view to go in the Alert Dialog
     */
    @SuppressLint("InflateParams")
    private fun createViewForAlertDialogue(notification: Notification, view: View): View {
        val dialogueView = (view.context as Activity).layoutInflater.inflate(R.layout.notification_dialogue_layout, null)
        setNotificationContent(dialogueView, notification)
        return dialogueView
    }

    /**
     * Sets the content of the notification dialogue when it's tapped from the recycler view
     */
    private fun setNotificationContent(dialogueView: View, notification: Notification) {
        if (notification.type == NotificationType.GIVING_COINS) {
            showContentForGivingCoinsNotification(dialogueView, notification)
        } else {
            dialogueView.notification_content.text = notification.moreInformation
        }
    }

    /**
     * Set content specifically for a "giving coins" notification
     */
    private fun showContentForGivingCoinsNotification(dialogueView: View, notification: Notification) {
        if (notification.moreInformation != "") {
            notification.message += "\n\n Your Big left a note:\n${notification.moreInformation}"
        }
        dialogueView.notification_content.text = notification.message
    }
}