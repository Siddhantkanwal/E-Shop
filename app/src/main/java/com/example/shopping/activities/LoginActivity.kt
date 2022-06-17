package com.example.shopping.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import com.example.shopping.R
import com.example.shopping.firestore.FirestoreClass
import com.example.shopping.models.Constants
import com.example.shopping.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        tv_register.setOnClickListener {
            val intent= Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        btn_login.setOnClickListener {
            logInRegisteredUser()
        }
        tv_forgot_password.setOnClickListener {
            val intent= Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
    fun userLoggedInSuccess(user:User){
        hideProgressDialog()

        if(user.profileCompleted==0){
            val intent=Intent(this,UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
        }else{
            val intent=Intent(this,DashboardActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        finishAffinity()

    }
    private fun logInRegisteredUser(){
        if(validateLoginDetails()){
            showProgressDialog(resources.getString(R.string.please_wait))
            val email=et_email.text.toString().trim { it<=' ' }
            val password=et_password.text.toString().trim { it<=' ' }
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener { task->
                    hideProgressDialog()
                    if(task.isSuccessful){
                        showErrorSnackBar("Your logged in successfully.",false)
                        FirestoreClass().getUserDetails(this)
                    }
                    else{
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }
                }
        }
    }
    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                // showErrorSnackBar("Your details are valid.", false)
                true
            }
        }
    }
}