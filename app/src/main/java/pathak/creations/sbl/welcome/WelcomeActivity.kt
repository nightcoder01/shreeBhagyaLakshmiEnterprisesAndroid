package pathak.creations.sbl.welcome

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import br.com.ilhasoft.support.validation.Validator
import pathak.creations.sbl.R
import pathak.creations.sbl.databinding.WelcomeBinding

class WelcomeActivity:AppCompatActivity(){


    lateinit var welcomeBinding: WelcomeBinding
    lateinit var welcomeVM: WelcomeVM
    var validator: Validator = Validator(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        welcomeBinding = DataBindingUtil.setContentView(this, R.layout.welcome)
       

        validator = Validator(welcomeBinding)
        welcomeVM = WelcomeVM(this, validator)
        welcomeBinding.welcomeVM = welcomeVM

    }

}