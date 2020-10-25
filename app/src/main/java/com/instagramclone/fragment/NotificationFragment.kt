package com.instagramclone.fragment

import android.os.Bundle
import android.util.Log
import com.instagramclone.utils.Constants.TAG
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.instagramclone.R
import com.instagramclone.adapter.NotificationAdapter
import com.instagramclone.model.Notification
import com.instagramclone.utils.getFirebaseDatabase
import com.instagramclone.utils.getFirebaseUser
import kotlinx.android.synthetic.main.fragment_notification.*
import kotlinx.android.synthetic.main.fragment_notification.view.*


class NotificationFragment : Fragment() {

    private var notificationList = ArrayList<Notification>()
    private var notificationAdapter = NotificationAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_notification, container, false)
        notificationAdapter.submitList(notificationList = notificationList)
        Log.d(TAG, "onCreateView: notificationList : $notificationList")

        readNotification(completion = {
            view.recycler_view_notifications.apply {
                val layoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                this.layoutManager = layoutManager
                adapter = notificationAdapter


            }
        })

        return view
    }

    private fun readNotification(completion: () -> Unit) {
        val notificationRef = getFirebaseDatabase().reference.child("Notifications").child(
            getFirebaseUser()!!.uid
        )

        notificationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val notification = data.getValue(Notification::class.java)
                        Log.d(TAG, "onDataChange: ispost 확인 : ${notification!!.ispost}")
                        Log.d(TAG, "onDataChange: ${notification!!.text}")
                        Log.d(TAG, "onDataChange: ${notification!!.postId}")
                        Log.d(TAG, "onDataChange: ${notification!!.userId}")
                        notificationList.add(notification!!)

                    }
                    notificationList.reverse()
                    notificationAdapter.notifyDataSetChanged()
                    Log.d(TAG, "onDataChange: notificationList : $notificationList")
                    completion()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}