package pathak.creations.sbl.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import pathak.creations.sbl.R
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.welcome.WelcomeActivity

class DashBoard : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dash_board)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT).setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_sales_order, R.id.nav_retailer_master,
                R.id.nav_tools, R.id.nav_retailer, R.id.nav_send
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        // Inflate the menu; this adds items to the action bar if it is present.

        menuInflater.inflate(R.menu.dash_board, menu)

        val logOut = menu.findItem(R.id.action_LogOut)
        logOut.setOnMenuItemClickListener {

            callLogoutDialog()

            true

        }



        return true
    }


    private fun callLogoutDialog() {
        val inflater = LayoutInflater.from(this)
        val view1 = inflater.inflate(R.layout.logout_alert, null)
        val deleteDialo = AlertDialog.Builder(this).create()

        val btnYes: TextView
        val btnNo: TextView

        deleteDialo.setView(view1)
        btnYes = view1.findViewById(R.id.tvYes)
        btnNo = view1.findViewById(R.id.tvNo)
        btnYes.setOnClickListener { view2 ->

            deleteDialo.dismiss()
            callLogout()
        }
        btnNo.setOnClickListener { view22 -> deleteDialo.dismiss() }

        deleteDialo.show()

        Log.e("msg", "profile")
    }

    private fun callLogout() {

        PreferenceFile.removeAll(this)
        startActivity(Intent(this, WelcomeActivity::class.java))
        finishAffinity()

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
