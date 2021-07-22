package com.example.githubviewer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubviewer.R
import com.example.githubviewer.models.Item
import com.example.githubviewer.ui.fragments.FragmentRepositoriesListDirections
import kotlinx.android.synthetic.main.fragment_repository_preview.view.*

class RepositoriesAdapter:RecyclerView.Adapter<RepositoriesAdapter.RepositoryViewHolder>(){
    inner class RepositoryViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    private val differCallback=object:DiffUtil.ItemCallback<Item>(){
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.html_url==newItem.html_url
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem==newItem
        }
    }
    val differ=AsyncListDiffer(this,differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoryViewHolder {
        return RepositoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_repository_preview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RepositoryViewHolder, position: Int) {
        val repository = differ.currentList[position]
//        language-   Kotlin
        holder.itemView.apply {
            Glide.with(this).load(repository.owner.avatar_url).into(iv_owner_avatar)
            tv_description.text=repository.description
            tv_forks_count.text=repository.forks_count.toString()
            tv_watchers_count.text=repository.watchers_count.toString()
            tv_owner_login.text=repository.owner.login
            tv_repository_name.text=repository.name
            tv_open_issues_count.text=repository.open_issues.toString()
            tv_stargazer_count.text=repository.stargazers_count.toString()
            tv_language.text=repository.language
            setOnClickListener{
                val action =
                    FragmentRepositoriesListDirections.actionFragmentRepositoriesListToFragmentRepositoryInfo(
                        repository.owner.login,
                        repository.name
                    )
                Navigation.findNavController(it).navigate(action)

            }
        }
    }
    private var onItemClickListener: ((Item) -> Unit)? = null
    fun setOnItemClickListener(listener: (Item) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}