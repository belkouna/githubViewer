package com.example.githubviewer.utils

import android.content.Context
import android.widget.Toast

class Extensions {
    fun Context.toastS(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun Context.toastL(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}