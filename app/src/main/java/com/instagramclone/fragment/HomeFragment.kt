package com.instagramclone.fragment

import android.os.Bundle
import android.util.Log
import com.instagramclone.utils.Constants.TAG
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instagramclone.R
import com.instagramclone.adapter.PostAdapter
import com.instagramclone.model.Post
import com.instagramclone.utils.getFirebaseUser
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        postList = ArrayList<Post>()
        followingList = ArrayList<String>()

        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, true)
        layoutManager.stackFromEnd = true
        postAdapter = PostAdapter()





        postAdapter!!.submitList(postList as ArrayList<Post>)

        view.recycler_view_home.apply {
            Log.d(TAG, "onCreateView: recyclerView")
            this.setHasFixedSize(true)
            this.layoutManager = layoutManager

            adapter = postAdapter


        }

        checkFollowings()





        return view
    }

    private fun checkFollowings() {

        val followingRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("Following")

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    followingList?.clear()
                    for (data in snapshot.children) {
                        data.key?.let {
                            (followingList as ArrayList<String>).add(it)
                        }
                    }
                    retrievePosts()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun retrievePosts(){

        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
             postsRef.addValueEventListener(object : ValueEventListener{
                 override fun onDataChange(snapshot: DataSnapshot) {
                     if (snapshot.exists()) {

                         postList?.clear()
                         for (data in snapshot.children) {
                             val post = data.getValue(Post::class.java)

                             for (userId in followingList!! as ArrayList<String>){
                                 if (post!!.publisher == userId){
                                     postList?.add(post)
                                 }
                                 postAdapter!!.notifyDataSetChanged()
                             }

                         }
                     }

                     
                 }

                 override fun onCancelled(error: DatabaseError) {
                     
                 }

             })

    }


}