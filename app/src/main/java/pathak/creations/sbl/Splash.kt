package pathak.creations.sbl

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.dashboard.DashBoard
import pathak.creations.sbl.select_distributor.SelectDistributor
import pathak.creations.sbl.welcome.WelcomeActivity

class Splash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        Handler(Looper.getMainLooper()).postDelayed({


            if (PreferenceFile.retrieveKey(this, CommonKeys.ID) == null) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {

                if (PreferenceFile.retrieveKey(this, CommonKeys.SELECTED_DISTRIBUTOR) == null)
                {

                    startActivity(Intent(this, SelectDistributor::class.java))
                    finish()
                }
                    else
                    {
                startActivity(Intent(this, DashBoard::class.java))
                finish()
                    }



            }


        },3000)

    }


}
