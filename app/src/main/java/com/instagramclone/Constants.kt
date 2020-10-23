package com.instagramclone

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

object Constants {

    const val TAG: String = "로그"
    const val BASE_URL =
        "https://firebasestorage.googleapis.com/v0/b/instagramclone-ed298.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=94f49350-beb9-4e46-87a4-6ffcfeed59b2"
}

fun getFirebaseUser(): FirebaseUser? {

    return FirebaseAuth.getInstance().currentUser

}

fun getFirebaseDatabase(): FirebaseDatabase {
    return FirebaseDatabase.getInstance()
}


fun firebaseSignOut() {

}