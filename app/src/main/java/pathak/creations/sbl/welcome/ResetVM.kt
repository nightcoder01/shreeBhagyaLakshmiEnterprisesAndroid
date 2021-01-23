package pathak.creations.sbl.welcome

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import br.com.ilhasoft.support.validation.Validator
import pathak.creations.sbl.retrofit.RetrofitResponse

class ResetVM(
    var context: Context,
    var  validator: Validator
)  : ViewModel(), RetrofitResponse
{



    var email = ""
    var password = ""
    var cnfPassword = ""


    fun updateClick(view: View) {
        if (validator.validate()) {

            view.context.startActivity(Intent(view.context,WelcomeActivity::class.java))
            (context as Activity).finishAffinity()


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