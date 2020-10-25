package com.instagramclone.adapter

import android.content.Context
import com.instagramclone.utils.Constants.TAG
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.instagramclone.R
import com.instagramclone.fragment.PostDetailFragment
import com.instagramclone.fragment.ProfileFragment
import com.instagramclone.model.Notification
import com.instagramclone.model.Post
import com.instagramclone.model.User
import com.instagramclone.utils.getFirebaseDatabase
import kotlinx.android.synthetic.main.notification_item_layout.view.*

class NotificationAdapterViewHolder(itemView: View, private val context: Context) :
    RecyclerView.ViewHolder(itemView) {


    fun bindWithView(notification: Notification) {


        getUserData(notification.userId, completion = { imageUrl, username ->
            Glide.with(context).load(imageUrl).placeholder(R.drawable.profile)
                .into(itemView.notification_profile_image)
            itemView.username_notification.text = username
        })

        Log.d(TAG, "bindWithView: 포스트아이디 ${notification.postId}")
        Log.d(TAG, "bindWithView: ispost : ${notification.ispost}")
        if (notification.ispost == "true") {
            Log.d(TAG, "bindWithView: ispost true")
            itemView.notification_post_image.visibility = View.VISIBLE
            getPostCommentImage(notification.postId, itemView.notification_post_image, context)
        } else {
            Log.d(TAG, "bindWithView: ispost false")
            itemView.notification_post_image.visibility = View.INVISIBLE
        }

        itemView.comment_notification.text = notification.text

        itemView.setOnClickListener {

            if (notification.ispost == "true") {

                val editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("postId", notification.postId)
                editor.apply()

                (context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, PostDetailFragment()).commit()
            } else {
                val editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("profileId", notification.userId)
                editor.apply()

                (context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()
            }

        }


    }


    private fun getUserData(userId: String, completion: (String, String) -> Unit) {
        val userRef = getFirebaseDatabase().reference.child("Users").child(userId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    completion(user!!.getImage(), user!!.getUsername())
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }


    private fun getPostCommentImage(postId: String, imageView: ImageView, context: Context) {
        val postRef = getFirebaseDatabase().reference.child("Posts").child(postId)

        Log.d(TAG, "getPostCommentImage: $postId")
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val post = snapshot.getValue(Post::class.java)

                    Log.d(TAG, "onDataChange: 이미지 포스트 url : ${post!!.postImage}")
                    Glide.with(context)
                        .load(post!!.postImage)
                        .placeholder(R.drawable.profile)
                        .into(imageView)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


}