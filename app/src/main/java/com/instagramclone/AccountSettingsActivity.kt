package com.instagramclone

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.instagramclone.Constants.BASE_URL
import com.instagramclone.model.User
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import com.instagramclone.Constants.TAG
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class AccountSettingsActivity : AppCompatActivity() {


    private var checker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("profilePicture")

        logout_btn_profile.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        close_profile_btn.setOnClickListener {
//            val intent = Intent(this, SignInActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
            finish()
        }

        save_info_profile_btn.setOnClickListener {
            if (checker == "clicked") {
                uploadImageAndUpdateInfo()
            } else {
                updateUserInfo()
            }
        }

        change_image_text_btn.setOnClickListener {
            checker = "clicked"

            CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this)
        }

        getUserInfo(getFirebaseUser()!!.uid)
    }

    private fun uploadImageAndUpdateInfo() {

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("프로필 수정")
        progressDialog.setMessage("업데이트중..")
        progressDialog.show()

        when {
            TextUtils.isEmpty(full_name_profile_frag.text.toString()) -> {
                Toast.makeText(this, "fullname을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(username_profile_frag.text.toString()) -> {
                Toast.makeText(this, "username을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(bio_profile.text.toString()) -> {
                Toast.makeText(this, "bio를 입력해주세요", Toast.LENGTH_SHORT).show()
            }

            imageUri == null -> {
                Toast.makeText(this, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
            else -> {
                val fileRef = storageProfilePicRef!!.child(getFirebaseUser()!!.uid + ".jpg")
                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener {

                    if (it.isSuccessful) {
                        val downloadUrl = it.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()

                        userMap["fullname"] =
                            full_name_profile_frag.text.toString().toLowerCase()
                        userMap["username"] =
                            username_profile_frag.text.toString().toLowerCase()
                        userMap["bio"] = bio_profile.text.toString().toLowerCase()
                        userMap["image"] = myUrl

                        ref.child(getFirebaseUser()!!.uid).updateChildren(userMap)
                            .addOnCompleteListener {
                                Log.d(TAG, "uploadImageAndUpdateInfo: 컴플릿")
                                if (it.isSuccessful) {

                                    Log.d(TAG, "updateUserInfo: 수정 성공")
                                    Toast.makeText(App.instance, "수정 완료", Toast.LENGTH_SHORT)
                                        .show()
                                    startActivity(
                                        Intent(
                                            App.instance,
                                            MainActivity::class.java
                                        )
                                    )
                                    finish()

                                }
                            }

                    } else {
                        progressDialog.dismiss()
                    }
//                    OnCompleteListener<Uri> { task ->
//
//                    }

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK && data != null
        ) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            profile_image_view.setImageURI(imageUri)
        }
    }

    private fun updateUserInfo() {
        when {
            TextUtils.isEmpty(full_name_profile_frag.text.toString()) -> {
                Toast.makeText(this, "fullname을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(username_profile_frag.text.toString()) -> {
                Toast.makeText(this, "username을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(bio_profile.text.toString()) -> {
                Toast.makeText(this, "bio를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else -> {

                val userRef = getFirebaseDatabase().reference.child("Users")

                val userMap = HashMap<String, Any>()

                userMap["fullname"] = full_name_profile_frag.text.toString().toLowerCase()
                userMap["username"] = username_profile_frag.text.toString().toLowerCase()
                userMap["bio"] = bio_profile.text.toString().toLowerCase()


                userRef.child(getFirebaseUser()!!.uid).updateChildren(userMap)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d(TAG, "updateUserInfo: 수정 성공")
                            Toast.makeText(App.instance, "수정 완료", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(App.instance, MainActivity::class.java))
                            finish()
                        }
                    }

            }
        }

    }

    fun getUserInfo(uid: String) {

        val userRef = uid.let {
            FirebaseDatabase.getInstance().reference.child("Users")
                .child(it)

        }

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    val user = snapshot.getValue(User::class.java)

                    Glide.with(App.instance)
                        .load(user?.getImage())
                        .placeholder(R.drawable.profile)
                        .into(profile_image_view)

                    full_name_profile_frag.setText(user?.getFullname())
                    username_profile_frag.setText(user?.getUsername())
                    bio_profile.setText(user?.getBIo())
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}