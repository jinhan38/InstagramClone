package com.instagramclone.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instagramclone.AddStoryActivity
import com.instagramclone.R
import com.instagramclone.model.Story
import com.instagramclone.model.User
import com.instagramclone.utils.App
import com.instagramclone.utils.Constants.TAG
import kotlinx.android.synthetic.main.add_story_item.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class StoryAdapter : RecyclerView.Adapter<StoryAdapterViewHolder>() {

    private var storyList = ArrayList<Story>()
    lateinit var context: Context


    override fun getItemViewType(position: Int): Int {
        Log.d(TAG, "getItemViewType: 진입")
        if (position == 0) {
            return 0
        }
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapterViewHolder {

        context = parent.context

        return if (viewType == 0) {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.add_story_item, parent, false)
            StoryAdapterViewHolder(view, parent.context, viewType)
        } else {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.story_item, parent, false)
            StoryAdapterViewHolder(view, parent.context, viewType)
        }


    }

    override fun onBindViewHolder(holder: StoryAdapterViewHolder, position: Int) {

//        holder.userSetting(storyList[position], completion = {
//            notifyDataSetChanged()
//        })

        if (storyList.size == 0) {

            holder.itemView.story_add_btn.setOnClickListener {
                Log.d(TAG, "onBindViewHolder: 클릭 클릭")
                holder.addStory(storyList[position])
            }
        }

        if (storyList.size > 0) {

            if (position != 0) {

                holder.bindWithView(storyList[position])

            }

        } else {
            Log.d(TAG, "onBindViewHolder: 스토리 사이즈 0")
        }


    }


    override fun getItemCount(): Int {
        return storyList.size + 1
    }

    fun submitList(storyList: ArrayList<Story>) {
        this.storyList = storyList
    }

}