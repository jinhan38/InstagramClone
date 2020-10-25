package com.instagramclone.activity

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.instagramclone.R
import com.instagramclone.utils.getFirebaseDatabase
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_add_post.*

@Suppress("UNREACHABLE_CODE")
class AddPostActivity : AppCompatActivity() {

    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Post Picture")



        save_info_add_post_btn.setOnClickListener {
            uploadImage()
        }


        CropImage.activity()
            .setAspectRatio(2, 1)
            .start(this)
    }

    private fun uploadImage() {
        if (imageUri == null) Toast.makeText(this, "이미지를 추가해주세요", Toast.LENGTH_SHORT).show()
        when {

            TextUtils.isEmpty(description_post.text.toString()) -> Toast.makeText(
                this,
                "설명을 적어주세요",
                Toast.LENGTH_SHORT
            ).show()

            else->{
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("포스트 추가")
                progressDialog.setMessage("추가중..")
                progressDialog.show()


                val fileRef = storagePostPicRef!!.child(System.currentTimeMillis().toString() + ".jpg")
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
                    if (it.isSuccessful){


                        val downloadUrl = it.result
                        myUrl = downloadUrl.toString()


                        val ref = getFirebaseDatabase().reference.child("Posts")
                        val postId = ref.push().key.toString()
                        
                        val postMap = HashMap<String, Any>()
                        postMap["postId"] = postId
                        postMap["description"] = description_post.text.toString()
                        postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                        postMap["postImage"] = myUrl

                        ref.child(postId).updateChildren(postMap).addOnCompleteListener {
                            Toast.makeText(this, "포스트 추가 완료", Toast.LENGTH_SHORT).show()

                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }


                        progressDialog.dismiss()
                    }
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
            image_post.setImageURI(imageUri)
        }             else {
            finish()
        }
    }
}