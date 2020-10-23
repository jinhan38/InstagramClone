package com.instagramclone.model

//data class User(
//    val username: String,
//    val fullname: String,
//    val email: String,
//    val image: String,
//    val uid: String,
//    val bio: String
//)

class User {


    private var username: String = ""
    private var fullname: String = ""
    private var email: String = ""
    private var image: String = ""
    private var uid: String = ""
    private var bio: String = ""

    constructor()


    constructor(username: String, fullname: String, email: String, image: String, bio: String) {
        this.username = username
        this.fullname = fullname
        this.email = email
        this.image = image
        this.bio = bio
    }

    fun getUsername(): String {
        return this.username
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun getFullname(): String {
        return this.fullname
    }

    fun setFullname(fullname: String) {
        this.fullname = fullname
    }

    fun getEmail(): String {
        return this.email
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getImage(): String {
        return this.image
    }

    fun setImage(image: String) {
        this.image = image
    }

    fun getBIo(): String {
        return this.bio
    }

    fun setBio(bio: String) {
        this.bio = bio
    }

    fun getUid(): String {
        return this.uid
    }

    fun setUid(uid: String) {
        this.uid = uid
    }


}
