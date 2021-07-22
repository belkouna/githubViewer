package com.example.githubviewer.repositories

import com.example.githubviewer.api.RetrofitInstance

class GithubRepository {

    suspend fun getAllKotlinRepositories(pageNumber: Int, pageSize: Int) =
            RetrofitInstance.api.getKotlinRepositories(pageNumber,20)

    suspend fun searchKotlinRepositories(pageNumber: Int,pageSize: Int,keyword:String)=
            RetrofitInstance.api.searchKotlinRepositories(keyword,pageNumber,pageSize)

    suspend fun getRepositoryInfo(repoOwner: String, repoName: String) =
            RetrofitInstance.api.getRepositoryInfo(repoOwner,repoName)

    suspend fun getRepositoryContributors(repoOwner: String, repoName: String) =
            RetrofitInstance.api.getContributors(repoOwner,repoName)
}