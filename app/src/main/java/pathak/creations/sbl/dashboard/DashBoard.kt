package pathak.creations.sbl.dashboard

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuItemCompat
import androidx.databinding.ObservableBoolean
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
import pathak.creations.sbl.common.LocationClicked
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.data_classes.*
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService
import pathak.creations.sbl.select_distributor.SelectDistributor
import pathak.creations.sbl.welcome.WelcomeActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.ExperimentalTime


class DashBoard : AppCompatActivity(), RetrofitResponse ,LocationClicked {

    override fun clicked(boolean: Boolean) {
        locationClickListener.value = "true"
    }


    private lateinit var appBarConfiguration: AppBarConfiguration

    var isLocChecked: ObservableBoolean = ObservableBoolean(false)


     var locationClickListener : MutableLiveData<String> = MutableLiveData()
    var locationClicked : LiveData<String> = locationClickListener







    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dash_board)


        val dNow = Date()
        val ft = SimpleDateFormat("yyMMddhhmmssMs")
        val currentDate = ft.format(dNow)


        val diff = CommonMethods.getDateDiff(ft,currentDate,PreferenceFile.retrieveKey(this,CommonKeys.CURRENT_DATE)!!)
        Log.e("dfadsfafd","======${PreferenceFile.retrieveKey(this,CommonKeys.CURRENT_DATE)!!}===$dNow==$currentDate==$diff")



        if(diff>0)
        {
            PreferenceFile.removekey(this,CommonKeys.SELECTED_DISTRIBUTOR_NAME)
            PreferenceFile.removekey(this,CommonKeys.SELECTED_DISTRIBUTOR)
            wordViewModel.deleteAllCart()
            startActivity(Intent(this, SelectDistributor::class.java))
            finish()
        }
        else
        {
          /*  PreferenceFile.removekey(this,CommonKeys.SELECTED_DISTRIBUTOR_NAME)
            PreferenceFile.removekey(this,CommonKeys.SELECTED_DISTRIBUTOR)
            wordViewModel.deleteAllCart()
            startActivity(Intent(this, SelectDistributor::class.java))
            finish()*/
        }


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


        if (PreferenceFile.retrieveKey(this, CommonKeys.IS_FIRST_CHECKED).equals("false", false)) {
           callAllServices()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        // Inflate the menu; this adds items to the action bar if it is present.

        menuInflater.inflate(R.menu.dash_board, menu)


        val item = menu.findItem(R.id.action_cart)
        MenuItemCompat.setActionView(item, R.layout.badg_layout)

        val logOut = menu.findItem(R.id.action_LogOut)
        val refresh = menu.findItem(R.id.action_refresh)
        val locationIs = menu.findItem(R.id.action_loc)

         locationIs.isVisible = isLocChecked.get()

        locationClicked.observe(this , Observer {

            Log.e("locationClicked=",it.toString())

            locationIs.isVisible = true


        })


        val badgeLayout = menu.findItem(R.id.action_cart).actionView as RelativeLayout
        val tv = badgeLayout.findViewById<View>(R.id.actionbar_notifcation_textview) as TextView

        wordViewModel.allCart.observe(this, Observer { dist ->
            // Update the cached copy of the words in the adapter.

            dist?.let {

                tv.text = dist.size.toString()

            }
        })





        logOut.setOnMenuItemClickListener {

            callLogoutDialog()

            true

        }
        locationIs.setOnMenuItemClickListener {

            showSettingsAlert()

            true

        }
        refresh.setOnMenuItemClickListener {

            callAllServices()

            true

        }
        return true
    }
    private fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Enable GPS !!")
        alertDialog.setCancelable(false)
        alertDialog.setMessage("Please enable your GPS setting.")
        alertDialog.setPositiveButton("ok") { dialog, which ->
            // permissions()
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        alertDialog.setNegativeButton("cancel")
        {
                dialog, which ->
            dialog.dismiss()
        }

        alertDialog.show()
    }


    private fun callAllServices() {
        try {

            PreferenceFile.storeKeyNull(
                this,
                CommonKeys.IS_FIRST_CHECKED,
                "true",
                ""
            )

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

                Log.e("callDistributorList", "=token====${PreferenceFile.retrieveKey(this, CommonKeys.TOKEN)!!}")

            }
            else {
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
                json.put("dist_id", PreferenceFile.retrieveKey(this,CommonKeys.SELECTED_DISTRIBUTOR))
                RetrofitService(
                    this,
                    this,
                    CommonKeys.GET_RETAILERS,
                    CommonKeys.GET_RETAILERS_CODE,
                    json,2
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
    private fun callCategories() {
        try {

            if (CommonMethods.isNetworkAvailable(this)) {
                val json = JSONObject()

                json.put("dist_id",PreferenceFile.retrieveKey(this,CommonKeys.SELECTED_DISTRIBUTOR))

                RetrofitService(
                    this,
                    this,
                    CommonKeys.CATEGORIES ,
                    CommonKeys.CATEGORIES_CODE
                    ,json,
                    2
                ).callService(true, PreferenceFile.retrieveKey(this, CommonKeys.TOKEN)!!)

                Log.e("callCategories", "=====$json")
                Log.e("callCategories", "=token====${PreferenceFile.retrieveKey(this, CommonKeys.TOKEN)!!}")

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


        wordViewModel.deleteAllCart()


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
                CommonKeys.GET_RETAILERS_CODE -> {
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
                            callCategories()
                        } else {
                            CommonMethods.alertDialog(this, msg)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                CommonKeys.CATEGORIES_CODE -> {
                    try {

                        Log.e("CATEGORIES_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {

                            val data = json.getJSONArray("data")
                            wordViewModel.deleteAllCategories()
                           // listCategories.clear()

                            for (i in 0 until data.length()) {

                                val dataObj = data.getJSONObject(i)


                                if(dataObj.getString("catgroup").isNotEmpty()) {

                                    wordViewModel.insertCategories(
                                        Categories(i.toString(),
                                            dataObj.getString("catgroup"),
                                            dataObj.getString("category"),
                                            dataObj.getString("code"),
                                            dataObj.getString("description"),
                                            dataObj.getString("price"),
                                            dataObj.getString("weight"),
                                            dataObj.getString("ptrflag")))


                                }


                            }


                        }

                        else {
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
