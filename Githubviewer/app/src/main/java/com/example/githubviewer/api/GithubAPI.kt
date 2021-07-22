package com.example.githubviewer.api

import com.example.githubviewer.models.Contributor
import com.example.githubviewer.models.RepositoriesSearchResponse
import com.example.githubviewer.models.RepositoryInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
interface GithubAPI {
    @GET("search/repositories?q=language:kotlin&order=desc&sort=stars")
    suspend fun getKotlinRepositories(
            @Query("page")
            pageNumber:Int=1,
            @Query("per_page")
            pageSize:Int=20
    ):Response<RepositoriesSearchResponse>
    @GET("search/repositories?q=language:kotlin&order=desc&sort=stars")
    suspend fun searchKotlinRepositories(
            @Query("q")
            searchQuery:String,
            @Query("page")
            pageNumber:Int=1,
            @Query("per_page")
            pageSize:Int=20
    ):Response<RepositoriesSearchResponse>

    @GET("repos/{owner}/{name}")
    suspend fun getRepositoryInfo(
            @Path("owner") owner: String,   // owner login
            @Path("name") name: String      // repo name
    )
    :Response<RepositoryInfo>

    @GET("repos/{owner}/{name}/contributors")
    suspend fun getContributors(
        @Path("owner") owner: String,   // owner login
        @Path("name") name: String     // repo name
    ):Response<List<Contributor>>
}