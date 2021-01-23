package pathak.creations.sbl.welcome

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import br.com.ilhasoft.support.validation.Validator
import org.json.JSONObject
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.dashboard.DashBoard
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService

class WelcomeVM(
    var context: WelcomeActivity,
    var  validator: Validator
) : ViewModel(), RetrofitResponse
{



    var email = ""
    var password = ""


    fun loginClick(view: View) {
        if (validator.validate()) {


            callLogin(view)

            //   view.context.startActivity(Intent(view.context, DashBoard::class.java))
        }
    }

    private fun callLogin(view: View) {
        try {

            if (CommonMethods.isNetworkAvailable(view.context)) {
                val json = JSONObject()
                json.put("username", email.trim())
                json.put("password", password.trim())
                json.put("login_device", "android")
                RetrofitService(
                    view.context,
                    this,
                    CommonKeys.LOGIN,
                    CommonKeys.LOGIN_CODE,
                    json,
                    2
                ).callService(true, "")

                Log.e("LoginActivity_LOG", "=====$json")

            } else {
                CommonMethods.alertDialog(
                    view.context,
                    view.context.getString(R.string.checkYourConnection)
                )
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun forgetPass(view: View) {
        ///forget()
       view.context.startActivity(Intent(view.context,ForgotPassword::class.java))

    }

    fun rememberMe(view: View) {
        ///remember()
    }


    override fun response(code: Int, response: String) {
        try {
            when (code) {

                CommonKeys.LOGIN_CODE -> {
                    try {
                        //{"status":false,"code":422,"message":"The selected username is invalid."}


                        //{"status":true,"code":200,"message":"Logged in successfully",
                        // "data":{"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjIzMTU2YWI5MDkwNjUzMjUxMzY4MzA4MmE5OWUyNDNiNTcwZGFjZDhkZWZkZWJhMTAyO
                        // Tc3ODIwODBlNmE4NTMzNWNhZGUxMjNmMjQ4M2Q1In0.eyJhdWQiOiIzIiwianRpIjoiMjMxNTZhYjkwOTA2NTMyNTEzNjgzMDgyYTk5ZTI0M2I1NzBkYWNkOGRlZmRl
                        // YmExMDI5Nzc4MjA4MGU2YTg1MzM1Y2FkZTEyM2YyNDgzZDUiLCJpYXQiOjE2MDg5Nzg5MDEsIm5iZiI6MTYwODk3ODkwMSwiZXhwIjoxNjQwNTE0OTAxLCJzdWIiOiI
                        // xIiwic2NvcGVzIjpbXX0.fw_kaHWFUWCKDdHJWlND2WWrgHzissnwob1VZaNSqrL1c6jkH_qcJtA_iygTrvy8eZA3LKUZ3FlT31Tq0omS2rnvVvHKM2PVZtNmKgLz0
                        // 9f_mMeBypUL2_56HHBGdClVO3qfaY4fbVOT7kEUUWvr2-Eyc8EZr7uCQzOf7rhp-AzY5eqt39abbijHvv8dKREF6Y9gdqr1X68ppi_rf8DPucHO-ToOs6Uznw42Rq
                        // akKcp61UOCzyomY-mg398b2WBSYaWww6IQsM2m9pG28pJdrNcobxYK48JQdYbcIbQaHnvwaGYMMIwwWT1hjpkdm3omIDGeDxCJheyzpImB5gh5RvTAhaIFLQxZhm
                        // 8xLGegcwF-be21yCGO9Du86qfM0We9Yzs2OKKGpGeV9MJ7Ns2sZ1vb-OiFF9gG8JL5Oy5rQilC7AL4zWsbRw-QejGzpB72x5Qm93KdvNgFX59rkNXP9ygNiTMh
                        // BMacaASoGAjveotnzidZdwh8rWO6yMzJf9b-ehWS4cblviUn7bgfnOg8r-ugI9nYE3Q6gOcV1cY20eYjJVea_gHGxJXnBhdRWiz2JtvXlp9TDc9r5DA93s9CJwP
                        // 6QUO9mP-Xj1pGl3yDDaNtU-oG0hY2mBppL78ZiyJiAstUHHP35XDofUVWeBFNjzgcHA_akW40L4x1lYT8a8A","user":{"id":1,"type":"salesman",
                        // "name":"Test Account","username":"secondary","mobile":"9876543210","email_verified_at":null,"notification_token":"",
                        // "status":1,"login_device":"android","created_at":"2020-12-15 20:47:34","updated_at":"2020-12-26 10:35:01"}}}


                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        val data = json.getJSONObject("data")
                        if (status) {

                            val userData = data.getJSONObject("user")

                            PreferenceFile.storeKeyNull(
                                context,
                                CommonKeys.TOKEN,
                                data.getString("token"),
                                ""
                            )
                            PreferenceFile.storeKeyNull(
                                context,
                                CommonKeys.ID,
                                userData.getString("id"),
                                ""
                            )
                            PreferenceFile.storeKeyNull(
                                context,
                                CommonKeys.TYPE,
                                userData.getString("type"),
                                ""
                            )
                            PreferenceFile.storeKeyNull(
                                context,
                                CommonKeys.NAME,
                                userData.getString("name"),
                                ""
                            )
                            PreferenceFile.storeKeyNull(
                                context,
                                CommonKeys.USERNAME,
                                userData.getString("username"),
                                ""
                            )
                            PreferenceFile.storeKeyNull(
                                context,
                                CommonKeys.PHONE,
                                userData.getString("mobile"),
                                ""
                            )
                            PreferenceFile.storeKeyNull(
                                context,
                                CommonKeys.EMAIL_VERIFIED,
                                userData.getString("email_verified_at"),
                                ""
                            )
                            PreferenceFile.storeKeyNull(
                                context,
                                CommonKeys.NOTIFICATION_TOKEN,
                                userData.getString("notification_token"),
                                ""
                            )
                            PreferenceFile.storeKeyNull(
                                context,
                                CommonKeys.STATUS,
                                userData.getString("status"),
                                ""
                            )

                            CommonMethods.alertDialogIntentClear(
                                context,
                                msg,
                                DashBoard::class.java
                            )

                        } else {
                            CommonMethods.alertDialog(context, msg)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}