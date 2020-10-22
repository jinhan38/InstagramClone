package com.instagramclone

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "SignUpActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        signup_link_btn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        signup_btn.setOnClickListener {
            CreateAccount()
        }
    }

    private fun CreateAccount() {

        val fullName = full_name_signup.text.toString()
        val userName = user_name_signup.text.toString()
        val email = email_signup.text.toString()
        val password = password_signup.text.toString()


        when {
            TextUtils.isEmpty(fullName) -> {
                Toast.makeText(this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(userName) -> {
                Toast.makeText(this, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(email) -> {
                Toast.makeText(this, "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            }

            else -> {

                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("회원가입 중...")
                progressDialog.setMessage("잠시만 기다려주세요")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()


                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            saveUserInfo(fullName, userName, email, password, progressDialog)
                        } else {
                            val message = task.exception.toString()
                            Toast.makeText(this, "회원가입 실패 erroe : $message", Toast.LENGTH_SHORT)
                                .show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }

                    }
            }
        }
    }

    private fun saveUserInfo(
        fullName: String,
        userName: String,
        email: String,
        password: String,
        progressDialog: ProgressDialog
    ) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        Log.d(TAG, "saveUserInfo: uid $currentUserID")
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName.toLowerCase()
        userMap["username"] = userName.toLowerCase()
        userMap["email"] = email
        userMap["bio"] = "인스타 클론앱 제작중"
        userMap["image"] =
            "https://firebasestorage.googleapis.com/v0/b/instagramclone-ed298.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=94f49350-beb9-4e46-87a4-6ffcfeed59b2"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    Toast.makeText(this, "회원가입 완료", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, SignInActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "회원가입 실패 error : ${task.exception}", Toast.LENGTH_SHORT).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }
    }
}