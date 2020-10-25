package com.instagramclone.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.instagramclone.utils.App
import com.instagramclone.R
import com.instagramclone.`interface`.IFollowButton
import com.instagramclone.fragment.ProfileFragment
import com.instagramclone.model.User
import com.instagramclone.utils.getFirebaseDatabase
import com.instagramclone.utils.getFirebaseUser

class UserAdapter(
    private val isFragment: Boolean = false,
    private val followButton: IFollowButton
) : RecyclerView.Adapter<UserAdapterViewHolder>() {

    private var iFollowButton: IFollowButton? = null
    private var mUser = ArrayList<User>()
    private lateinit var context: Context

    init {
        this.iFollowButton = followButton
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapterViewHolder {


        this.context = parent.context

        return UserAdapterViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.user_item_layout, parent, false),
            this.iFollowButton!!
        )

    }

    override fun onBindViewHolder(holder: UserAdapterViewHolder, position: Int) {

        val user = mUser[position]

        holder.bindWithView(user)

        holder.itemView.setOnClickListener {

//            if (isFragment) {

                val pref = App.instance.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("profileId", user.getUid())
                pref.apply()

                (this.context as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()

//            }
        }


    }


    override fun getItemCount(): Int {
        return mUser.count()
    }


    fun submitList(mUser: ArrayList<User>) {
        this.mUser = mUser
    }

}