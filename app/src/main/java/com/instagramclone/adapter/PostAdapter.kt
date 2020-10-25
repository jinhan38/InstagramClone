package com.instagramclone.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.instagramclone.R
import com.instagramclone.activity.CommentsActivity
import com.instagramclone.activity.ShowUsersActivity
import com.instagramclone.model.Post
import com.instagramclone.model.User
import com.instagramclone.utils.App
import com.instagramclone.utils.Constants.TAG
import com.instagramclone.utils.getFirebaseDatabase
import com.instagramclone.utils.getFirebaseUser
import kotlinx.android.synthetic.main.posts_layout.view.*

class PostAdapter() : RecyclerView.Adapter<PostAdapterViewHolder>() {

    private var postList = ArrayList<Post>()
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapterViewHolder {
        Log.d(TAG, "onCreateViewHolder: 진입")
        context = parent.context
        return PostAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.posts_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PostAdapterViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: 진입")

        val post = postList[position]

        holder.bindWithView(post)

        holder.itemView.post_image_like_btn.apply {
            setOnClickListener {
                if (this.tag == "Like") {

                    getFirebaseDatabase().reference.child("Likes")
                        .child(post.postId)
                        .child(getFirebaseUser()!!.uid)
                        .setValue(true)

                    getUserData(completion = {
                        addNotification(
                            userId = post.publisher,
                            postId = post.postId,
                            username = it
                        )
                    })

                } else {

                    getFirebaseDatabase().reference.child("Likes")
                        .child(post.postId)
                        .child(getFirebaseUser()!!.uid)
                        .removeValue()

//                    val intent = Intent(App.instance, MainActivity::class.java)
//                    App.instance.startActivity(intent)
                }

                numberOrLikes(holder.itemView.likes, post.postId)
            }
        }

        holder.itemView.post_image_comment_btn.apply {
            this.setOnClickListener {

                val intent = Intent(App.instance, CommentsActivity::class.java)
                intent.putExtra("postId", post.postId)
                intent.putExtra("publisherId", post.publisher)
                context!!.startActivity(intent)

            }
        }

        holder.itemView.comments.setOnClickListener {
            val intent = Intent(App.instance, CommentsActivity::class.java)
            intent.putExtra("postId", post.postId)
            intent.putExtra("publisherId", post.publisher)
            context!!.startActivity(intent)
        }

        isLikes(post.postId, holder.itemView.post_image_like_btn)
        numberOrLikes(holder.itemView.likes, post.postId)
        getTotalComments(holder.itemView.comments, post.postId)

        holder.itemView.likes.setOnClickListener {
            Log.d(TAG, "onBindViewHolder: 포스트 어댑터 ${post.publisher}")
            val intent = Intent(context, ShowUsersActivity::class.java)
            intent.putExtra("id", post.postId)
            intent.putExtra("title", "likes")
            context!!.startActivity(intent)
        }


    }

    private fun numberOrLikes(likes: TextView, postId: String) {
        val likesRef = getFirebaseDatabase().reference
            .child("Likes").child(postId)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    likes.text = snapshot.childrenCount.toString() + " likes"
                    likes.visibility = View.VISIBLE

                } else {
                    likes.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun getTotalComments(comments: TextView, postId: String) {
        val commentsRef = getFirebaseDatabase().reference
            .child("Comments").child(postId)

        commentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {


                    comments.text = "댓글 ${snapshot.childrenCount}개 보기"
                    comments.visibility = View.VISIBLE

                } else {
                    comments.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun isLikes(postId: String, postImageLikeBtn: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val likesRef = getFirebaseDatabase().reference
            .child("Likes").child(postId)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists()) {
                    postImageLikeBtn.setImageResource(R.drawable.heart_clicked)
                    postImageLikeBtn.tag = "Liked"
                } else {
                    postImageLikeBtn.setImageResource(R.drawable.heart_not_clicked)
                    postImageLikeBtn.tag = "Like"
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }


    private fun getUserData(completion: (String) -> Unit) {
        val userRef = getFirebaseDatabase().reference.child("Users").child(getFirebaseUser()!!.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    completion(user!!.getUsername())
                }

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }

    private fun addNotification(userId: String, postId: String, username: String) {
        val notificationRef = getFirebaseDatabase().reference.child("Notifications").child(
            userId
        )

        val notificationMap = HashMap<String, Any>()

        notificationMap["userId"] = getFirebaseUser()!!.uid
        notificationMap["text"] = "${username}님이 좋아요를 눌렀습니다."
        notificationMap["postId"] = postId
        notificationMap["ispost"] = "true"

        notificationRef.push().setValue(notificationMap)
    }

    override fun getItemCount(): Int {
        return this.postList.size
    }

    fun submitList(postList: ArrayList<Post>) {
        this.postList = postList
    }
}