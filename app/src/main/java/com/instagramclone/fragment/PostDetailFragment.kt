package com.instagramclone.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instagramclone.R
import com.instagramclone.adapter.PostAdapter
import com.instagramclone.model.Post
import kotlinx.android.synthetic.main.fragment_post_detail.view.*

class PostDetailFragment : Fragment() {

    private var postAdapter = PostAdapter()
    private var postList = ArrayList<Post>()
    private var postId : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)


        val preferences = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (preferences != null){
            postId = preferences.getString("postId", "none")
        }
         
        view.recycler_view_post_detail.apply {
            this.setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            this.layoutManager = layoutManager
            postAdapter.submitList(postList = postList)
            adapter = postAdapter

        }




        retrievePosts()
        
        return view
    }

    private fun retrievePosts(){

        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts").child(postId!!)
        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    postList!!.clear()
                    val post = snapshot.getValue(Post::class.java)
                    postList!!.add(post!!)

                    postAdapter!!.notifyDataSetChanged()
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

}