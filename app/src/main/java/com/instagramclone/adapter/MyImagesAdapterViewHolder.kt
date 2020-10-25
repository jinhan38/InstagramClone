package com.instagramclone.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instagramclone.R
import com.instagramclone.model.Post
import kotlinx.android.synthetic.main.images_item_layout.view.*

class MyImagesAdapterViewHolder(itemView : View, private val context : Context) : RecyclerView.ViewHolder(itemView)  {


    fun bindWithView(post : Post){
        Glide.with(context)
            .load(post.postImage)
            .placeholder(R.drawable.profile)
            .into(itemView.post_image)

    }
}