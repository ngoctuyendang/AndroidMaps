package com.appdemo.androidmaps.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import androidx.fragment.app.DialogFragment
import com.appdemo.androidmaps.R
import com.appdemo.androidmaps.databinding.DialogMarksBinding


class EditMarker(private val listener: (String, String) -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val dialogView = DialogMarksBinding.inflate(inflater, null, false)
        val note = dialogView.note
        val userName = dialogView.userName

        val dialogAlert =
            AlertDialog.Builder(ContextThemeWrapper(activity, R.style.AlertDialog))
                .setTitle("Add your note")
                .setView(dialogView.root)
                .setPositiveButton("Ok", null)
                .setNegativeButton("Cancel", null)
                .create()

        userName.setOnFocusChangeListener { _, _ -> dialogView.nameError.visibility = View.GONE }

        note.setOnFocusChangeListener { _, _ -> dialogView.noteError.visibility = View.GONE }

        dialogAlert.setOnShowListener {
            val positive = dialogAlert.getButton(AlertDialog.BUTTON_POSITIVE)
            val negative = dialogAlert.getButton(AlertDialog.BUTTON_NEGATIVE)

            positive.setOnClickListener {
                if (userName.text.isEmpty()) {
                    userName.clearFocus()
                    dialogView.nameError.visibility = View.VISIBLE
                }
                if (note.text.isEmpty()) {
                    note.clearFocus()
                    dialogView.noteError.visibility = View.VISIBLE
                }
                if (userName.text.isNotEmpty() && note.text.isNotEmpty()) {
                    listener.invoke(userName.text.toString(), note.text.toString())
                    dialogAlert.dismiss()
                }
            }
            negative.setOnClickListener { dialogAlert.dismiss() }
        }
        return dialogAlert
    }
}