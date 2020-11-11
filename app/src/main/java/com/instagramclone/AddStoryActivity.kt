package com.instagramclone

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.instagramclone.activity.MainActivity
import com.instagramclone.utils.Constants.TAG
import com.instagramclone.utils.getFirebaseDatabase
import com.instagramclone.utils.getFirebaseUser
import com.theartofdev.edmodo.cropper.CropImage

class AddStoryActivity : AppCompatActivity() {


    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageStoryRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)


        storageStoryRef = FirebaseStorage.getInstance().reference.child("Story Picture")


        CropImage.activity()
            .setAspectRatio(9, 16)
            .start(this)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK && data != null
        ) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            uploadStory()
        } else {
            finish()
        }
    }

    private fun uploadStory() {

        when {

            imageUri == null ->
                Toast.makeText(this, "이미지를 추가해주세요", Toast.LENGTH_SHORT).show()

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("스토리 추가")
                progressDialog.setMessage("추가중..")
                progressDialog.show()


                val fileRef =
                    storageStoryRef!!.child(System.currentTimeMillis().toString() + ".jpg")
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


                        val ref = getFirebaseDatabase().reference.child("Story")
                        val storyId = ref.push().key.toString()

                        val  timeEnd =  System.currentTimeMillis() + 86400000 // one day

                        val storyMap = HashMap<String, Any>()
                        storyMap["imageurl"] = myUrl
                        storyMap["timestart"] = ServerValue.TIMESTAMP
                        storyMap["timeend"] = timeEnd
                        storyMap["storyid"] = storyId
                        storyMap["userid"] = getFirebaseUser()!!.uid

                        ref.child(storyId).updateChildren(storyMap).addOnCompleteListener {
                            Toast.makeText(this, "포스트 추가 완료", Toast.LENGTH_SHORT).show()

//                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                            progressDialog.dismiss()
                        }.addOnFailureListener{
                            Log.d(TAG, "uploadStory: error : $it")
                            progressDialog.dismiss()
                        }


                    }
                }

            }
        }
    }
}