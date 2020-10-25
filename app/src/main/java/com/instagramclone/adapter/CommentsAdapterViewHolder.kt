package com.instagramclone.adapter

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instagramclone.R
import com.instagramclone.model.Comment
import com.instagramclone.model.Post
import com.instagramclone.model.User
import com.instagramclone.utils.Constants.TAG
import com.instagramclone.utils.App
import kotlinx.android.synthetic.main.comments_item_layout.view.*

class CommentsAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    private val comments_list_image_comment = itemView.comments_list_image_comment
    private val conmments_list_username = itemView.conmments_list_username
    private val comments_list_comment = itemView.comments_list_comment


    fun bindWithView(comment: Comment) {
        Log.d(TAG, "bindWithView: 코멘트 ${comment.comment}")
        comments_list_comment.text = comment.comment


        Log.d(TAG, "bindWithView: 퍼블리셔 ${comment.publisher}")
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(comment.publisher)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                        var user = snapshot.getValue(User::class.java)

                        Glide.with(App.instance)
                            .load(user!!.getImage())
                            .placeholder(R.drawable.profile)
                            .into(comments_list_image_comment)

                        conmments_list_username.text = user.getUsername()


                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })



    }

}