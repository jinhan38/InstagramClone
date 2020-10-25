package com.instagramclone.activity

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instagramclone.R
import com.instagramclone.adapter.CommentsAdapter
import com.instagramclone.model.Comment
import com.instagramclone.model.Post
import com.instagramclone.model.User
import com.instagramclone.utils.App
import com.instagramclone.utils.Constants.TAG
import com.instagramclone.utils.getFirebaseDatabase
import com.instagramclone.utils.getFirebaseUser
import kotlinx.android.synthetic.main.activity_comments.*

class CommentsActivity : AppCompatActivity() {

    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentsList = ArrayList<Comment>()
    lateinit var commentText : String
    private lateinit var commentsAdapter: CommentsAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        val intent = intent
        postId = intent.getStringExtra("postId").toString()
        publisherId = intent.getStringExtra("publisherId").toString()
        Log.d(TAG, "onCreate: 받은 퍼블리셔 아이디 : $publisherId")

        getUserInfo()

        post_comment.setOnClickListener {

//            if (add_comment.text == ""){
//
//            }
            if (TextUtils.isEmpty(add_comment.text.toString())) {
                Log.d(TAG, "onCreate: empty")
                Toast.makeText(this@CommentsActivity, "내용을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "onCreate: not empty")
                addComment()
            }

        }

        getPostCommentImage()
        getCommentList()


    }


    private fun getPostCommentImage() {
        val postRef = getFirebaseDatabase().reference.child("Posts").child(postId)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val data = snapshot.getValue(Post::class.java)
                    Glide.with(this@CommentsActivity)
                        .load(data!!.postImage)
                        .placeholder(R.drawable.profile)
                        .into(post_image_comment)

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun addComment() {

        val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments")
            .child(postId)

        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = add_comment.text.toString()
        commentsMap["publisher"] = getFirebaseUser()!!.uid

        commentsRef.push().setValue(commentsMap)

        commentText = add_comment.text.toString()
        add_comment!!.text.clear()

        addNotification()



    }


    private fun getUserInfo() {

        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(getFirebaseUser()!!.uid)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val user = snapshot.getValue(User::class.java)

                    Glide.with(App.instance)
                        .load(user?.getImage())
                        .placeholder(R.drawable.profile)
                        .into(profile_image_comment)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun getCommentList() {
        val commentRef = getFirebaseDatabase().reference.child("Comments").child(postId)
        commentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    for (data in snapshot.children) {
                        val comment = data.getValue(Comment::class.java)
                        commentsList.add(comment!!)

                    }

                    if (commentsList?.count()!! > 0) {

                        val layoutManager = LinearLayoutManager(
                            this@CommentsActivity,
                            LinearLayoutManager.VERTICAL,
                            true
                        )
                        layoutManager.stackFromEnd = true
                        commentsAdapter = CommentsAdapter(postId)
                        commentsAdapter.submitList(commentsList as ArrayList<Comment>)

                        recycler_view_comments.apply {
                            this.layoutManager = layoutManager
                            adapter = commentsAdapter
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun addNotification() {
        val notificationRef = getFirebaseDatabase().reference.child("Notifications").child(publisherId)
        val notificationMap = HashMap<String, Any>()
        notificationMap["userId"] = getFirebaseUser()!!.uid
        notificationMap["text"] = "commented : $commentText"
        notificationMap["postId"] = postId
        notificationMap["ispost"] = "true"

        notificationRef.push().setValue(notificationMap)
    }
}