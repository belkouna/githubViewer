package com.example.githubviewer.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubviewer.repositories.GithubRepository

class RepositoryInfoViewModelProviderFactory(
    val githubRepository: GithubRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RepositoryInfoViewModel(githubRepository) as T
    }
}