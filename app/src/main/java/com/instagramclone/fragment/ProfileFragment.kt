package com.instagramclone.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instagramclone.activity.AccountSettingsActivity
import com.instagramclone.utils.App
import com.instagramclone.utils.Constants.TAG
import com.instagramclone.R
import com.instagramclone.adapter.MyImagesAdapter
import com.instagramclone.model.Post
import com.instagramclone.utils.getFirebaseUser
import com.instagramclone.model.User
import com.instagramclone.utils.getFirebaseDatabase
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment() {

    private lateinit var profileId: String
    private var postList = ArrayList<Post>()

    //    private var postList: List<Post>? = null
    private var currentUid = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        myPhotos(completion = {
            if (postList.count() > 0) {
                view.recycler_view_upload_pic.apply {
                    var gridLayoutManager = GridLayoutManager(activity, 3)
                    this.layoutManager = gridLayoutManager
                    setHasFixedSize(true)
                    val myImagesAdapter = MyImagesAdapter()
                    myImagesAdapter.submitList(postList)
                    adapter = myImagesAdapter

                }
            }

        })


        val pref = App.instance.getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        if (pref != null) {


            this.profileId = pref.getString("profileId", "none").toString()
            Log.d(TAG, "onCreateView: profileid : $profileId")
            Log.d(TAG, "onCreateView: 파베 유아이디 ${getFirebaseUser()?.uid}")

            if (getFirebaseUser()?.uid == this.profileId) {
                view.edit_account_settings_btn.text = getString(R.string.profile_edit)
                currentUid = getFirebaseUser()!!.uid
            } else if (getFirebaseUser()?.uid != this.profileId) {
                checkFollowAndFollowingButtonStatus()
                currentUid = this.profileId
            }

            getFollowers(currentUid)
            getFollowings(currentUid)
            getUserInfo(currentUid)
        }


        view.edit_account_settings_btn.setOnClickListener {

            when (edit_account_settings_btn.text) {
                getString(R.string.profile_edit) -> {

                    startActivity(Intent(context, AccountSettingsActivity::class.java))

                }

                "Follow" -> {
                    getFirebaseUser()?.uid.let {
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it.toString())
                            .child("Following").child(this.profileId)
                            .setValue(true)

                    }

                    getFirebaseUser()?.uid.let { it2 ->

                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(this.profileId)
                            .child("Followers").child(it2.toString())
                            .setValue(true).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "onFollowButtonClick: Followers 성공")
                                    edit_account_settings_btn.text = "Following"
                                }
                            }

                    }
                }

                "Following" -> {
                    getFirebaseUser()?.uid.let {

                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it.toString())
                            .child("Following").child(this.profileId)
                            .removeValue()
                    }

                    getFirebaseUser()?.uid.let { it2 ->

                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(this.profileId)
                            .child("Followers").child(it2.toString())
                            .removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "onFollowButtonClick: Followers 제거 성공")
                                    edit_account_settings_btn.text = "Follow"
                                }
                            }

                    }
                }
            }


        }

        return view
    }


    /**
     * 현재 보여주는 프로필이 등록한 post 데이터 가져오기
     */
    private fun myPhotos(completion: () -> Unit) {
        val postsRef = getFirebaseDatabase().reference.child("Posts")

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    postList.clear()


                    for (data in snapshot.children) {

                        val post = data.getValue(Post::class.java)
                        if (post?.publisher == currentUid) {
                            postList.add(post)

                        }
                        postList.reverse()
                    }

                    completion()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    private fun checkFollowAndFollowingButtonStatus() {
        val followingRef = getFirebaseUser()?.uid.let {
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: $profileId")
                if (snapshot.child(profileId).exists()) {
                    view?.edit_account_settings_btn?.text = "Following"
                } else {
                    view?.edit_account_settings_btn?.text = "Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    /**
     * ProfileFragment에 팔로워 숫자 가져오기
     */
    private fun getFollowers(uid: String) {
        val followersRef = uid.let {
            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(it)
                .child("Followers")
        }

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                view?.total_followers?.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

    /**
     * ProfileFragment에 팔로잉 숫자 가져오기
     */
    private fun getFollowings(uid: String) {
        val followersRef = uid.let {
            FirebaseDatabase.getInstance().reference.child("Follow")
                .child(it.toString())
                .child("Following")
        }

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot != null) {
                    view?.total_following?.text = (snapshot.childrenCount - 1).toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }


    private fun getUserInfo(uid: String) {

        val userRef = uid.let {
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(it)

        }

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val user = snapshot.getValue(User::class.java)

                    val imageView = view?.pro_image_profile_frag as ImageView
                    Glide.with(App.instance)
                        .load(user?.getImage())
                        .placeholder(R.drawable.profile)
                        .into(imageView)

                    view?.profile_fragment_username?.text = user?.getUsername()
                    view?.full_name?.text = user?.getFullname()
                    view?.bio_profile_frag?.text = user?.getBIo()
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: 진입")
    }

    override fun onPause() {
        Log.d(TAG, "onPause: 진입")
        super.onPause()
        val pref = App.instance.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        pref.putString("profileId", getFirebaseUser()?.uid)
        pref.apply()
    }

    override fun onStop() {
        Log.d(TAG, "onStop: 진입")
        super.onStop()
        val pref = App.instance.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        pref.putString("profileId", getFirebaseUser()?.uid)
        pref.apply()
    }

}