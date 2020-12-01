package pathak.creations.sbl.welcome

import android.view.View
import androidx.lifecycle.ViewModel
import br.com.ilhasoft.support.validation.Validator

class WelcomeVM(
    welcomeActivity: WelcomeActivity,
   var  validator: Validator
) : ViewModel() {





    var email = ""
    var password = ""


    fun loginClick(view: View) {
        if (validator.validate()) {}
    }


    fun forgetPass(view: View) {
        ///forget()
    }

    fun rememberMe(view: View) {
        ///remember()
    }

}