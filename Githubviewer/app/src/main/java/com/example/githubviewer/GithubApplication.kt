package com.example.githubviewer

import android.app.Application
import com.example.githubviewer.repositories.GithubRepository
import com.example.githubviewer.ui.RepositoryInfoViewModelProviderFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider

class GithubApplication : Application() ,KodeinAware{
    override val kodein = Kodein.lazy {
        import(androidXModule(this@GithubApplication))

        bind() from provider { RepositoryInfoViewModelProviderFactory(instance()) }
        bind() from provider {GithubRepository()}
    }
}