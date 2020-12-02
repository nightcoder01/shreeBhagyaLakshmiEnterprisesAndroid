package pathak.creations.sbl.welcome

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import br.com.ilhasoft.support.validation.Validator
import pathak.creations.sbl.dashboard.DashBoard

class WelcomeVM(
    welcomeActivity: WelcomeActivity,
   var  validator: Validator
) : ViewModel() {





    var email = ""
    var password = ""


    fun loginClick(view: View) {
        if (validator.validate()) {

            view.context.startActivity(Intent(view.context, DashBoard::class.java))
        }
    }


    fun forgetPass(view: View) {
        ///forget()
    }

    fun rememberMe(view: View) {
        ///remember()
    }

}