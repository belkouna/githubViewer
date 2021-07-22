package com.example.githubviewer.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.githubviewer.R
import com.example.githubviewer.adapters.RepositoriesAdapter
import com.example.githubviewer.ui.RepositoriesActivity
import com.example.githubviewer.ui.RepositoriesViewModel
import com.example.githubviewer.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.example.githubviewer.utils.Resource
import kotlinx.android.synthetic.main.fragment_repositories_list.*

class FragmentRepositoriesList :Fragment(R.layout.fragment_repositories_list){

    lateinit var viewModel: RepositoriesViewModel
    lateinit var repositoriesAdapter: RepositoriesAdapter

    val TAG = "ReposListFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        viewModel=(activity as RepositoriesActivity).viewModel
        setupRecyclerView()

        viewModel.allRepositories.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { repositoriesResponse ->
                        repositoriesAdapter .differ.submitList(repositoriesResponse.items.toList())
                        val totalPages = repositoriesResponse.total_count / QUERY_PAGE_SIZE+2
                        isLastPage=viewModel.allRepositoriesPageNumber==totalPages
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
        isLoading=false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading=true
    }

    private fun setupRecyclerView() {
        repositoriesAdapter = RepositoriesAdapter()
        rvRepositoriesList.apply {
            adapter = repositoriesAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@FragmentRepositoriesList.scrollListener)
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
                viewModel.getRepositories()
                isScrolling = false
            } else {
                rvRepositoriesList.setPadding(0, 0, 0, 0)
            }


        }
    }
}