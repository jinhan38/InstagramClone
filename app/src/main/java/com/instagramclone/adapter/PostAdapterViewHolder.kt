package com.instagramclone.adapter

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.instagramclone.utils.Constants.TAG
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instagramclone.R
import com.instagramclone.model.Post
import com.instagramclone.model.User
import com.instagramclone.utils.App
import com.instagramclone.utils.getFirebaseDatabase
import com.instagramclone.utils.getFirebaseUser
import kotlinx.android.synthetic.main.posts_layout.view.*

class PostAdapterViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView) {


//    private var iFollowButton: IFollowButton? = null

    private val user_profile_image_post = itemView.user_profile_image_post
    private val user_name_post = itemView.user_name_post
    private val post_image_home = itemView.post_image_home
    private val post_image_like_btn = itemView.post_image_like_btn
    private val post_image_comment_btn = itemView.post_image_comment_btn
    private val post_save_comment_btn = itemView.post_save_comment_btn
    private val likes = itemView.likes
    private val publisher = itemView.publisher
    private val description = itemView.description
    private val comments = itemView.comments

    init {

    }

    /**
     * recyclerView
     */
    fun bindWithView(post: Post) {
//        val imageView = post_image_home as ImageView
        Glide.with(App.instance)
            .load(post.postImage)
            .placeholder(R.drawable.profile)
            .into(post_image_home)
        
        if (TextUtils.isEmpty(post.description)) {
            itemView.description.visibility = View.GONE
        } else {
            itemView.description.visibility = View.VISIBLE
            description.text = post.description
        }

        val usersRef = getFirebaseDatabase().reference.child("Users").child(post.publisher)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

                Glide.with(App.instance)
                    .load(user?.getImage())
                    .placeholder(R.drawable.profile)
                    .into(user_profile_image_post)

                user_name_post.text = user?.getUsername()
                publisher.text = user?.getFullname()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


        checkSaveStatus(post.postId, post_save_comment_btn)

        post_save_comment_btn.setOnClickListener {

            Log.d(TAG, "bindWithView:저장  클릭")
            if (post_save_comment_btn.tag == "Save") {
                Log.d(TAG, "bindWithView: 저장 save")
                getFirebaseDatabase().reference.child("Saves")
                    .child(getFirebaseUser()!!.uid)
                    .child(post.postId)
                    .setValue(true)
            } else {
                Log.d(TAG, "bindWithView: 저장 not save")
                getFirebaseDatabase().reference.child("Saves")
                    .child(getFirebaseUser()!!.uid)
                    .child(post.postId)
                    .removeValue()

            }

        }


    }


    private fun checkSaveStatus(postId: String, imageView: ImageView) {
        val savesRef = FirebaseDatabase.getInstance().reference.child("Saves")
            .child(getFirebaseUser()!!.uid)

        savesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(postId).exists()) {
                    imageView.setImageResource(R.drawable.save_large_icon)
                    imageView.tag = "Saved"

                } else {
                    imageView.setImageResource(R.drawable.save_unfilled_large_icon)
                    imageView.tag = "Save"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }


}