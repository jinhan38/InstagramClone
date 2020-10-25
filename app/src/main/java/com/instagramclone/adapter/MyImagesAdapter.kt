package com.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instagramclone.R
import com.instagramclone.model.Post

class MyImagesAdapter() : RecyclerView.Adapter<MyImagesAdapterViewHolder>() {

    lateinit var mPost: List<Post>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyImagesAdapterViewHolder {

        return MyImagesAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.images_item_layout, parent, false),
            parent.context
        )
    }

    override fun onBindViewHolder(holder: MyImagesAdapterViewHolder, position: Int) {
        holder.bindWithView(mPost[position])

    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    fun submitList(mPost: List<Post>) {
        this.mPost = mPost
    }

}