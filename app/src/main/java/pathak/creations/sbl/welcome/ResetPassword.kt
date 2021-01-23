package pathak.creations.sbl.welcome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import br.com.ilhasoft.support.validation.Validator
import pathak.creations.sbl.R
import pathak.creations.sbl.databinding.ResetPasswordBinding

class ResetPassword : AppCompatActivity() {

    lateinit var resetPasswordBinding: ResetPasswordBinding
    lateinit var resetVM: ResetVM
    var validator: Validator = Validator(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resetPasswordBinding = DataBindingUtil.setContentView(this, R.layout.reset_password)
        validator = Validator(resetPasswordBinding)
        resetVM = ResetVM(this, validator)
        resetPasswordBinding.resetVM = resetVM


    }
}
