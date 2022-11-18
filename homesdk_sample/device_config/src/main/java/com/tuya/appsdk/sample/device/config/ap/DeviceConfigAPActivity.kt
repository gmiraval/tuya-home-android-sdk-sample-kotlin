/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2021 Tuya Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.tuya.appsdk.sample.device.config.ap


import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.tuya.appsdk.sample.device.config.R
import com.tuya.appsdk.sample.resource.HomeModel
import com.tuya.smart.android.user.api.ILoginCallback
import com.tuya.smart.android.user.bean.User
import com.tuya.smart.home.sdk.TuyaHomeSdk
import com.tuya.smart.home.sdk.builder.ActivatorBuilder
import com.tuya.smart.sdk.api.ITuyaActivator
import com.tuya.smart.sdk.api.ITuyaActivatorGetToken
import com.tuya.smart.sdk.api.ITuyaSmartActivatorListener
import com.tuya.smart.sdk.bean.DeviceBean
import com.tuya.smart.sdk.enums.ActivatorModelEnum
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

//okhttp libs
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response



/**
 * Device Configuration AP Mode Sample
 *
 * @author qianqi <a href="mailto:developer@tuya.com">Contact me.</a>
 * @since 2021/1/5 5:13 PM
 */
class DeviceConfigAPActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val TAG = "DeviceConfigEZ"
    }

    lateinit var cpiLoading: CircularProgressIndicator
    lateinit var btnSearch: Button
    lateinit var mToken: String
    lateinit var token: String
    private var mTuyaActivator: ITuyaActivator? = null
    lateinit var strSsid: String
    lateinit var strPassword: String
    lateinit var mContentTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_config_activity)

        //me traigo idtoken de activity anterior
        token = intent.getStringExtra("token").toString()

        val toolbar: Toolbar = findViewById<View>(R.id.topAppBar) as Toolbar
        toolbar.setNavigationOnClickListener {
            finish()
        }
        toolbar.title = getString(R.string.device_config_ap_title)
        mContentTv=findViewById(R.id.content_tv)
        mContentTv.text=getString(R.string.device_config_ap_description)

        cpiLoading = findViewById(R.id.cpiLoading)
        btnSearch = findViewById(R.id.btnSearch)
        btnSearch.setOnClickListener(this)

        Log.d("TAGGG", "idtoken recibido de BFF:$token")

        //vamos a buscar binding token con it token a BFF
        //todo:fix-ver porque no progresa el ap mode-puede ser formato token.
        //ver en branch master formato del mToken
        //okhttp -http client
        val client = OkHttpClient()
        val url = "https://tuyabff.apps.k8s.cablevision-labs.com.ar/tuya/device/paring/token"
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
                Log.d("TAGGG", "error call a BFF")
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle this
                //ojo, response solo puede llamarse una vez, sino da error
                //guardar en res y luego usar res.
                val res = JSONObject(response.body!!.string()).getJSONObject("result")
                mToken = res.getString("region")+res.getString("secret")+res.getString("token")

                Log.d("TAGGG", "binding token mToken: $res")
                Log.d("TAGGG", "binding token mToken: $mToken")

                //log binding token response
                Log.d("TAGGG", res.toString())

                //login hardcoded para probar  por si lo necesita para el bindind - inicio
                val uid = "0000O125"
                val pwd = "12345678"
                val callback =  object : ILoginCallback {
                    override fun onSuccess(user: User?) {
                        Toast.makeText(
                            this@DeviceConfigAPActivity,
                            "Login success",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d("TAGGG","Login success" )
                    }
                    override fun onError(code: String?, error: String?) {
                        Log.d("TAGGG","Login failed" )
                    }
                }
                TuyaHomeSdk.getUserInstance().loginWithUid("54", uid, pwd, callback)
                //login hardcoded para probar  por si lo necesita para el bindind -fin

            }
        })
    }

    override fun onClick(v: View?) {
        strSsid = findViewById<EditText>(R.id.etSsid).text.toString()
        strPassword = findViewById<EditText>(R.id.etPassword).text.toString()

        v?.id?.let {
            if (it == R.id.btnSearch) {


                // Get Network Configuration Token -original
                //https://support.tuya.com/en/help/_detail/K9g77yqai8my9

                val homeId = HomeModel.INSTANCE.getCurrentHome(this)

                Log.d("TAGGG", "homeId: $homeId")
                TuyaHomeSdk.getActivatorInstance().getActivatorToken(homeId,
                        object : ITuyaActivatorGetToken {
                            override fun onSuccess(token: String) {
                                mToken = token
                                // Start network configuration -- AP mode

                                onClickSetting()
                                //Stop configuration
//                                mTuyaActivator.stop()
                                //Exit the page to destroy some cache data and monitoring data.
//                                mTuyaActivator.onDestroy()
                            }

                            override fun onFailure(s: String, s1: String) {

                            }
                        })





            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onRestart() {
        super.onRestart()
        //print vars
        Log.d("TAGGG", "strSsid:$strSsid")
        Log.d("TAGGG", "strPassword:$strPassword")
        Log.d("TAGGG", "mToken:$mToken")
        //Show loading progress, disable btnSearch clickable

        cpiLoading.visibility = View.VISIBLE
        btnSearch.isClickable = false
        cpiLoading.isIndeterminate = true
        val builder = ActivatorBuilder()
                .setSsid(strSsid)
                .setContext(this)
                .setPassword(strPassword)
                .setActivatorModel(ActivatorModelEnum.TY_AP)
                .setTimeOut(100)
                .setToken(mToken)
                .setListener(object : ITuyaSmartActivatorListener {

                    @Override
                    override fun onStep(step: String?, data: Any?) {
                        Log.i(TAG, "$step --> $data")
                    }

                    override fun onActiveSuccess(devResp: DeviceBean?) {
                        cpiLoading.visibility = View.GONE

                        Log.i(TAG, "Activate success")
                        Toast.makeText(
                                this@DeviceConfigAPActivity,
                                "Activate success",
                                Toast.LENGTH_LONG
                        ).show()

                        finish()
                    }

                    override fun onError(
                            errorCode: String?,
                            errorMsg: String?
                    ) {
                        cpiLoading.visibility = View.GONE
                        btnSearch.isClickable = true

                        Toast.makeText(
                                this@DeviceConfigAPActivity,
                                "Activate error-->$errorMsg",
                                Toast.LENGTH_LONG
                        ).show()
                    }
                }
                )
        mTuyaActivator = TuyaHomeSdk.getActivatorInstance().newActivator(builder)
        //Start configuration
        Log.i(TAG, "Activator started")
        mTuyaActivator?.start()



    }

    /**
     *
     * wifi setting
     */
    private fun onClickSetting() {
        var wifiSettingsIntent = Intent("android.settings.WIFI_SETTINGS")
        if (null == wifiSettingsIntent.resolveActivity(packageManager)) {
            wifiSettingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
        }
        if (null == wifiSettingsIntent.resolveActivity(packageManager)){
            return
        }
        startActivity(wifiSettingsIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mTuyaActivator?.onDestroy()
    }

}