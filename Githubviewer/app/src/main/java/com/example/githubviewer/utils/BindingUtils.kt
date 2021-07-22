package com.example.githubviewer.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@BindingAdapter("load_image")
fun loadImage(view: ImageView, url: String?) {
    if (url.isNullOrEmpty()) {
        // dummy img
        Glide.with(view)
            .load("https://github.githubassets.com/images/modules/open_graph/github-mark.png")
            .into(view)
    } else {
        Glide.with(view)
            .load(url)
            .into(view)
    }
}
