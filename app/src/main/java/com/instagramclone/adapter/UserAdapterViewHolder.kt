package com.instagramclone.adapter

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instagramclone.utils.App
import com.instagramclone.utils.Constants.TAG
import com.instagramclone.R
import com.instagramclone.`interface`.IFollowButton
import com.instagramclone.utils.getFirebaseUser
import com.instagramclone.model.User
import kotlinx.android.synthetic.main.user_item_layout.view.*

class UserAdapterViewHolder(itemView: View, iFollowButton: IFollowButton) :
    RecyclerView.ViewHolder(itemView), View.OnClickListener {


    private var iFollowButton: IFollowButton? = null

    private val user_profile_image_search = itemView.user_profile_image_search
    private val user_full_name_search = itemView.user_full_name_search
    private val user_name_search = itemView.user_name_search
    private val follow_btn_search = itemView.follow_btn_search

    init {
        this.iFollowButton = iFollowButton
        follow_btn_search.setOnClickListener(this)


    }

    /**
     * recyclerView
     */
    fun bindWithView(user: User) {
        val imageView = user_profile_image_search as ImageView
        Glide.with(App.instance)
            .load(user.getImage())
            .placeholder(R.drawable.profile)
            .into(imageView)

        user_full_name_search.text = user.getFullname()
        user_name_search.text = user.getUsername()

        val followingRef = getFirebaseUser()?.uid.let {
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(user.getUid()).exists()) {
                    follow_btn_search.text = "Following"
                } else {
                    follow_btn_search.text = "Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: 통신 에러 : $error")
            }

        })



    }

    override fun onClick(p0: View?) {
        when (p0) {
            follow_btn_search -> {
                this.iFollowButton?.onFollowButtonClick(
                    adapterPosition,
                    follow_btn_search.text.toString()
                )
            }
        }
    }

}