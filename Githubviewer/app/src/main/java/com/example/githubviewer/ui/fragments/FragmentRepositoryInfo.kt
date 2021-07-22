package com.example.githubviewer.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubviewer.R
import com.example.githubviewer.adapters.ContributorsAdapter
import com.example.githubviewer.adapters.RepositoriesAdapter
import com.example.githubviewer.databinding.FragmentRepositoryInfoBinding
import com.example.githubviewer.models.Contributor
import com.example.githubviewer.ui.RepositoriesActivity
import com.example.githubviewer.ui.RepositoryInfoViewModel
import com.example.githubviewer.utils.Resource
import kotlinx.android.synthetic.main.fragment_repository_info.*
import com.example.githubviewer.ui.RepositoryInfoViewModelProviderFactory
import kotlinx.android.synthetic.main.fragment_repositories_list.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class FragmentRepositoryInfo:Fragment(), KodeinAware {
    lateinit var viewModel: RepositoryInfoViewModel
    lateinit var contributorsAdapter:ContributorsAdapter
    override val kodein by kodein()
    private val factory: RepositoryInfoViewModelProviderFactory by instance()

    // Navigation
    private val args: FragmentRepositoryInfoArgs by navArgs() //NOTE: invalidated cache and restarted, then make project to generate FragmentRepositoryInfoArgs
    private lateinit var binding: FragmentRepositoryInfoBinding
    private var ownerLogin: String = ""
    private var repoName: String = ""

    private fun setupRecyclerView() {
        contributorsAdapter = ContributorsAdapter()
        recyclerViewContributors.apply {
            adapter = contributorsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        viewModel=(activity as RepositoriesActivity).repositoryInfoViewModel
        setupRecyclerView()
        viewModel.repo.observe(viewLifecycleOwner, Observer {
            response->
            response.data?.let {
                repositoryInfo ->
                contributorsAdapter.differ.submitList(repositoryInfo.contributors.toList())
            }
        })

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ownerLogin = args.repositoryOwner
        repoName = args.repositoryName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_repository_info, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(RepositoryInfoViewModel::class.java)

        binding.apply {
            lifecycleOwner = this@FragmentRepositoryInfo
            vm = viewModel
        }

        viewModel.getRepo( ownerLogin, repoName)

        initObserver()
    }


    // Observe data & update UI
    private fun initObserver() {
        viewModel.repo.observe(viewLifecycleOwner, Observer { status ->
            when (status) {
                is Resource.Loading -> showProgressBar()
                is Resource.Success -> {
                    binding.repo = status.data
                    viewModel.isWatchersVisible.value = status.data!!.watchers != null
                    hideProgressBar()
                    bindProjectLink(status.data.html_url)
                    bindContributorsUrl(status.data.contributors_url)
                }
                is Resource.Error -> {
                    hideProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        detailsProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        detailsProgressBar.visibility = View.VISIBLE
    }

    private fun bindProjectLink(url: String) {
        project_link_container.setOnClickListener {
            val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
            defaultBrowser.data = Uri.parse(url)
            startActivity(defaultBrowser)
        }
    }
    private fun bindContributorsUrl(url: String) {
        contributors_url.setOnClickListener {
            val defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER)
            defaultBrowser.data = Uri.parse(url)
            startActivity(defaultBrowser)
        }
    }
}