package com.instagramclone.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
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
import com.instagramclone.utils.Constants.TAG
import kotlinx.android.synthetic.main.add_story_item.view.*
import kotlinx.android.synthetic.main.story_item.view.*

class StoryAdapterViewHolder(itemView: View, val context: Context, val viewHolderType: Int) :
    RecyclerView.ViewHolder(itemView) {


    val story_image = itemView.story_image
    val story_image_seen = itemView.story_image_seen
    val story_user_name = itemView.story_user_name
    val story_add_btn = itemView.story_add_btn

    fun userSetting(story: Story, completion : () -> Unit) {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(story.userid)

        Log.d(TAG, "bindWithView: ${story.userid}")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    story_user_name.text = user!!.getUsername()

                    Glide.with(context)
                        .load(user.getImage())
                        .into(story_add_btn)
                    Log.d(TAG, "onDataChange: ${user.getImage()} ")
                    completion()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    fun addStory(story: Story) {
        Log.d(TAG, "addStory: 클릭")
        val intent = Intent(context, AddStoryActivity::class.java)
        intent.putExtra("userId", story.userid)
        context.startActivity(intent)

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