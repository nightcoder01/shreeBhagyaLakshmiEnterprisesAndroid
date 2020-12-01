package pathak.creations.sbl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import pathak.creations.sbl.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        Handler(Looper.getMainLooper()).postDelayed({



                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()


        },3000)

    }


}
