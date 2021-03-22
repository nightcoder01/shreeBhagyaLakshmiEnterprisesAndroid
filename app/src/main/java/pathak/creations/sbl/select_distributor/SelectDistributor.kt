package pathak.creations.sbl.select_distributor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_select_distributor.*
import kotlinx.android.synthetic.main.custom_spinner.view.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONObject
import pathak.creations.sbl.AppController
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.custom_adapter.SpinnerCustomDistributorAdapter
import pathak.creations.sbl.dashboard.DashBoard
import pathak.creations.sbl.data_classes.*
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SelectDistributor : AppCompatActivity(), RetrofitResponse {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_distributor)

        tvHeading.text = "Select Distributor"
       // if (PreferenceFile.retrieveKey(this@SelectDistributor, CommonKeys.IS_FIRST_CHECKED).equals("false", false)) {
            callAllServices()
      //  }

        //set live data observer
        wordViewModel.allDistributor.observe(this, Observer { dist ->
            // Update the cached copy of the words in the adapter.

            dist?.let {

                setDistributorAdapter(it)

            }
        })

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



    private fun setDistributorAdapter(
        list: List<Distributor>

    ) {
        tvDistributor2.setOnClickListener {
            openDistributorShort(tvDistributor2,list)
        }    }

    var popupWindow: PopupWindow? = null


    private fun openDistributorShort(
        view: TextView,
        listDist: List<Distributor>

    ) {
        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.custom_spinner, null)

        popupWindow = PopupWindow(
            customView,
            view.width,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val adapter = SpinnerCustomDistributorAdapter(listDist)
        customView.rvSpinner.adapter = adapter
        adapter.onClicked(object : SpinnerCustomDistributorAdapter.CardInterface{
            override fun clickedSelected(position: Int) {

                view.text =listDist[position].distName
                popupWindow!!.dismiss()


                PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.SELECTED_DISTRIBUTOR,listDist[position].distID)
                PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.SELECTED_DISTRIBUTOR_NAME,listDist[position].distName)


                val dNow = Date()
                val ft = SimpleDateFormat("yyMMddhhmmssMs")
                val currentDate = ft.format(dNow)
                PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.CURRENT_DATE,currentDate)




                val it = Intent(this@SelectDistributor, DashBoard::class.java)
                it.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(it)




            }
        })

        customView.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(s.isNullOrBlank())
                {
                    val adapter2 = SpinnerCustomDistributorAdapter(listDist)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomDistributorAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =listDist[position].distName
                            popupWindow!!.dismiss()
                            PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.SELECTED_DISTRIBUTOR,listDist[position].distID)
                            PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.SELECTED_DISTRIBUTOR_NAME,listDist[position].distName)

                            val dNow = Date()
                            val ft = SimpleDateFormat("yyMMddhhmmssMs")
                            val currentDate = ft.format(dNow)
                            PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.CURRENT_DATE,currentDate)

                            /*if (PreferenceFile.retrieveKey(this@SelectDistributor, CommonKeys.IS_FIRST_CHECKED).equals("false", false)) {
                               // callAllServices()
                            }*/
                            val it = Intent(this@SelectDistributor, DashBoard::class.java)
                            it.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(it)

                          //  callBeatList(listDist[position].distID)
                            //  callBeatRetailer(listBeats[position].dist_id,listBeats[position].beatname)
                        }
                    })
                }
                else
                {

                    val list : ArrayList<Distributor> = ArrayList()

                    for(i in listDist.indices)
                    {
                        if(listDist[i].distName.toLowerCase().contains(s.toString().toLowerCase(),false))
                        {
                            list.add(listDist[i])

                        }
                    }


                    val adapter2 = SpinnerCustomDistributorAdapter(list)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomDistributorAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =list[position].distName
                            popupWindow!!.dismiss()
                            PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.SELECTED_DISTRIBUTOR,list[position].distID)
                            PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.SELECTED_DISTRIBUTOR_NAME,list[position].distName)

                            val dNow = Date()
                            val ft = SimpleDateFormat("yyMMddhhmmssMs")
                            val currentDate = ft.format(dNow)
                            PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.CURRENT_DATE,currentDate)

                            /*if (PreferenceFile.retrieveKey(this@SelectDistributor, CommonKeys.IS_FIRST_CHECKED).equals("false", false)) {
                                callAllServices()
                            }*/

                            val it = Intent(this@SelectDistributor, DashBoard::class.java)
                            it.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(it)

                          //  callBeatList(list[position].distID)

                            // callBeatRetailer(list[position].dist_id,list[position].beatname)
                        }
                    })
                }
            }
        })

        popupWindow!!.isOutsideTouchable = true

        popupWindow!!.showAsDropDown(view)
        popupWindow!!.isFocusable = true
        popupWindow!!.update()

    }

    private fun callDistributor() {
        try {

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
                                            dataObj.getString("ptrflag"))
                                    )


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
