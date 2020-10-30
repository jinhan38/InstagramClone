package com.instagramclone.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instagramclone.R
import com.instagramclone.model.Story
import com.instagramclone.utils.Constants.TAG

class StoryAdapter : RecyclerView.Adapter<StoryAdapterViewHolder>() {

    private var storyList = ArrayList<Story>()


    override fun getItemViewType(position: Int): Int {
        Log.d(TAG, "getItemViewType: 진입")
        if (position == 0) {
            return 0
        }
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryAdapterViewHolder {

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

        if (storyList.size == 0) {
            holder.storyAddClick()
        } else {
            val story = storyList[position]
            holder.bindWithView(story)
        }


    }


    override fun getItemCount(): Int {
        return storyList.size + 1
    }

    fun submitList(storyList: ArrayList<Story>) {
        this.storyList = storyList
    }

}