package com.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instagramclone.R
import com.instagramclone.`interface`.IFollowButton
import com.instagramclone.model.User

class UserAdapter(
    private val isFragment: Boolean = false,
    private val followButton: IFollowButton
) : RecyclerView.Adapter<UserAdapterViewHolder>() {

  private var iFollowButton : IFollowButton? = null
    private var mUser = ArrayList<User>()

    init {
        this.iFollowButton = followButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapterViewHolder {

        return UserAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.user_item_layout, parent, false),
            this.iFollowButton!!
        )

    }

    override fun onBindViewHolder(holder: UserAdapterViewHolder, position: Int) {

        holder.bindWithView(mUser[position])
    }

    override fun getItemCount(): Int {
        return mUser.count()
    }


    fun submitList(mUser : ArrayList<User>){
        this.mUser = mUser
    }

}