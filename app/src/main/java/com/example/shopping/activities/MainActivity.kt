package com.example.shopping.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.shopping.R
import com.example.shopping.models.Constants
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences=getSharedPreferences(Constants.MYSHOPPAL_PREFERENCES,Context.MODE_PRIVATE)
        val username=sharedPreferences.getString(Constants.LOGGED_IN_USERNAME," ")!!
        tv_main.text="Hello $username"
    }
}