package com.tuya.appsdk.sample.binding

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.tuya.appsdk.sample.R
import com.tuya.appsdk.sample.resource.HomeModel
import com.tuya.smart.android.user.api.ILoginCallback
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.builder.ActivatorBuilder
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener
import com.tuya.smart.sdk.bean.DeviceBean
import com.tuya.smart.sdk.enums.ActivatorModelEnum
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




class BindingActivity : AppCompatActivity(), View.OnClickListener  {
    companion object {
        const val TAG = "DeviceConfigEZ"
    }

    lateinit var cpiLoading: CircularProgressIndicator
    lateinit var buttonAP: Button

    lateinit var bindToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_binding)

        //config listener de botones
        buttonAP = findViewById(R.id.buttonAP)
        buttonAP.setOnClickListener(this)


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
                bindToken = res.getString("token")

                Log.d("TAGGG binding token", bindToken )
                val tvb: TextView = findViewById<TextView>(R.id.textViewBinding)

                //cambio valor textview a token
                tvb.text = "Binding Token:$bindToken"

                //log binding token response
                Log.d("TAGGG binding response", res.toString())

/*                //login hardcoded para probar  por si lo necesita para el bindind
                val uid = "0000O125"
                val pwd = "12345678"
                val callback =  object : ILoginCallback {
                    override fun onSuccess(user: User?) {
                        Toast.makeText(
                            this@BindingActivity,
                            "Login success",
                            Toast.LENGTH_LONG
                        ).show()

                    }

                    override fun onError(code: String?, error: String?) {
                        TODO("Not yet implemented")
                    }
                }
                TuyaHomeSdk.getUserInstance().loginWithUid("54", uid, pwd, callback)*/

            }
        })





    }

    override fun onClick(v: View?) {
/*        val strSsid = findViewById<EditText>(R.id.etSsid).text.toString()
        val strPassword = findViewById<EditText>(R.id.etPassword).text.toString()*/
        val strSsid = "gmiraval24G"
        val strPassword = "guillote1973"

        v?.id?.let {
            if (it == R.id.buttonAP) {
                //ToDO: implement ap mode binding
                //imprimir token en layout y boton para enviar biding token luego de pasar a wifi del modo AP
                // Start network configuration -- AP mode

                //todo: sacar este hardcoded
                val strSsid = "gmiraval24G"
                val strPassword ="guillote1973"

                val builder = ActivatorBuilder()
                    .setSsid(strSsid)
                    .setContext(v.context)
                    .setPassword(strPassword)
                    .setActivatorModel(ActivatorModelEnum.TY_AP)
                    .setTimeOut(100)
                    .setToken(bindToken)
                    .setListener(object : ITuyaSmartActivatorListener {

                        @Override
                        override fun onStep(step: String?, data: Any?) {
                            Log.i("TAGG", "$step --> $data")
                        }

                        override fun onActiveSuccess(devResp: DeviceBean?) {
                            //cpiLoading.visibility = View.GONE

                            Log.i("TAGG", "Activate success")
                            Toast.makeText(
                                this@BindingActivity,
                                "Activate success",
                                Toast.LENGTH_LONG
                            ).show()

                            finish()
                        }

                        override fun onError(
                            errorCode: String?,
                            errorMsg: String?
                        ) {
                            //cpiLoading.visibility = View.GONE
                            buttonAP.isClickable = true

                            Toast.makeText(
                                this@BindingActivity,
                                "Activate error-->$errorMsg",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    )

                val mTuyaActivator =
                    TuyaHomeSdk.getActivatorInstance().newActivator(builder)

                //Start configuration
                mTuyaActivator.start()

                //Show loading progress, disable btnSearch clickable
                //cpiLoading.visibility = View.VISIBLE
                buttonAP.isClickable = false

                //Stop configuration
//                                mTuyaActivator.stop()
                //Exit the page to destroy some cache data and monitoring data.
//                                mTuyaActivator.onDestroy()


            }
        }
    }

}





