package com.instagramclone.adapter

import android.content.Context
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instagramclone.R
import com.instagramclone.fragment.PostDetailFragment
import com.instagramclone.model.Post
import kotlinx.android.synthetic.main.images_item_layout.view.*

class MyImagesAdapterViewHolder(itemView: View, private val context: Context) :
    RecyclerView.ViewHolder(itemView) {


    fun bindWithView(post: Post) {
        Glide.with(context)
            .load(post.postImage)
            .placeholder(R.drawable.profile)
            .into(itemView.post_image)


        itemView.post_image.setOnClickListener {

            val editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("postId", post.postId)
            editor.apply()

            (context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PostDetailFragment()).commit()
        }

    }
}