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
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.core.view.MenuItemCompat
import androidx.databinding.ObservableBoolean
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.my_cart_layout.*
import org.json.JSONArray
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
import kotlin.collections.ArrayList
import kotlin.time.ExperimentalTime


class DashBoard : AppCompatActivity(), RetrofitResponse ,LocationClicked {

    override fun clicked(boolean: Boolean) {
        locationClickListener.value = "true"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    var isLocChecked: ObservableBoolean = ObservableBoolean(false)

     var locationClickListener : MutableLiveData<String> = MutableLiveData()
    var locationClicked : LiveData<String> = locationClickListener

    private lateinit var navController: NavController


    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dash_board)


        val dNow = Date()
        val ft = SimpleDateFormat("yyMMddhhmmssMs")
        val currentDate = ft.format(dNow)


        //210706122328728
        //210706123413713
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
         navController = findNavController(R.id.nav_host_fragment)

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
           //callAllServices()
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

            badgeLayout.setOnClickListener {
                Log.e("badgeLayout=", "======1111=====")
                if(navController.currentDestination!!.label!="My Cart") {
                navController.navigate(R.id.nav_cart)
            }
        }

        val tv = badgeLayout.findViewById<View>(R.id.actionbar_notifcation_textview) as TextView

        wordViewModel.allOrders.observe(this, Observer { dist ->
            size = (dist.size+1).toString()

            val dNow = Date()
            val ft = SimpleDateFormat("yyyy")
            val year = ft.format(dNow)

            size = if(size.length!=2){"000$size"}else{"00$size"}
            transactionNo = "SO/${PreferenceFile.retrieveKey(this,CommonKeys.SELECTED_DISTRIBUTOR)}/$year/$size"

        })



        wordViewModel.allCart.observe(this, Observer { dist ->
            // Update the cached copy of the words in the adapter.

            var cartItem = 0
            dist?.let {

                listCart.clear()
                cartItem = it.size
                var count = 0
                for(i in it.indices)
                {
                    if(it[i].offline_status=="online") count += 1
                }
                listCart.addAll(it)
                tv.text = count.toString()

            }

            logOut.setOnMenuItemClickListener {


                if(CommonMethods.isNetworkAvailable(this)) {

                    if(cartItem>0)
                    {
                        callCartAlert()
                    }
                    else
                    {
                        callLogoutDialog()
                    }
                }
                else
                {
                    if(cartItem>0)
                    {
                        callSyncAlert()
                    }
                    else
                    { callLogoutDialog() }
                }
                true

            }

        })



