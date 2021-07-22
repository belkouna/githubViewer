package com.example.githubviewer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubviewer.R
import com.example.githubviewer.models.Contributor
import kotlinx.android.synthetic.main.fragment_contributor.view.*


class ContributorsAdapter: RecyclerView.Adapter<ContributorsAdapter.ContributorsViewHolder>(){
    inner class ContributorsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    private val differCallback=object: DiffUtil.ItemCallback<Contributor>(){
        override fun areItemsTheSame(oldItem: Contributor, newItem: Contributor): Boolean {
            return oldItem.html_url==newItem.html_url
        }

        override fun areContentsTheSame(oldItem: Contributor, newItem: Contributor): Boolean {
            return oldItem==newItem
        }
    }
    val differ= AsyncListDiffer(this,differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributorsViewHolder {
        return ContributorsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_repository_preview,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ContributorsViewHolder, position: Int) {
        val contributor = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(contributor.avatar_url).into(iv_contributor_avatar)
            tv_contributor_login.text=contributor.login

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}