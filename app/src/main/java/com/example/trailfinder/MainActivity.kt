package com.example.trailfinder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = MaterialTextView(this).apply {
            text = getString(R.string.hello_world)
            textSize = 24f
            setPadding(50, 100, 50, 100)
        }

        setContentView(textView)
    }
}
