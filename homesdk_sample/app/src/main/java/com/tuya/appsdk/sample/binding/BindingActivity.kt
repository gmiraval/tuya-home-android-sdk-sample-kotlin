package com.tuya.appsdk.sample.binding

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tuya.appsdk.sample.R


class BindingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_binding)

         val token:String = intent.getStringExtra("token").toString()
        Log.d("TAGGG binding", token)

    }
}