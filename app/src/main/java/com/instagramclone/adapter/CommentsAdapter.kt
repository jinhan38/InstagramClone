package com.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instagramclone.R
import com.instagramclone.model.Comment

class CommentsAdapter(postId : String) : RecyclerView.Adapter<CommentsAdapterViewHolder>() {

    lateinit var commentsList: ArrayList<Comment>
    lateinit var context: Context
    private val postId = postId


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapterViewHolder {
        this.context = parent.context

        return CommentsAdapterViewHolder(
            LayoutInflater.from(context).inflate(R.layout.comments_item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CommentsAdapterViewHolder, position: Int) {

        val comment = commentsList[position]
        holder.bindWithView(comment = comment)


    }

    override fun getItemCount(): Int {
        return commentsList.size
    }

    fun submitList(commentsList: ArrayList<Comment>) {
        this.commentsList = commentsList
    }
}