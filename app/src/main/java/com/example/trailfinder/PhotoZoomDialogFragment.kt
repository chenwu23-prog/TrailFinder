package com.example.trailfinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide

class PhotoZoomDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_URI = "image_uri"

        fun newInstance(uri: String): PhotoZoomDialogFragment {
            return PhotoZoomDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_URI, uri)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.dialog_photo_zoom, container, false)
        val imageView = root.findViewById<ImageView>(R.id.zoomImageView)

        val uri = requireArguments().getString(ARG_URI)!!
        Glide.with(this)
            .load(uri)
            .fitCenter()
            .into(imageView)

        // Tap anywhere to close
        root.setOnClickListener { dismiss() }

        return root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(ColorDrawable(0xCC000000.toInt()))
        }
    }
}