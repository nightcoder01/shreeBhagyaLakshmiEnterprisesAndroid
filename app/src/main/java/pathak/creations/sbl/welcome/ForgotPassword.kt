package pathak.creations.sbl.welcome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import br.com.ilhasoft.support.validation.Validator
import pathak.creations.sbl.R
import pathak.creations.sbl.databinding.ForgotPasswordBinding

class ForgotPassword : AppCompatActivity() {

    lateinit var forgotPasswordBinding: ForgotPasswordBinding
    lateinit var forgotVM: ForgotVM
    var validator: Validator = Validator(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotPasswordBinding = DataBindingUtil.setContentView(this, R.layout.forgot_password)
        validator = Validator(forgotPasswordBinding)
        forgotVM = ForgotVM(this, validator)
        forgotPasswordBinding.forgotVM = forgotVM



    }
}