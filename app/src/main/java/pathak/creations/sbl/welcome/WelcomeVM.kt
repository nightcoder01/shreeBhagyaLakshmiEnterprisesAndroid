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
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService
import pathak.creations.sbl.select_distributor.SelectDistributor

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
                            PreferenceFile.storeKeyNull(
                                context,
                                CommonKeys.IS_LOCATION_CHECKED,
                                "false",
                                ""
                            )

                            PreferenceFile.storeKeyNull(
                                context,
                                CommonKeys.IS_FIRST_CHECKED,
                                "false",
                                ""
                            )

                            CommonMethods.alertDialogIntentClear(
                                context,
                                msg,
                                SelectDistributor::class.java
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