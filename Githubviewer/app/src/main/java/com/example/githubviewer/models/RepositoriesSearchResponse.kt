package com.example.githubviewer.models

data class RepositoriesSearchResponse(
    val incomplete_results: Boolean,
    val items: MutableList<Item>,
    val total_count: Int
)