package com.instagramclone.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.instagramclone.Constants.TAG
import com.instagramclone.R
import com.instagramclone.`interface`.IFollowButton
import com.instagramclone.adapter.UserAdapter
import com.instagramclone.adapter.UserAdapterViewHolder
import com.instagramclone.model.User
import com.instagramclone.utils.onMyTextChanged
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*

class SearchFragment : Fragment(), IFollowButton {

    private var mList: ArrayList<User> = ArrayList()
    private val userAdapter = UserAdapter(true, this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search, container, false)

        retrieveUsers {
            when {
                true -> recyclerViewSetting(mList)
                false -> Log.d(TAG, "onCreateView: 데이터 호출 실패")
            }
        }


        view.search_edit_text.onMyTextChanged {

            if (it != null) {

                if (it.isNullOrEmpty()) {
                    Log.d(TAG, "onCreateView: 검색 : $it")
                    recycler_view_search.visibility = View.VISIBLE

                    searchUsers(it.toString().toLowerCase(), completion = {
                        when {
                            true -> {
                                userAdapter.submitList(mList)
                                userAdapter.notifyDataSetChanged()
                            }
                            else -> {
                                Log.d(TAG, "onCreateView: 데이터 호출 실패")
                            }

                        }
                    })

                } else {
                    recycler_view_search.visibility = View.INVISIBLE
                }
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
        val query =
            FirebaseDatabase.getInstance().reference
                .child("Users")
                .orderByChild("fullname")
                .startAt(searchKeyword)
                .endAt(searchKeyword + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange: 데이터 호출 성공 : ")
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
        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        recycler_view_search.apply {
            this.setHasFixedSize(true)
            this.layoutManager = layoutManager

            userAdapter.submitList(users)
            adapter = userAdapter

        }

    }

    override fun onFollowButtonClick(position: Int) {

    }

}