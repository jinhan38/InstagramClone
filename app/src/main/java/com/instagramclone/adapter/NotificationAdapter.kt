package com.instagramclone.adapter

import android.content.Context
import android.util.Log
import com.instagramclone.utils.Constants.TAG
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instagramclone.R
import com.instagramclone.model.Notification

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapterViewHolder>() {

    private var notificationList = ArrayList<Notification>()
    lateinit var context: Context

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapterViewHolder {
        this.context = parent.context

        return NotificationAdapterViewHolder(
            itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.notification_item_layout, parent, false),
            context = this.context
        )
    }

    override fun onBindViewHolder(holder: NotificationAdapterViewHolder, position: Int) {

        val notification = notificationList[position]
        Log.d(TAG, "onBindViewHolder: ispost 확인 : ${notification.ispost}")
        Log.d(TAG, "onBindViewHolder: ${notification.text}")
        Log.d(TAG, "onBindViewHolder: ${notification.postId}")
        Log.d(TAG, "onBindViewHolder: ${notification.userId}")
        holder.bindWithView(notification)


    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    fun submitList(notificationList: ArrayList<Notification>) {
        this.notificationList = notificationList
    }
}