package pathak.creations.sbl.welcome

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import br.com.ilhasoft.support.validation.Validator

import pathak.creations.sbl.retrofit.RetrofitResponse

class ForgotVM(
    var context: Context,
    var  validator: Validator
)  : ViewModel(), RetrofitResponse
{



    var email = ""


    fun sendClick(view: View) {
        if (validator.validate()) {

            view.context.startActivity(Intent(view.context,ResetPassword::class.java))

        }
    }
    fun backClick(view: View) {


        (context as Activity).onBackPressed()

    }




    override fun response(code: Int, response: String) {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}