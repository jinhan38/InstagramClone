package com.instagramclone.adapter

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instagramclone.App
import com.instagramclone.Constants.TAG
import com.instagramclone.R
import com.instagramclone.`interface`.IFollowButton
import com.instagramclone.model.User
import kotlinx.android.synthetic.main.user_item_layout.view.*

class UserAdapterViewHolder(itemView: View, iFollowButton: IFollowButton) :
    RecyclerView.ViewHolder(itemView) {


    private var iFollowButton: IFollowButton? = null

    private val user_profile_image_search = itemView.user_profile_image_search
    private val user_full_name_search = itemView.user_full_name_search
    private val user_name_search = itemView.user_name_search
    private val follow_btn_search = itemView.follow_btn_search

    init {
        this.iFollowButton = iFollowButton
        follow_btn_search.setOnClickListener {
            Log.d(TAG, "follow 버튼 클릭: ")
        }
    }

    fun bindWithView(user: User) {
        Glide.with(App.instance)
            .load(user.image)
            .placeholder(R.drawable.profile)
            .into(user_profile_image_search)

        user_full_name_search.text = user.fullname
        user_name_search.text = user.username
    }

}