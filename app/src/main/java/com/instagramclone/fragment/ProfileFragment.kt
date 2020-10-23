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
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instagramclone.AccountSettingsActivity
import com.instagramclone.App
import com.instagramclone.Constants.TAG
import com.instagramclone.R
import com.instagramclone.getFirebaseUser
import com.instagramclone.model.User
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    private lateinit var profileId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val pref = App.instance.getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        if (pref != null) {
            var currentUid = ""

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
                    view?.total_following?.text = snapshot.childrenCount.toString()
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

    override fun onPause() {
        super.onPause()
        val pref = App.instance.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        pref.putString("profileId", getFirebaseUser()?.uid)
        pref.apply()
    }

    override fun onStop() {
        super.onStop()
        val pref = App.instance.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
        pref.putString("profileId", getFirebaseUser()?.uid)
        pref.apply()
    }

}