package com.example.githubviewer.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubviewer.models.Contributor
import com.example.githubviewer.models.RepositoryInfo
import com.example.githubviewer.repositories.GithubRepository
import com.example.githubviewer.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import retrofit2.Response

class RepositoryInfoViewModel(
    private val repository: GithubRepository
) : ViewModel() {

     val repo = MutableLiveData<Resource<RepositoryInfo>>()

    var repositoryInfoResponse:RepositoryInfo?=null

    val isWatchersVisible = MutableLiveData<Boolean>()

    fun getRepo(ownerLogin: String, repoName: String) {
        viewModelScope.launch {
            safeLoadRepositoryInfo(ownerLogin,repoName)
        }
    }
    private fun handleRepositoryInfo(response: Response<RepositoryInfo>,contributors: Response<List<Contributor>>) : Resource<RepositoryInfo> {
        if(response.isSuccessful){
            response.body()?.let {
                repositoryInfo ->
                repositoryInfoResponse=repositoryInfo
                repositoryInfoResponse!!.contributors= contributors.body()!!
                return Resource.Success(repositoryInfoResponse?:repositoryInfo)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun safeLoadRepositoryInfo(ownerLogin: String, repoName: String) {
        repo.postValue(Resource.Loading())
        val response= repository.getRepositoryInfo(ownerLogin,repoName)
        val contributors=repository.getRepositoryContributors(ownerLogin,repoName)
        repo.postValue(handleRepositoryInfo(response,contributors))
    }

}