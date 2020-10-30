package com.instagramclone.adapter

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instagramclone.R
import com.instagramclone.model.Story
import com.instagramclone.utils.Constants.TAG
import com.instagramclone.utils.getFirebaseDatabase
import kotlinx.android.synthetic.main.add_story_item.view.*
import kotlinx.android.synthetic.main.story_item.view.*

class StoryAdapterViewHolder(itemView: View, val context: Context, val viewHolderType: Int) :
    RecyclerView.ViewHolder(itemView) {


    val story_image = itemView.story_image
    val story_image_seen = itemView.story_image_seen
    val story_user_name = itemView.story_user_name
    val story_add_btn = itemView.story_add_btn


    fun storyAddClick(){
        story_add_btn.setOnClickListener {
            Log.d(TAG, "storyAddClick: 스토리 추가 클릭")
        }
    }


    fun bindWithView(story: Story) {
        Glide.with(context)
            .load(story.imageurl)
            .placeholder(R.drawable.profile)
            .into(story_image)

        Glide.with(context)
            .load(story.imageurl)
            .placeholder(R.drawable.profile)
            .into(story_image_seen)

 
    }


}