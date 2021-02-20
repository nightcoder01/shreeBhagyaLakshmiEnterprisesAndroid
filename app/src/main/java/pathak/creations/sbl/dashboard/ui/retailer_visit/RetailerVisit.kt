package pathak.creations.sbl.dashboard.ui.retailer_visit

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.custom_spinner.view.*
import kotlinx.android.synthetic.main.logout_alert.*
import kotlinx.android.synthetic.main.retailer_visit.*
import org.json.JSONObject
import pathak.creations.sbl.AppController
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.GPSTracker
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.custom_adapter.SpinnerCustomAdapter
import pathak.creations.sbl.custom_adapter.SpinnerCustomDistributorAdapter
import pathak.creations.sbl.data_class.BeatData
import pathak.creations.sbl.data_class.BeatRetailerData
import pathak.creations.sbl.data_classes.Distributor
import pathak.creations.sbl.data_classes.WordViewModel
import pathak.creations.sbl.data_classes.WordViewModelFactory
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService

class RetailerVisit : Fragment(), RetrofitResponse {


    private lateinit var retailerVisitVM: RetailerVisitVM

    var list : ArrayList<RetailerVisitData> = ArrayList()
    var listBeats : ArrayList<BeatData> = ArrayList()


    var distributor: String = ""
    var distributorId: String = ""
    var beat: String = ""
    var listBeatsRetailer : ArrayList<BeatRetailerData> = ArrayList()
    var listBeatsRetailerFilter : ArrayList<BeatRetailerData> = ArrayList()

  //  var listDistName : ArrayList<String> = ArrayList()
  //  var listDistId : ArrayList<String> = ArrayList()

    var ctx : Context? = null

    lateinit var adapter : RetailerVisitAdapter

    private var latitude : Double = 0.0
    private var longitude : Double = 0.0
    private var gpsTracker: GPSTracker? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retailerVisitVM = ViewModelProvider(this).get(RetailerVisitVM::class.java)
        val root = inflater.inflate(R.layout.retailer_visit, container, false)

        ctx = root.context

        retailerVisitVM.dateValue.observe(viewLifecycleOwner, Observer {
            tvDateValue.text = it
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDate.setOnClickListener { retailerVisitVM.datePicker(view) }
        tvDateValue.setOnClickListener { retailerVisitVM.datePicker(view) }
        tvAdd.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_add_visit) }


        //callBeatList(PreferenceFile.retrieveKey(ctx!!,CommonKeys.NAME))

        setDistributor()
        if(beat.isNotEmpty()) {

            tvDistributor2.hint = distributor
            tvBeatName2.hint = beat

            callBeatList(distributorId)

        }



        // setCountList()

        setSearch()




        //set live data observer
        wordViewModel.allDistributor.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.


