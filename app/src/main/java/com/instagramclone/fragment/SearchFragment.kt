package com.instagramclone.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instagramclone.utils.Constants.TAG
import com.instagramclone.R
import com.instagramclone.`interface`.IFollowButton
import com.instagramclone.adapter.UserAdapter
import com.instagramclone.utils.getFirebaseUser
import com.instagramclone.model.User
import com.instagramclone.utils.getFirebaseDatabase
import com.instagramclone.utils.onMyTextChanged
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : Fragment(), IFollowButton {


    private var mList: ArrayList<User> = ArrayList()
    private val userAdapter = UserAdapter(true, this)
    private lateinit var recycler_view_search: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        this.recycler_view_search = view.recycler_view_search
        retrieveUsers(completion = {
            Log.d(TAG, "onCreateView: retrieveUsers completion")
            when {
                true -> recyclerViewSetting(mList)
                false -> Log.d(TAG, "onCreateView: 데이터 호출 실패")
            }
        })


        view.search_edit_text.onMyTextChanged {


            if (!it.isNullOrEmpty()) {


                Log.d(TAG, "onCreateView: 검색 : $it")
                recycler_view_search.visibility = View.VISIBLE

                searchUsers(it.toString().toLowerCase(), completion = {
                    when {
                        true -> {
                            recycler_view_search.visibility = View.VISIBLE
                            userAdapter.submitList(mList)
                            userAdapter.notifyDataSetChanged()
                        }
                        else -> {
                            Log.d(TAG, "onCreateView: 데이터 호출 실패")
                        }

                    }
                })

            } else {
                retrieveUsers(completion = {
                    Log.d(TAG, "onCreateView: retrieveUsers completion")
                    when {
                        true -> recyclerViewSetting(mList)
                        false -> Log.d(TAG, "onCreateView: 데이터 호출 실패")
                    }
                })
            }
        }

        return view
    }

    private fun retrieveUsers(completion: (Boolean) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance().getReference().child("Users")
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: 데이터 호출 성공 : ")
                mList.clear()
                if (snapshot.children.count() > 0) {

                    for (data in snapshot.children) {

                        val user = data.getValue(User::class.java)

                        if (user != null) {
                            mList.add(user)
                        }
                    }
                    if (mList.size > 0) {

                        completion(true)
                    } else {
                        completion(false)
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                completion(false)
                Log.d(TAG, "onCancelled: 데이터 호출 실패 : $error")
            }

        })
    }

    private fun searchUsers(searchKeyword: String, completion: (Boolean) -> Unit) {
        Log.d(TAG, "searchUsers: 진입")
        val query =
            FirebaseDatabase.getInstance().reference
                .child("Users")
                .orderByChild("fullname")
                .startAt(searchKeyword)
                .endAt(searchKeyword + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()

                for (data in snapshot.children) {
                    val user = data.getValue(User::class.java)
                    if (user != null) {
                        mList.add(user)
                    }
                }

                if (mList.size > 0) {

                    completion(true)
                } else {
                    completion(false)
                }

            }

            override fun onCancelled(error: DatabaseError) {
                completion(false)
                Log.d(TAG, "onCancelled: 데이터 호출 실패 : $error")
            }

        })
    }


    private fun recyclerViewSetting(users: ArrayList<User>) {
        Log.d(TAG, "recyclerViewSetting: ")
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        recycler_view_search.apply {
            visibility = View.VISIBLE
            this.setHasFixedSize(true)
            this.layoutManager = layoutManager

            userAdapter.submitList(users)
            adapter = userAdapter


        }

    }

    override fun onFollowButtonClick(position: Int, buttonText: String) {

        if (!mList.isNullOrEmpty() && mList.size > 0) {

            Log.d(TAG, "onFollowButtonClick: 버튼 텍스트 $buttonText")
            when (buttonText) {
            
                
                "Follow" -> {
                    getFirebaseUser()?.uid.let {
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it.toString())
                            .child("Following").child(mList[position].getUid())
                            .setValue(true).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Log.d(TAG, "onFollowButtonClick: follow 성공")

                                    getFirebaseUser()?.uid.let { it2 ->

                                        FirebaseDatabase.getInstance().reference
                                            .child("Follow").child(mList[position].getUid())
                                            .child("Followers").child(it2.toString())
                                            .setValue(true).addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Log.d(TAG, "onFollowButtonClick: Followers 성공")
                                                    getUserData(completion = {username ->
                                                        addNotification(mList[position].getUid(), username)
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
                            .child("Following").child(mList[position].getUid())
                            .removeValue().addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Log.d(TAG, "onFollowButtonClick: follow 제거 성공")

                                    getFirebaseUser()?.uid.let { it2 ->

                                        FirebaseDatabase.getInstance().reference
                                            .child("Follow").child(mList[position].getUid())
                                            .child("Followers").child(it2.toString())
                                            .removeValue().addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    Log.d(TAG, "onFollowButtonClick: Followers 제거 성공")
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