        locationIs.setOnMenuItemClickListener {

            showSettingsAlert()

            true

        }
        refresh.setOnMenuItemClickListener {

            callAllServices()

            true

        }


        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.label == "Home") {
                logOut.isVisible = true
                refresh.isVisible = true
            } else {
                logOut.isVisible = false
                refresh.isVisible = false

                if (destination.label == "Add Sales Order") {

                    locationIs.isVisible = false
                }
            }
        }



        return true
    }

    var transactionNo = ""
    var size = ""
    var listCart : ArrayList<Cart> = ArrayList()

    private fun callPlaceOrder(listCart: ArrayList<Cart>) {
        try {

            val jsonMain = JSONObject()

            val jsonArray = JSONArray()

            val json = JSONObject()

            val dNow = Date()
            val ft = SimpleDateFormat("yyMMddhhmmssMs")
            val datetime = ft.format(dNow)

            for(i in 0 until listCart.size) {


                json.put("dist_code", listCart[i].distID)
                json.put("dist", listCart[i].dist_name)
                json.put("retailer_code", listCart[i].retailer_code)
                json.put("retailer", listCart[i].retailer_name)
                json.put("beatname", listCart[i].beatName)
                json.put("catgroup", listCart[i].cat_group)
                json.put("category", listCart[i].category)
                json.put("category_code", listCart[i].cat_code)
                json.put("category_description", listCart[i].name)
                json.put("qty", listCart[i].itemCount)
                json.put("ptr_price", listCart[i].ptr_price)
                json.put("ptd_price", listCart[i].ptd_price)
                json.put("total_ptr_price", listCart[i].overAllPrice)
                json.put("total_ptd_price", listCart[i].ptd_total)



                wordViewModel.insertOrders(
                    Orders(datetime+i
                        ,""
                        ,""
                        ,""
                        ,""
                        ,transactionNo
                        ,""
                        ,listCart[i].distID
                        ,listCart[i].dist_name
                        ,listCart[i].retailer_name
                        ,listCart[i].retailer_code
                        ,listCart[i].beatName
                        ,""
                        ,listCart[i].cat_group
                        ,listCart[i].category
                        ,listCart[i].cat_code
                        ,listCart[i].name
                        ,listCart[i].itemCount
                        ,""
                        ,listCart[i].overAllPrice
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,listCart[i].ptr_price
                        ,listCart[i].ptd_price
                        ,""
                        ,""
                        ,""
                    )
                )
            }



            jsonArray.put(json)
            jsonMain.put("items",jsonArray)

            Log.e("fasdfasfdfsd",jsonMain.toString())


            if (CommonMethods.isNetworkAvailable(this)) {

                RetrofitService(
                    this,
                    this,
                    CommonKeys.ADD_CART ,
                    CommonKeys.ADD_CART_CODE,
                    jsonMain,2
                ).callService(true, PreferenceFile.retrieveKey(this, CommonKeys.TOKEN)!!)

            }
            else {

                CommonMethods.alertDialog(this,getString(R.string.checkYourConnection))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callSyncAlert() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Sync online")
        alertDialog.setCancelable(false)
        alertDialog.setMessage("Please go online to sync the orders..FOR TODAY.")
        alertDialog.setPositiveButton("ok") { dialog, which ->
            dialog.dismiss()

        }
        alertDialog.setNegativeButton("cancel")
        {
                dialog, which ->
            dialog.dismiss()
        }

        alertDialog.show()
    }
    private fun callCartAlert() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Sync online")
        alertDialog.setCancelable(false)
        alertDialog.setMessage("Please sync data before logout ..FOR TODAY.")
        alertDialog.setPositiveButton("ok") { dialog, which ->
            dialog.dismiss()
            callPlaceOrder(listCart)
        }
        alertDialog.setNegativeButton("cancel")
        {
                dialog, which ->
            dialog.dismiss()
        }

        alertDialog.show()
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

        deleteDialo.setView(view1)
        val btnYes: TextView = view1.findViewById(R.id.tvYes)
        val btnNo: TextView = view1.findViewById(R.id.tvNo)
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
        wordViewModel.deleteAllOrders()
        wordViewModel.deleteAllTransactions()


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

                        /*{"id":98,"date":"2017-10-10","dist_id":"DST-080","distributor":"H.S.R.TRADERS - HSR LAYOUT",
                            "retailer_id":"DST-080-RM-98","retailer_name":"BANGLORE RICE TRADERS",
                            "beatname":"AGARA, PARIGAPALYA","address":"NO 799 23 RD CROSS SECTOR 2 HSR LAYOUT BLORE- 560102",
                            "phone":null,"mobile":"8025727657","type":"Retailer","note":"","place":"AGARA, PARIGAPALYA",
                            "firstname":null,"lastname":null,"state":"Karnataka","areaname":"Bangalore City","country":"India",
                            "pincode":null,"cst":"","cst_registerationdate":null,"vattin":"","csttin":null,"pan":null,
                            "updated":"2020-06-19 12:20:42","client":"SBL","empname":"secondary","ca":"SAS002","cac":"SLI002",
                            "rid":98,"classification":"A","retailer_type":null,"dvisit":"YES","cperson":null,"email":null,
                            "gstin":null,"sno":9999,"latitude":null,"longitude":null}*/

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
                                        , dataObj.getString("longitude"),false
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
                CommonKeys.ADD_CART_CODE -> {
                    try {

                        Log.e("ADD_CART_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {

                            for(i in 0 until listCart.size)
                            { wordViewModel.deleteCart(listCart[i].cartId) }

                            wordViewModel.deleteAllCart()
                            wordViewModel.deleteAllOrders()
                            wordViewModel.deleteAllTransactions()

                            PreferenceFile.removeAll(this)
                            startActivity(Intent(this, WelcomeActivity::class.java))
                            finishAffinity()
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
