package com.example.githubviewer.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.githubviewer.R
import com.example.githubviewer.repositories.GithubRepository
import kotlinx.android.synthetic.main.activity_repositories.*

//import androidx.navigation.fragment.findNavController

class RepositoriesActivity : AppCompatActivity() {

    lateinit var viewModel:RepositoriesViewModel
    lateinit var repositoryInfoViewModel: RepositoryInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repositories)

        val githubRepository=GithubRepository()
        val viewModelProviderFactory=RepositoriesViewModelProviderFactory(application, githubRepository)
        val repositoryInfoViewModelFactory=RepositoryInfoViewModelProviderFactory(githubRepository)

        viewModel=ViewModelProvider(this,viewModelProviderFactory).get(RepositoriesViewModel::class.java)
        repositoryInfoViewModel=ViewModelProvider(this,repositoryInfoViewModelFactory).get(RepositoryInfoViewModel::class.java)

        bottomNavigationView.setupWithNavController(repositoriesNavHostFragment.findNavController())
    }
}