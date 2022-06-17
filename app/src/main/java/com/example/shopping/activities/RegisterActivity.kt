  package com.example.shopping.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.shopping.R
import com.example.shopping.firestore.FirestoreClass
import com.example.shopping.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*

  class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        tv_login.setOnClickListener {
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        setupActionBar()
        btn_register.setOnClickListener {
            registerUser()
        }
        tv_login.setOnClickListener {
            onBackPressed()
        }
    }
      private fun registerUser(){
          if(validateRegisterDetails()){
              showProgressDialog(resources.getString(R.string.please_wait))
              val email:String=et_email.text.toString().trim { it<=' ' }
              val password:String=et_password.text.toString().trim { it<=' ' }
              FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                  .addOnCompleteListener(
                      OnCompleteListener <AuthResult>{
                          task ->
                          hideProgressDialog()
                          if(task.isSuccessful){
                              val firebaseUser:FirebaseUser=task.result!!.user!!
                              val user= User(
                                  firebaseUser.uid,
                                  et_first_name.text.toString().trim { it<=' ' },
                                  et_last_name.text.toString().trim { it<=' ' },
                                  et_email.text.toString().trim { it<=' ' }
                              )
                              showErrorSnackBar(
                                  "Your re registered successfully .Your id is ${firebaseUser.uid}",
                                  false
                              )
                              FirestoreClass().registerUser(this,user)
//                              FirebaseAuth.getInstance().signOut()
//                              finish()
                          }else{
                              hideProgressDialog()
                              showErrorSnackBar(task.exception!!.message.toString(),true)
                          }
                      }
                  )
          }
      }
      private fun validateRegisterDetails(): Boolean {
          return when {
              TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                  showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                  false
              }

              TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                  showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                  false
              }

              TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                  showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                  false
              }

              TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                  showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                  false
              }

              TextUtils.isEmpty(et_confirm_password.text.toString().trim { it <= ' ' }) -> {
                  showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                  false
              }

              et_password.text.toString().trim { it <= ' ' } != et_confirm_password.text.toString()
                  .trim { it <= ' ' } -> {
                  showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                  false
              }
              !cb_terms_and_condition.isChecked -> {
                  showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                  false
              }
              else -> {
//                    showErrorSnackBar(resources.getString(R.string.registery_successfull), false)
                  true
              }
          }
      }
      private fun setupActionBar() {

          setSupportActionBar(toolbar_register_activity)

          val actionBar = supportActionBar
          if (actionBar != null) {
              actionBar.setDisplayHomeAsUpEnabled(true)
              actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24)
          }

          toolbar_register_activity.setNavigationOnClickListener { onBackPressed() }
      }
      fun userRegistrationSuccess(){
          hideProgressDialog()

          val intent=Intent(this,LoginActivity::class.java)
          startActivity(intent)
      }
}