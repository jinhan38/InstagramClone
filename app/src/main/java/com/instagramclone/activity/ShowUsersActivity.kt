package com.instagramclone.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instagramclone.R
import com.instagramclone.`interface`.IFollowButton
import com.instagramclone.adapter.UserAdapter
import com.instagramclone.model.User
import com.instagramclone.utils.Constants.TAG
import com.instagramclone.utils.getFirebaseDatabase
import com.instagramclone.utils.getFirebaseUser
import kotlinx.android.synthetic.main.activity_show_users.*

class ShowUsersActivity : AppCompatActivity(), IFollowButton {

    var id: String = ""
    var title: String = ""

    lateinit var userAdapter: UserAdapter
    private var userList: ArrayList<User> = ArrayList()
    private var idList: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_users)

        userAdapter = UserAdapter(false, this)
        userAdapter.submitList(userList)

        val intent = intent
        id = intent.getStringExtra("id").toString()
        title = intent.getStringExtra("title").toString()


        setSupportActionBar(toolbar)
        supportActionBar!!.title = title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        recycler_view.apply {
            Log.d(TAG, "onCreate: 리사이클러뷰 진입")
            this.setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(this@ShowUsersActivity)
            adapter = userAdapter
        }

        Log.d(TAG, "onCreate: 타이틀 $ title")
        when (title) {

            "likes" -> getLikes(completion = { showUsers() })
            "following" -> getFollowing(completion = { showUsers() })
            "followers" -> getFollowers(completion = { showUsers() })
            "views" -> getViews()
        }

    }


    private fun getViews() {

    }

    private fun getFollowers(completion: () -> Unit) {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow")
            .child(id)
            .child("Followers")


        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    idList.clear()

                    for (data in snapshot.children) {
                        idList.add(data.key.toString())
                    }

                    completion()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun getFollowing(completion: () -> Unit) {

        val followingRef = FirebaseDatabase.getInstance().reference.child("Follow")
            .child(id).child("Following")

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {

                    idList.clear()

                    for (data in snapshot.children) {
                        idList.add(data.key.toString())
                    }

                    completion()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun getLikes(completion: () -> Unit) {
        val likesRef = getFirebaseDatabase().reference
            .child("Likes").child(id)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    Log.d(TAG, "onDataChange: likes ")
                    idList.clear()

                    for (data in snapshot.children) {
                        idList.add(data.key.toString())
                    }

                    completion()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun showUsers() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: 데이터 호출 성공 : ")
                userList.clear()

                for (data in snapshot.children) {

                    val user = data.getValue(User::class.java)

                    Log.d(TAG, "onDataChange:user.getUid :  ${user!!.getUid()}")
                    Log.d(TAG, "onDataChange:id $id")
                    Log.d(TAG, "onDataChange: idList $idList")
                    for (id in idList) {
                        if (user!!.getUid() == id) {
                            userList.add(user)
                        }
                    }
                }
                Log.d(TAG, "onDataChange: userList 사이즈 ${userList.size}")
                userAdapter.submitList(userList)
                userAdapter.notifyDataSetChanged()


            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "onCancelled: 데이터 호출 실패 : $error")
            }

        })

    }

    override fun onFollowButtonClick(position: Int, buttonText: String) {

        if (!userList.isNullOrEmpty() && userList.size > 0) {

            Log.d(TAG, "onFollowButtonClick: 버튼 텍스트 $buttonText")
            when (buttonText) {


                "Follow" -> {
                    getFirebaseUser()?.uid.let {
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it.toString())
                            .child("Following").child(userList[position].getUid())
                            .setValue(true).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Log.d(TAG, "onFollowButtonClick: follow 성공")

                                    getFirebaseUser()?.uid.let { it2 ->

                                        FirebaseDatabase.getInstance().reference
                                            .child("Follow").child(userList[position].getUid())
                                            .child("Followers").child(it2.toString())
                                            .setValue(true).addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Log.d(TAG, "onFollowButtonClick: Followers 성공")
                                                    getUserData(completion = {username ->
                                                        addNotification(userList[position].getUid(), username)
                                                    })
                                                }
                                            }

                                    }


                                }
                            }

                    }
                }

                else -> {
                    getFirebaseUser()?.uid.let {

                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it.toString())
                            .child("Following").child(userList[position].getUid())
                            .removeValue().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Log.d(TAG, "onFollowButtonClick: follow 제거 성공")

                                    getFirebaseUser()?.uid.let { it2 ->

                                        FirebaseDatabase.getInstance().reference
                                            .child("Follow").child(userList[position].getUid())
                                            .child("Followers").child(it2.toString())
                                            .removeValue().addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Log.d(
                                                        TAG,
                                                        "onFollowButtonClick: Followers 제거 성공"
                                                    )
                                                }
                                            }

                                    }


                                }
                            }

                    }
                }
            }

        }



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

    private fun addNotification(userId: String, username: String) {
        val notificationRef = getFirebaseDatabase().reference.child("Notifications").child(userId)

        val notificationMap = HashMap<String, Any>()

        notificationMap["userId"] = getFirebaseUser()!!.uid
        notificationMap["text"] = "${username}님이 팔로우를 시작했습니다."
        notificationMap["postId"] = ""
        notificationMap["ispost"] = "false"

        notificationRef.push().setValue(notificationMap)
    }
}