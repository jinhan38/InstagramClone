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
import com.instagramclone.adapter.StoryAdapter
import com.instagramclone.model.Post
import com.instagramclone.model.Story
import com.instagramclone.utils.getFirebaseDatabase
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    private var postAdapter: PostAdapter = PostAdapter()
    private var storyAdapter: StoryAdapter = StoryAdapter()
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<String>? = null
    private var storyList = ArrayList<Story>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        postList = ArrayList<Post>()
        followingList = ArrayList<String>()


        postAdapter!!.submitList(postList as ArrayList<Post>)


        view.recycler_view_home.apply {
            Log.d(TAG, "onCreateView: recyclerView")

            storyAdapter.submitList(storyList)
            val verticalLayoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, true)
            verticalLayoutManager.stackFromEnd = true
            this.setHasFixedSize(true)
            this.layoutManager = verticalLayoutManager
            adapter = postAdapter


        }

        view.recycler_view_story.apply {
            val horizontalLayoutManager =
                LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, true)
            horizontalLayoutManager.stackFromEnd = true
            this.setHasFixedSize(true)
            this.layoutManager = horizontalLayoutManager
            this.adapter = storyAdapter
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
                    retrieveStories()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun retrieveStories() {
        
        storyList.clear()
        //어댑터의 첫 포지션은 추가하는 이미지다
        //그래서 비어있는 story data를 넣는다
        storyList.add(Story())

        Log.d(TAG, "onDataChange: 스토리 리스트 추가 $storyList")
        
        val storyRef = getFirebaseDatabase().reference.child("Story")

        storyRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {


                if (snapshot.exists()) {

                    val timeCurrent = System.currentTimeMillis()

                    for (id in followingList!!) {
                        var countStory = 0
                        var story: Story? = null


                        for (data in snapshot.child(id).children) {
                            story = data.getValue(Story::class.java)

                            if (timeCurrent > story!!.timestart && timeCurrent < story!!.timeend) {
                                countStory += 1
                            }
                        }

                        if (countStory > 0) {
                            storyList.add(story!!)
                        }

                    }

                    storyAdapter.submitList(storyList = storyList)
                    storyAdapter.notifyDataSetChanged()

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun retrievePosts() {

        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")
        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    postList?.clear()
                    for (data in snapshot.children) {
                        val post = data.getValue(Post::class.java)

                        for (userId in followingList!! as ArrayList<String>) {
                            if (post!!.publisher == userId) {
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