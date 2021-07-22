package com.example.githubviewer.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubviewer.GithubApplication
import com.example.githubviewer.models.RepositoriesSearchResponse
import com.example.githubviewer.repositories.GithubRepository
import com.example.githubviewer.utils.Resource
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.Response

class RepositoriesViewModel(
        app: Application,
    val githubRepository: GithubRepository
) :AndroidViewModel(app){

    val allRepositories:MutableLiveData<Resource<RepositoriesSearchResponse>> = MutableLiveData()
    var allRepositoriesPageNumber=1
    var allRepositoriesResponse:RepositoriesSearchResponse?=null

    val searchedRepositories:MutableLiveData<Resource<RepositoriesSearchResponse>> = MutableLiveData()
    var searchedRepositoriesPageNumber=1
    var searchedRepositoriesResponse:RepositoriesSearchResponse?=null

    var newSearchQuery:String? = null
    var oldSearchQuery:String? = null

    init {
        getRepositories()
    }

    fun getRepositories()=viewModelScope.launch {
       safeGetAllRepositoriesCall()
    }

    fun searchRepositories(keyword:String)=viewModelScope.launch {
       safeSearchRepositoriesCall(keyword)
    }

    private fun handleAllRepositoriesResponse(response: Response<RepositoriesSearchResponse>):Resource<RepositoriesSearchResponse>{
        if(response.isSuccessful){
            response.body()?.let {
                resultResponse->
                allRepositoriesPageNumber++
                if(allRepositoriesResponse==null){
                    allRepositoriesResponse=resultResponse
                }
                else{
                    val oldRepositories=allRepositoriesResponse?.items
                    val newRepositories=resultResponse.items
                    oldRepositories?.addAll(newRepositories)
                }
                return Resource.Success(allRepositoriesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
    private fun handleSearchRepositoriesResponse(response: Response<RepositoriesSearchResponse>):Resource<RepositoriesSearchResponse>{
        if(response.isSuccessful){
            response.body()?.let {
                    resultResponse->
                if(searchedRepositoriesResponse==null || newSearchQuery != oldSearchQuery){
                    searchedRepositoriesPageNumber = 1
                    oldSearchQuery = newSearchQuery
                    searchedRepositoriesResponse=resultResponse
                }
                else{
                    searchedRepositoriesPageNumber++
                    val oldRepositories=searchedRepositoriesResponse?.items
                    val newRepositories=resultResponse.items
                    oldRepositories?.addAll(newRepositories)
                }
                return Resource.Success(searchedRepositoriesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


    private suspend fun safeSearchRepositoriesCall(searchQuery: String) {
        newSearchQuery = searchQuery
        searchedRepositories.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = githubRepository.searchKotlinRepositories(searchedRepositoriesPageNumber,20,searchQuery)
                searchedRepositories.postValue(handleSearchRepositoriesResponse(response))
            } else {
                searchedRepositories.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> searchedRepositories.postValue(Resource.Error("Network Failure"))
                else -> searchedRepositories.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private suspend fun safeGetAllRepositoriesCall() {
        allRepositories.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = githubRepository.getAllKotlinRepositories(allRepositoriesPageNumber,20)
                allRepositories.postValue(handleAllRepositoriesResponse(response))
            } else {
                allRepositories.postValue(Resource.Error("No internet connection"))
            }
        } catch(t: Throwable) {
            when(t) {
                is IOException -> allRepositories.postValue(Resource.Error("Network Failure"))
                else -> allRepositories.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<GithubApplication>().getSystemService(
                Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}