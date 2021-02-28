package pathak.creations.sbl.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ObservableBoolean
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import pathak.creations.sbl.AppController
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.data_classes.*
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService
import pathak.creations.sbl.welcome.WelcomeActivity

class DashBoard : AppCompatActivity(), RetrofitResponse {


    private lateinit var appBarConfiguration: AppBarConfiguration

    var isLocChecked: ObservableBoolean = ObservableBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dash_board)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
                .setAnimationMode(Snackbar.ANIMATION_MODE_FADE)
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
                R.id.nav_tools, R.id.nav_retailer, R.id.nav_cart
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



        if (PreferenceFile.retrieveKey(this, CommonKeys.IS_LOCATION_CHECKED).equals("true", true)) {
            isLocChecked.set(true)
        } else {
            isLocChecked.set(false)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        // Inflate the menu; this adds items to the action bar if it is present.

        menuInflater.inflate(R.menu.dash_board, menu)

        val logOut = menu.findItem(R.id.action_LogOut)
        val refresh = menu.findItem(R.id.action_refresh)
        val locationIs = menu.findItem(R.id.action_loc)


        locationIs.isVisible = isLocChecked.get()

        logOut.setOnMenuItemClickListener {

            callLogoutDialog()

            true

        }
        locationIs.setOnMenuItemClickListener {

            callLogoutDialog()

            true

        }
        refresh.setOnMenuItemClickListener {

            callAllServices()

            true

        }
        return true
    }


    private fun callAllServices() {
        try {

            //distributor list
            if (CommonMethods.isNetworkAvailable(this)) {
                val json = JSONObject()

                RetrofitService(
                    this,
                    this,
                    CommonKeys.RETAILER_LIST,
                    CommonKeys.RETAILER_LIST_CODE,
                    1
                ).callService(true, PreferenceFile.retrieveKey(this, CommonKeys.TOKEN)!!)

                Log.e("callDistributorList", "=====$json")
                Log.e(
                    "callDistributorList",
                    "=token====${PreferenceFile.retrieveKey(this, CommonKeys.TOKEN)!!}"
                )

            } else {
                CommonMethods.alertDialog(
                    this,
                    getString(R.string.checkYourConnection)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callBeat() {
        try {

            //beat list
            if (CommonMethods.isNetworkAvailable(this)) {
                val json = JSONObject()

                RetrofitService(
                    this,
                    this,
                    CommonKeys.ALL_BEATS,
                    CommonKeys.ALL_BEATS_CODE,
                    1
                ).callService(true, PreferenceFile.retrieveKey(this, CommonKeys.TOKEN)!!)

                Log.e("callBeat", "=====$json")
                Log.e(
                    "callBeat",
                    "=token====${PreferenceFile.retrieveKey(this, CommonKeys.TOKEN)!!}"
                )

            } else {
                CommonMethods.alertDialog(
                    this,
                    getString(R.string.checkYourConnection)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callRetailer() {
        try {

            //retailer list
            if (CommonMethods.isNetworkAvailable(this)) {
                val json = JSONObject()

                RetrofitService(
                    this,
                    this,
                    CommonKeys.ALL_RETAILERS,
                    CommonKeys.ALL_RETAILERS_CODE,
                    1
                ).callService(true, PreferenceFile.retrieveKey(this, CommonKeys.TOKEN)!!)

                Log.e("callRetailer", "=====$json")
                Log.e(
                    "callRetailer",
                    "=token====${PreferenceFile.retrieveKey(this, CommonKeys.TOKEN)!!}"
                )

            } else {
                CommonMethods.alertDialog(
                    this,
                    getString(R.string.checkYourConnection)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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


    override fun response(code: Int, response: String) {
        try {
            when (code) {
                CommonKeys.RETAILER_LIST_CODE -> {
                    try {

                        Log.e("RETAILER_LIST_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {

                            val dataArray = json.getJSONArray("data")

                            wordViewModel.deleteAllDist()

                            for (i in 0 until dataArray.length()) {
                                val dataObj = dataArray.getJSONObject(i)

                                wordViewModel.insertDist(
                                    Distributor(
                                        dataObj.getString("dist_id")
                                        , dataObj.getString("name")
                                    )
                                )
                            }

                            callBeat()

                        } else {
                            CommonMethods.alertDialog(this, msg)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                CommonKeys.ALL_BEATS_CODE -> {
                    try {

                        Log.e("ALL_BEATS_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {

                            val dataArray = json.getJSONArray("data")

                            wordViewModel.deleteAllBeat()

                            for (i in 0 until dataArray.length()) {
                                val dataObj = dataArray.getJSONObject(i)

                                wordViewModel.insertBeat(
                                    Beat(
                                        dataObj.getString("id")
                                        , dataObj.getString("dist_id")
                                        , dataObj.getString("distributor")
                                        , dataObj.getString("state")
                                        , dataObj.getString("areaname")
                                        , dataObj.getString("beatname")
                                    )
                                )
                            }

                            callRetailer()
                        } else {
                            CommonMethods.alertDialog(this, msg)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                CommonKeys.ALL_RETAILERS_CODE -> {
                    try {

                        Log.e("ALL_RETAILERS_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {

                            val data = json.getJSONObject("data")
                            val dataArray = data.getJSONArray("data")



                            wordViewModel.deleteAllRetailer()

                            for (i in 0 until dataArray.length()) {
                                val dataObj = dataArray.getJSONObject(i)

                                wordViewModel.insertRetailer(
                                    Retailer(
                                        dataObj.getString("id")
                                        , dataObj.getString("date")
                                        , dataObj.getString("dist_id")
                                        , dataObj.getString("distributor")
                                        , dataObj.getString("retailer_id")
                                        , dataObj.getString("retailer_name")
                                        , dataObj.getString("beatname")
                                        , dataObj.getString("address")
                                        , dataObj.getString("phone")
                                        , dataObj.getString("mobile")
                                        , dataObj.getString("type")
                                        , dataObj.getString("note")
                                        , dataObj.getString("place")
                                        , dataObj.getString("firstname")
                                        , dataObj.getString("lastname")
                                        , dataObj.getString("state")
                                        , dataObj.getString("areaname")
                                        , dataObj.getString("country")
                                        , dataObj.getString("pincode")
                                        , dataObj.getString("cst")
                                        , dataObj.getString("cst_registerationdate")
                                        , dataObj.getString("vattin")
                                        , dataObj.getString("csttin")
                                        , dataObj.getString("pan")
                                        , dataObj.getString("updated")
                                        , dataObj.getString("client")
                                        , dataObj.getString("empname")
                                        , dataObj.getString("ca")
                                        , dataObj.getString("cac")
                                        , dataObj.getString("rid")
                                        , dataObj.getString("classification")
                                        , dataObj.getString("retailer_type")
                                        , dataObj.getString("dvisit")
                                        , dataObj.getString("cperson")
                                        , dataObj.getString("email")
                                        , dataObj.getString("gstin")
                                        , dataObj.getString("sno")
                                        , dataObj.getString("latitude")
                                        , dataObj.getString("longitude")
                                    )
                                )
                            }
                            //callBeat()

                        } else {
                            CommonMethods.alertDialog(this, msg)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    //data base work
    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as AppController).repository)
    }

}
