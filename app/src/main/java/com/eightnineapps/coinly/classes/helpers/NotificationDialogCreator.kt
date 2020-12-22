package com.eightnineapps.coinly.classes.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.objects.Notification
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.enums.NotificationType
import kotlinx.android.synthetic.main.dialog_confirm_removal.view.*
import kotlinx.android.synthetic.main.dialog_notification_layout.view.*

class NotificationDialogCreator {

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
    fun createAlertDialog(notification: Notification, view: View): AlertDialog {
        val builder = AlertDialog.Builder(view.context)
        val dialogueView = createViewForAlertDialogue(notification, view)
        builder.setView(dialogueView)
        return builder.create()
    }

    /**
     * Creates an alert dialogue to confirm removal of a big or little
     */
    fun createConfirmationDialog(user: User, context: Context): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val dialogueView = createViewForConfirmationDialog(user, context)
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
     * Inflate the view for the confirmation dialog
     */
    @SuppressLint("InflateParams")
    private fun createViewForConfirmationDialog(user: User, context: Context): View {
        val dialogueView = (context as Activity).layoutInflater.inflate(R.layout.dialog_confirm_removal, null)
        setRemovalDialogContent(dialogueView, user)
        return dialogueView
    }

    /**
     * Set the image and text content of the confirmation dialog
     */
    private fun setRemovalDialogContent(view: View, user: User) {
        Glide.with(view.context).load(user.profilePictureUri).into(view.profile_picture)
        view.confirmation_text_view.text =
            String.format(view.context.getString(R.string.confirmation_text), user.displayName)
    }

    /**
     * Inflate the view to go in the Alert Dialog
     */
    @SuppressLint("InflateParams")
    private fun createViewForAlertDialogue(notification: Notification, view: View): View {
        val dialogueView = (view.context as Activity).layoutInflater.inflate(R.layout.dialog_notification_layout, null)
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