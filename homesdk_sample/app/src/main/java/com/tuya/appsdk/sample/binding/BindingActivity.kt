package com.tuya.appsdk.sample.binding

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.tuya.appsdk.sample.R
//okhttp libs
import okhttp3.Call
import okhttp3.Callback

import okhttp3.OkHttpClient
import okhttp3.Request

import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

import org.json.JSONException
import org.json.JSONObject




class BindingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_binding)


        val token: String = intent.getStringExtra("token").toString()
        Log.d("TAGGG binding", token)

        //get binding token
        ///https://square.github.io/okhttp/recipes/
        ///json parser:
        ///https://developer.android.com/reference/org/json/JSONTokener

        //okhttp -http client
        val client = OkHttpClient()
        val url =
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
                //ojo, response solo puede llerse una vez, sino da error
                //guardar en res y luego usar res.
                val res = JSONObject(response.body!!.string()).getJSONObject("result")

                Log.d("TAGGG binding token", res.getString("token") )
                val tvb: TextView = findViewById<TextView>(R.id.textViewBinding)

                //cambio valor textview a token
                tvb.text = "Binding Token:" + res.getString("token")

                //log binding token response
                Log.d("TAGGG binding response", res.toString())

            }
        })


        //ToDO: implement ap mode binding
        //imprimir token en layout y boton para enviar biding token luego de pasar a wifi del modo AP


    }



}






