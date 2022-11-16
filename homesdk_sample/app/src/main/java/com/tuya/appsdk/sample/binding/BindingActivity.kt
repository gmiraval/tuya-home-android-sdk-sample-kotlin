package com.tuya.appsdk.sample.binding

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSON
import com.tuya.appsdk.sample.R
//okhttp libs
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response;
import java.io.IOException


class BindingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_binding)

        val token: String = intent.getStringExtra("token").toString()
        Log.d("TAGGG binding", token)

        //get binding token
        //https://square.github.io/okhttp/recipes/

        val client: OkHttpClient = OkHttpClient()
        val url: String =
            "https://tuyabff.apps.k8s.cablevision-labs.com.ar/tuya/device/paring/token"
        val payload = " "
        val requestBody = payload.toRequestBody()

        val request = Request.Builder()
            .header("User-Agent", "OkHttp Headers.java")
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Accept", "application/json; q=0.5")
            .method("POST", requestBody)
            .url(url)
            .build()

             client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle this
                Log.d("TAGGG", "error")
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle this

                //log binding token response
                Log.d("TAGGG", response.body!!.string())
            }
        })


        //implement ap mode binding


    }
}

