package com.instagramclone.model

import com.instagramclone.utils.getFirebaseUser

data class Story(
    var imageurl: String = "",
    var timestart: Long = 0,
    var timeend: Long = 0,
    var storyid: String = "",
    var userid: String = getFirebaseUser()!!.uid

)