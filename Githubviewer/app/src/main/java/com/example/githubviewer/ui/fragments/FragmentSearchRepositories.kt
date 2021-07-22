package com.example.githubviewer.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubviewer.R
import com.example.githubviewer.adapters.RepositoriesAdapter
import com.example.githubviewer.ui.RepositoriesActivity
import com.example.githubviewer.ui.RepositoriesViewModel
import com.example.githubviewer.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.example.githubviewer.utils.Constants.Companion.SEARCH_REPOSITORIES_DELAY
import com.example.githubviewer.utils.Resource
import kotlinx.android.synthetic.main.fragment_repositories_list.*
import kotlinx.android.synthetic.main.fragment_search_repositories.*
import kotlinx.android.synthetic.main.fragment_search_repositories.paginationProgressBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentSearchRepositories :Fragment(R.layout.fragment_search_repositories){

    lateinit var viewModel: RepositoriesViewModel
    lateinit var repositoriesAdapter: RepositoriesAdapter

    val TAG = "ReposSearchFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        viewModel=(activity as RepositoriesActivity).viewModel
        setupRecyclerView()

        var job: Job?=null
        etSearch.addTextChangedListener{editable->
            job?.cancel()
            job= MainScope().launch {
                delay(SEARCH_REPOSITORIES_DELAY)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        viewModel.searchRepositories(editable.toString())
                    }
                }
            }
        }

        viewModel.searchedRepositories.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { repositoriesResponse ->
                        repositoriesAdapter.differ.submitList(repositoriesResponse.items.toList())
                        val totalPages = repositoriesResponse.total_count / QUERY_PAGE_SIZE+2
                        isLastPage = viewModel.searchedRepositoriesPageNumber == totalPages
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

    }
    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setupRecyclerView() {
        repositoriesAdapter = RepositoriesAdapter()
        rvSearchRepositories.apply {
            adapter = repositoriesAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@FragmentSearchRepositories.scrollListener)
        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                viewModel.searchRepositories(etSearch.text.toString())
                isScrolling = false
            } else {
                rvSearchRepositories.setPadding(0, 0, 0, 0)
            }


        }
    }
}