package com.eightnineapps.coinly.classes.dialogues

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.eightnineapps.coinly.R

class SetNewPrizeDialogue: AppCompatDialogFragment() {

    private lateinit var prizeName: EditText
    private lateinit var prizePrice: EditText
    private lateinit var prizePicture: ImageView

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity!!)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.set_new_prize_dialogue_layout, null)
        builder.setView(view).setTitle("Set New Prize")
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .setPositiveButton("Set") { dialog, _ -> dialog.cancel() }

        prizeName = view.findViewById(R.id.prize_title)
        prizePrice = view.findViewById(R.id.prize_price)
        prizePicture = view.findViewById(R.id.new_prize_picture)

        return builder.create()
    }
}