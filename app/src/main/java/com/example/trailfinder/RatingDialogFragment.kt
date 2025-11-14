package com.example.trailfinder

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RatingDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_TRAIL_ID = "trailId"

        fun newInstance(trailId: Int): RatingDialogFragment {
            return RatingDialogFragment().apply {
                arguments = Bundle().apply { putInt(ARG_TRAIL_ID, trailId) }
            }
        }
    }

    private var onDismissListener: (() -> Unit)? = null
    fun setOnDismissListener(listener: () -> Unit) {
        onDismissListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ctx = requireContext()
        val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_rate_trail, null)

        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val tagScenic = view.findViewById<CheckBox>(R.id.tagScenic)
        val tagSteep = view.findViewById<CheckBox>(R.id.tagSteep)
        val tagCrowded = view.findViewById<CheckBox>(R.id.tagCrowded)
        val editComment = view.findViewById<EditText>(R.id.editComment)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)

        val dialog = AlertDialog.Builder(ctx)
            .setView(view)
            .create()

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnSubmit.setOnClickListener {
            val stars = ratingBar.rating.toInt().coerceIn(1, 5)
            val tags = buildList {
                if (tagScenic.isChecked) add("Scenic")
                if (tagSteep.isChecked) add("Steep")
                if (tagCrowded.isChecked) add("Crowded")
            }.joinToString(",").ifBlank { null }

            val rating = RatingEntity(
                trailId = arguments?.getInt(ARG_TRAIL_ID) ?: -1,
                stars = stars,
                tags = tags,
                comment = editComment.text.toString().ifBlank { null }
            )

            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(requireContext())

                withContext(Dispatchers.IO) {
                    db.ratingDao().insertRating(rating)
                }

                // ✅ log AFTER the real insert
                android.util.Log.d("DB_TEST", "Inserted rating (real Room insert): $rating")

                dialog.dismiss() // triggers onDismiss
            }
        }

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }
}