            dist?.let {

                setDistributorAdapter(it)

            }
        })

    }

    private fun setSearch() {
        etSearch.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                if(s.isNullOrEmpty())
                {
                    setBeatRetailerAdapter(listBeatsRetailer)
                }
                else
                {
                    listBeatsRetailerFilter.clear()


                    listBeatsRetailerFilter.addAll(listBeatsRetailer.filter
                    { it.retailer_name.toLowerCase().contains(s.toString().toLowerCase())
                            ||
                            it.beatname.toLowerCase().contains(s.toString().toLowerCase())
                            ||
                            it.distributor.toLowerCase().contains(s.toString().toLowerCase())
                    })


                    setBeatRetailerAdapter(listBeatsRetailerFilter)

                }






                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun setDistributor() {
        if(PreferenceFile.retrieveKey(ctx!!,CommonKeys.TYPE).equals("distributor"))
        {
            tvDistributor2.hint = PreferenceFile.retrieveKey(ctx!!,CommonKeys.NAME)
            distributor = PreferenceFile.retrieveKey(ctx!!,CommonKeys.NAME)!!
            callBeatList(PreferenceFile.retrieveKey(ctx!!,CommonKeys.NAME))
        }
        else
        {
            callDistributorList()
        }
    }

    private fun callDistributorList() {
        try {

            if (CommonMethods.isNetworkAvailable(ctx!!)) {
                val json = JSONObject()

                RetrofitService(
                    ctx!!,
                    this,
                    CommonKeys.RETAILER_LIST ,
                    CommonKeys.RETAILER_LIST_CODE,
                    1
                ).callService(true, PreferenceFile.retrieveKey(ctx!!, CommonKeys.TOKEN)!!)

                Log.e("callDistributorList", "=====$json")
                Log.e("callDistributorList", "=token====${PreferenceFile.retrieveKey(ctx!!, CommonKeys.TOKEN)!!}")

            } else {
                CommonMethods.alertDialog(
                    ctx!!,
                    getString(R.string.checkYourConnection)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callBeatList(distID: String?) {
        try {

            if (CommonMethods.isNetworkAvailable(ctx!!)) {
                val json = JSONObject()

                distributor =distID!!
                json.put("dist_id",distID)

                RetrofitService(
                    ctx!!,
                    this,
                    CommonKeys.BEAT_LIST ,
                    CommonKeys.BEAT_LIST_CODE,
                    json,2
                ).callService(true, PreferenceFile.retrieveKey(ctx!!, CommonKeys.TOKEN)!!)

                Log.e("callBeatList", "=====$json")
                Log.e("callBeatList", "=token====${PreferenceFile.retrieveKey(ctx!!, CommonKeys.TOKEN)!!}")

            } else {
                CommonMethods.alertDialog(
                    ctx!!,
                    getString(R.string.checkYourConnection)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    lateinit var deleteDialog: Dialog

    private fun deleteMethod(position: Int) {
        deleteDialog = Dialog(ctx!!)
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        deleteDialog.setContentView(R.layout.logout_alert)

        deleteDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        deleteDialog.setCancelable(true)
        deleteDialog.setCanceledOnTouchOutside(false)
        deleteDialog.window!!.setGravity(Gravity.CENTER)

        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        deleteDialog.tvYes.setOnClickListener {
            deleteDialog.dismiss()
            list.removeAt(position)
            adapter.notifyItemRemoved(position)

        }

        deleteDialog.tvNo.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.show()
    }

    data class RetailerVisitData(var id: String = "", var date: String = "",
                                 var distributor: String = "", var beat: String = "",
                                 var retailer: String = "", var remarks: String = "",
                                 var action: String = "", var drawable: Drawable
    )

    data class CountData(var str: String = "", var bool: Boolean = false)

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

                           // listDistName.clear()
                           // listDistId.clear()

                            wordViewModel.deleteAllDist()

                            for(i in 0 until dataArray.length())
                            {
                                val dataObj = dataArray.getJSONObject(i)
                               // listDistId.add(dataObj.getString("dist_id"))
                               // listDistName.add(dataObj.getString("name"))

                                wordViewModel.insertDist(
                                    Distributor(dataObj.getString("dist_id")
                                    ,dataObj.getString("name")))

                            }

                           // setDistributorAdapter(listDistName,listDistId)

                        }

                        else {
                            CommonMethods.alertDialog(ctx!!, msg)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                CommonKeys.BEAT_LIST_CODE -> {
                    try {

                        Log.e("BEAT_LIST_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {

                            val data = json.getJSONArray("data")

                            listBeats.clear()

                           /* listBeats.add(BeatData(
                                "","Select Beat","","","",""
                            ))*/





                            for (i in 0 until data.length()) {

                                val dataObj = data.getJSONObject(i)

                                if(dataObj.getString("beatname").isNotEmpty()) {
                                    listBeats.add(
                                        BeatData(
                                            dataObj.getString("areaname"),
                                            dataObj.getString("beatname"),
                                            dataObj.getString("dist_id"),
                                            dataObj.getString("distributor"),
                                            dataObj.getString("id"),
                                            dataObj.getString("state")
                                        )
                                    )
                                }
                            }


                            setBeatAdapter(listBeats)
                        }

                        else {
                            CommonMethods.alertDialog(ctx!!, msg)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                CommonKeys.BEAT_RETAILER_LIST_CODE -> {
                    try {

                        Log.e("BEAT_RETAILER_LIST_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {

                            val data = json.getJSONArray("data")



                            listBeatsRetailer.clear()



                            for (i in 0 until data.length()) {

                                val dataObj = data.getJSONObject(i)

                                if(dataObj.getString("beatname").isNotEmpty()) {
                                    listBeatsRetailer.add(
                                        BeatRetailerData(
                                            dataObj.getString("address"),
                                            dataObj.getString("areaname"),
                                            dataObj.getString("beatname"),
                                            dataObj.getString("ca"),
                                            dataObj.getString("cac"),
                                            dataObj.getString("classification"),
                                            dataObj.getString("client"),
                                            dataObj.getString("country"),
                                            dataObj.getString("cperson"),
                                            dataObj.getString("cst"),
                                            dataObj.getString("cst_registerationdate"),
                                            dataObj.getString("csttin"),
                                            dataObj.getString("date"),
                                            dataObj.getString("dist_id"),
                                            dataObj.getString("distributor"),
                                            dataObj.getString("dvisit"),
                                            dataObj.getString("email"),
                                            dataObj.getString("empname"),
                                            dataObj.getString("firstname"),
                                            dataObj.getString("gstin"),
                                            dataObj.getString("id"),
                                            dataObj.getString("lastname"),
                                            dataObj.getString("latitude"),
                                            dataObj.getString("longitude"),
                                            dataObj.getString("mobile"),
                                            dataObj.getString("note"),
                                            dataObj.getString("pan"),
                                            dataObj.getString("phone"),
                                            dataObj.getString("pincode"),
                                            dataObj.getString("place"),
                                            dataObj.getString("retailer_id"),
                                            dataObj.getString("retailer_name"),
                                            dataObj.getString("retailer_type"),
                                            dataObj.getString("rid"),
                                            dataObj.getString("sno"),
                                            dataObj.getString("state"),
                                            dataObj.getString("type"),
                                            dataObj.getString("updated"),
                                            dataObj.getString("vattin")
                                        )
                                    )
                                }
                            }


                            setBeatRetailerAdapter(listBeatsRetailer)
                        }

                        else {
                            CommonMethods.alertDialog(ctx!!, msg)
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

    private fun setBeatRetailerAdapter(listBeatsRetailer: ArrayList<BeatRetailerData>) {


        adapter  = RetailerVisitAdapter(listBeatsRetailer)

        rvRetailerVisit.adapter = adapter
        adapter.onClicked(object :RetailerVisitAdapter.CardInterface{
            override fun clickedSelected(position: Int, str: String) {
                if(str=="delete") {
                    Log.e("====delete==","==11==$position")

                    deleteMethod(position)
                }
                if(str=="add")
                {
                    distributor = listBeatsRetailer[position].distributor
                    distributorId = listBeatsRetailer[position].dist_id
                    beat = listBeatsRetailer[position].beatname
                    CommonMethods.hideKeyboard(rvRetailerVisit)



                    shareLocation(position)

/*
                    gpsTracker = GPSTracker(ctx)


                    if (gpsTracker!!.canGetLocation()) {


                        Log.e("ifffff", "==else===" + gpsTracker!!.canGetLocation())

                        val gpsTracker = GPSTracker(ctx)
                        latitude = gpsTracker.latitude
                        longitude = gpsTracker.longitude

                        Toast.makeText(
                            ctx,
                            "lattitude==$latitude    longitude==$longitude",
                            Toast.LENGTH_SHORT
                        ).show()

                        val bundle = bundleOf("distributorName" to listBeatsRetailer[position].distributor,
                            "beatName" to listBeatsRetailer[position].beatname,
                            "retailer" to listBeatsRetailer[position].retailer_name,
                            "retailerId" to listBeatsRetailer[position].retailer_id,
                            "salesman" to listBeatsRetailer[position].client,
                            "dist_id" to listBeatsRetailer[position].dist_id
                        )
                        Navigation.findNavController(rvRetailerVisit).navigate(R.id.action_add_sales,bundle)
                    }
                    else {

                        Log.e("iffffff", "===iff==" + gpsTracker!!.canGetLocation())
                        showSettingsAlert()
                    }
*/

                   /* val bundle = bundleOf("distributorName" to listBeatsRetailer[position].distributor,
                        "beatName" to listBeatsRetailer[position].beatname,
                        "retailer" to listBeatsRetailer[position].retailer_name,
                        "retailerId" to listBeatsRetailer[position].retailer_id,
                        "salesman" to listBeatsRetailer[position].client,
                        "dist_id" to listBeatsRetailer[position].dist_id
                        )
                    Navigation.findNavController(rvRetailerVisit).navigate(R.id.action_add_sales,bundle)*/

                }

                if(str=="edit")
                {
                    Navigation.findNavController(rvRetailerVisit).navigate(R.id.action_edit_visit)
                }
            }
        })

    }

    private fun setBeatAdapter(listBeats: ArrayList<BeatData>) {


        tvBeatName2.setOnClickListener {
            openPopShortBy(tvBeatName2,listBeats)
        }

    }

    var popupWindow: PopupWindow? = null

    fun openPopShortBy(
        view: TextView,
        listBeats: ArrayList<BeatData>
    ) {
        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.custom_spinner, null)



        popupWindow = PopupWindow(
            customView,
            view.width,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val adapter = SpinnerCustomAdapter(listBeats)
        customView.rvSpinner.adapter = adapter
        adapter.onClicked(object :SpinnerCustomAdapter.CardInterface{
            override fun clickedSelected(position: Int) {

                view.text =listBeats[position].beatname
                popupWindow!!.dismiss()
                callBeatRetailer(listBeats[position].dist_id,listBeats[position].beatname)
            }


        })


        customView.etSearch.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
               // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(s.isNullOrBlank())
                {
                    val adapter2 = SpinnerCustomAdapter(listBeats)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =listBeats[position].beatname
                            popupWindow!!.dismiss()
                            callBeatRetailer(listBeats[position].dist_id,listBeats[position].beatname)
                        }
                    })
                }
                else
                {

                    val list : ArrayList<BeatData> = ArrayList()

                    for(i in 0 until listBeats.size)
                    {
                        if(listBeats[i].beatname.toLowerCase().contains(s.toString().toLowerCase(),false))
                        {
                        list.add(listBeats[i])

                        }
                    }


                    val adapter2 = SpinnerCustomAdapter(list)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =list[position].beatname
                            popupWindow!!.dismiss()
                            callBeatRetailer(list[position].dist_id,list[position].beatname)
                        }


                    })

                }



               // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })





        popupWindow!!.isOutsideTouchable = true

        popupWindow!!.showAsDropDown(view)
        popupWindow!!.isFocusable = true
        popupWindow!!.update()

    }

    private fun callBeatRetailer(distId: String, beatname: String) {
        try {

            if (CommonMethods.isNetworkAvailable(ctx!!)) {
                val json = JSONObject()

                beat = beatname

                json.put("dist_id",distId)
                json.put("beatname",beatname)

                RetrofitService(
                    ctx!!,
                    this,
                    CommonKeys.BEAT_RETAILER_LIST ,
                    CommonKeys.BEAT_RETAILER_LIST_CODE,
                    json,2
                ).callService(true, PreferenceFile.retrieveKey(ctx!!, CommonKeys.TOKEN)!!)

                Log.e("callBeatList", "=====$json")

            } else {
                CommonMethods.alertDialog(
                    ctx!!,
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
        adapter.onClicked(object :SpinnerCustomDistributorAdapter.CardInterface{
            override fun clickedSelected(position: Int) {

                view.text =listDist[position].distName
                popupWindow!!.dismiss()
                callBeatList(listDist[position].distID)

                // callBeatRetailer(listBeats[position].dist_id,listBeats[position].beatname)
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
                            callBeatList(listDist[position].distID)

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
                            callBeatList(list[position].distID)

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

    private fun permissions(): Boolean {
        when {
            ContextCompat.checkSelfPermission(ctx!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ->
                when {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        ctx as Activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) -> {
                        ActivityCompat.requestPermissions(
                            ctx as Activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                            1
                        )
                        return false
                    }
                    else -> {
                        ActivityCompat.requestPermissions(
                            ctx as Activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                            1
                        )
                        return false
                    }
                }
            else -> return true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                when {
                    grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                        //setGoogleMap(mMap)

                        Toast.makeText(ctx,"Permission Granted",Toast.LENGTH_SHORT).show()

                    }
                    else -> {
                        showAlertMap()
                    }
                }
                return
            }
        }}
    private fun showAlertMap() {
        val alertDialog = AlertDialog.Builder(ctx!!)
        alertDialog.setTitle("Location required !!")
        alertDialog.setCancelable(false)
        alertDialog.setMessage("Please accept location permission for area Preference.")
        alertDialog.setPositiveButton("ok") { dialog, which ->
            permissions()
            dialog.dismiss()
        }
        alertDialog.setNegativeButton("cancel")
        {
                dialog, which ->
            dialog.dismiss()

        }

        alertDialog.show()
    }
    private fun showSettingsAlert(position: Int) {
        val alertDialog = AlertDialog.Builder(ctx!!)
        alertDialog.setTitle("Enable GPS !!")
        alertDialog.setCancelable(false)
        alertDialog.setMessage("Please enable location permission for area Preference.")
        alertDialog.setPositiveButton("ok") { dialog, which ->
           // permissions()
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            ctx!!.startActivity(intent)
        }
        alertDialog.setNegativeButton("cancel")
        {
                dialog, which ->
            dialog.dismiss()

            val bundle = bundleOf(
                "distributorName" to listBeatsRetailer[position].distributor,
                "beatName" to listBeatsRetailer[position].beatname,
                "retailer" to listBeatsRetailer[position].retailer_name,
                "retailerId" to listBeatsRetailer[position].retailer_id,
                "salesman" to listBeatsRetailer[position].client,
                "dist_id" to listBeatsRetailer[position].dist_id
            )
            Navigation.findNavController(rvRetailerVisit)
                .navigate(R.id.action_add_sales, bundle)
        }

        alertDialog.show()
    }
    private fun shareLocation(position: Int) {
        val alertDialog = AlertDialog.Builder(ctx!!)
        alertDialog.setTitle("share Location !!")
        alertDialog.setCancelable(false)
        alertDialog.setMessage("Please share location permission for area Preference.")
        alertDialog.setPositiveButton("ok") { dialog, which ->
            dialog.dismiss()

            gpsTracker = GPSTracker(ctx)





           if(permissions())
           {
               if(gpsTracker!!.canGetLocation()) {

                   Log.e("ifffff", "==else===" + gpsTracker!!.canGetLocation())

                   val gpsTracker = GPSTracker(ctx)
                   latitude = gpsTracker.latitude
                   longitude = gpsTracker.longitude

                   Toast.makeText(
                       ctx,
                       "lattitude==$latitude    longitude==$longitude",
                       Toast.LENGTH_SHORT
                   ).show()

                   val bundle = bundleOf(
                       "distributorName" to listBeatsRetailer[position].distributor,
                       "beatName" to listBeatsRetailer[position].beatname,
                       "retailer" to listBeatsRetailer[position].retailer_name,
                       "retailerId" to listBeatsRetailer[position].retailer_id,
                       "salesman" to listBeatsRetailer[position].client,
                       "dist_id" to listBeatsRetailer[position].dist_id
                   )
                   Navigation.findNavController(rvRetailerVisit)
                       .navigate(R.id.action_add_sales, bundle)
               }
               else
               {
                   showSettingsAlert(position)
               }
           }

        }
        alertDialog.setNegativeButton("cancel")
        {
                dialog, which ->

                dialog.dismiss()
                 val bundle = bundleOf("distributorName" to listBeatsRetailer[position].distributor,
                            "beatName" to listBeatsRetailer[position].beatname,
                            "retailer" to listBeatsRetailer[position].retailer_name,
                            "retailerId" to listBeatsRetailer[position].retailer_id,
                            "salesman" to listBeatsRetailer[position].client,
                            "dist_id" to listBeatsRetailer[position].dist_id
                            )
                        Navigation.findNavController(rvRetailerVisit).navigate(R.id.action_add_sales,bundle)




        }

        alertDialog.show()
    }



    //data base work
    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory(((context as Activity).application as AppController).repository)
    }

}