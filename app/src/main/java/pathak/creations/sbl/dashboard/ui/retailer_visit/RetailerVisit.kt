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
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AlertDialogLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.custom_spinner.view.*
import kotlinx.android.synthetic.main.logout_alert.*
import kotlinx.android.synthetic.main.retailer_visit.*
import org.json.JSONObject
import pathak.creations.sbl.AppController
import pathak.creations.sbl.R
import pathak.creations.sbl.common.*
import pathak.creations.sbl.custom_adapter.SpinnerCustomAdapter
import pathak.creations.sbl.custom_adapter.SpinnerCustomDistributorAdapter
import pathak.creations.sbl.dashboard.DashBoard
import pathak.creations.sbl.data_classes.*
import pathak.creations.sbl.interfaces.DataChangeListener
import pathak.creations.sbl.interfaces.RetailerDataChangeListener
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService

class RetailerVisit : Fragment(), RetrofitResponse, DataChangeListener<LiveData<List<Beat>>>,
    RetailerDataChangeListener<LiveData<List<Retailer>>> {


    private lateinit var retailerVisitVM: RetailerVisitVM

    var list : ArrayList<RetailerVisitData> = ArrayList()

    var distributor: String = ""
    var distributorId: String = ""
    var beat: String = ""
    var listBeatsRetailer : ArrayList<Retailer> = ArrayList()
    var listBeatsRetailerFilter : ArrayList<Retailer> = ArrayList()

    var ctx : Context? = null

    lateinit var adapter : RetailerVisitAdapter
    private  var latitude : Double = 0.0
    private  var longitude : Double = 0.0
    private  var gpsTracker: GPSTracker? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
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



        setDistributor()
        if(beat.isNotEmpty()) {

            tvDistributor2.hint = distributor
            tvBeatName2.hint = beat

            callBeatList(distributorId)
            callBeatRetailer(distributorId,beat)
        }

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
                   // setBeatRetailerAdapter(listBeatsRetailer)
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
        }
    }


    private fun callBeatList(distID: String?) {
        try {

            wordViewModel.getBeatFromDist(distID!!, this)

        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun DataChange(data: LiveData<List<Beat>>) {
        data.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.

            dist?.let {
                setBeatAdapter(it)
            }
        })
    }


    override fun RetailerDataChange(data: LiveData<List<Retailer>>) {
        data.observe(viewLifecycleOwner, Observer { retailer ->
            // Update the cached copy of the words in the adapter.
            Log.e("callBeatRetailer", "==33===${retailer.size}===")

            retailer?.let {
                listBeatsRetailer.addAll(it)
                setBeatRetailerAdapter(it)

            }
        })
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
                CommonKeys.ADD_VISIT_CODE -> {
                    try {

                        Log.e("RETAILER_LIST_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {




                            CommonMethods.alertDialog(ctx!!, msg)



                        } else {
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

    private fun setBeatRetailerAdapter(listBeatsRetailer: List<Retailer>) {


        adapter  = RetailerVisitAdapter(listBeatsRetailer)

        rvRetailerVisit.adapter = adapter
        adapter.onClicked(object :RetailerVisitAdapter.CardInterface{
            override fun clickedSelected(position: Int, str: String) {
                if(str=="delete") {
                    Log.e("====delete==","==11==$position")

                    deleteMethod(position)
                }
                if(str=="remarks") {
                    Log.e("====remarks==","==11==$position")

                    addRemarks(listBeatsRetailer,position)
                }
                if(str=="add")
                {
                    distributor = listBeatsRetailer[position].distributor
                    distributorId = listBeatsRetailer[position].dist_id
                    beat = listBeatsRetailer[position].beatname
                    CommonMethods.hideKeyboard(rvRetailerVisit)



                    if(PreferenceFile.retrieveKey(ctx!!,CommonKeys.IS_LOCATION_CHECKED)=="false")
                    {
                        shareLocation(position)
                    }
                    else



                    {

                        Log.e("dfasdasdfas","=============${listBeatsRetailer[position].retailer_name}")
                        Log.e("dfasdasdfas","=============${listBeatsRetailer[position].mobile}")
                        Log.e("dfasdasdfas","=============${listBeatsRetailer[position]}")
                        Log.e("dfasdasdfas","=============${position}")

                        val bundle = bundleOf("distributorName" to listBeatsRetailer[position].distributor,
                            "beatName" to listBeatsRetailer[position].beatname,
                            "retailer" to listBeatsRetailer[position].retailer_name,
                            "phone" to listBeatsRetailer[position].mobile,
                            "retailerId" to listBeatsRetailer[position].retailer_id,
                            "salesman" to listBeatsRetailer[position].client,
                            "dist_id" to listBeatsRetailer[position].dist_id
                        )


                       // Navigation.findNavController(rvRetailerVisit).navigate(R.id.action_add_sales,bundle)
                        Navigation.findNavController(rvRetailerVisit)
                            .navigate(R.id.retailerVisitCheckIn, bundle)
                    }

                }

                if(str=="edit")
                {
                    Navigation.findNavController(rvRetailerVisit).navigate(R.id.action_edit_visit)
                }
            }
        })

    }


    private lateinit var dialogBuilderMain  : AlertDialog

    private fun addRemarks(
        retailer: List<Retailer>,
        position: Int
    ) {


        val dialogBuilder = AlertDialog.Builder(ctx!!)
        val layout = AlertDialogLayout.inflate(ctx, R.layout.custom_remarks,null)
        dialogBuilder.setView(layout)

        val tvSubmit :TextView= layout.findViewById(R.id.tvSubmittt)
        val etRemarks : EditText = layout.findViewById(R.id.etRemarks)

        dialogBuilderMain = dialogBuilder.create()
        dialogBuilderMain.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBuilderMain.setCancelable(false)
        dialogBuilderMain.setCanceledOnTouchOutside(true)

        tvSubmit.setOnClickListener {



            if(etRemarks.text.toString().isNotEmpty())
            {Toast.makeText(ctx,"remarks added successfully",Toast.LENGTH_SHORT).show()
                retailer[position].note = etRemarks.text.toString()

                wordViewModel.updateRetailer(retailer[position])
                adapter.notifyItemChanged(position)

                dialogBuilderMain.dismiss()
            callRemarksAdd(retailer[position],etRemarks.text.toString())
            }
            else
            {
                Toast.makeText(ctx,"remarks is empty",Toast.LENGTH_SHORT).show()
                dialogBuilderMain.dismiss()
            }

        }


        dialogBuilderMain.show()
    }

    private fun callRemarksAdd(
        retailer: Retailer,
        toString: String
    ) {
        try {

            if (CommonMethods.isNetworkAvailable(ctx!!)) {
                val json = JSONObject()

                json.put("beatname",retailer.beatname)
                json.put("dist_id",retailer.dist_id)
                json.put("distributor",retailer.distributor)
                json.put("retailer_name",retailer.retailer_name)
                json.put("retailer_id",retailer.retailer_id)
                json.put("remarks",toString)
                json.put("latitude","0.0")
                json.put("longitude","0.0")
                // //beatname:
                //    //dist_id:
                //    //distributor:
                //    //retailer_name:
                //    //retailer_id:
                //    //latitude:
                //    //longitude:
                //    //remarks:


                RetrofitService(
                    ctx!!,
                    this,
                    CommonKeys.ADD_VISIT,
                    CommonKeys.ADD_VISIT_CODE,
                    json,2
                ).callService(true, PreferenceFile.retrieveKey(ctx!!, CommonKeys.TOKEN)!!)

                Log.e("callDistributorList", "=====$json")
                Log.e("callDistributorList", "=token====${PreferenceFile.retrieveKey(ctx!!, CommonKeys.TOKEN)!!}")

            }
            else {
                CommonMethods.alertDialog(
                    ctx!!,
                    getString(R.string.checkYourConnection)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun setBeatAdapter(listBeats: List<Beat>) {

        tvBeatName2.setOnClickListener {
            openPopShortBy(tvBeatName2,listBeats)
        }

    }

    var popupWindow: PopupWindow? = null

    fun openPopShortBy(
        view: TextView,
        listBeats: List<Beat>
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

                    val list : ArrayList<Beat> = ArrayList()

                    for(i in listBeats.indices)
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

            Log.e("callBeatRetailer", "=====$distId===$beatname")
            wordViewModel.getBeatRetailer(beatname, this)


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDistributorAdapter(
        list: List<Distributor>

    ) {

        tvDistributor2.text =PreferenceFile.retrieveKey(ctx!!,CommonKeys.SELECTED_DISTRIBUTOR_NAME)
        callBeatList(PreferenceFile.retrieveKey(ctx!!,CommonKeys.SELECTED_DISTRIBUTOR))
        /*tvDistributor2.setOnClickListener {
            openDistributorShort(tvDistributor2,list)
        }*/

    }

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
                        //showAlertMap()
                    }
                }
                return
            }
        }}
    private fun showSettingsAlert(position: Int) {
        val alertDialog = AlertDialog.Builder(ctx!!)
        alertDialog.setTitle("Enable GPS !!")
        alertDialog.setCancelable(false)
        alertDialog.setMessage("Please enable your GPS setting.")
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


            Log.e("dfasdasdfas","=============${listBeatsRetailer[position].phone}")


            val bundle = bundleOf(
                "distributorName" to listBeatsRetailer[position].distributor,
                "beatName" to listBeatsRetailer[position].beatname,
                "retailer" to listBeatsRetailer[position].retailer_name,
                "phone" to listBeatsRetailer[position].mobile,
                "retailerId" to listBeatsRetailer[position].retailer_id,
                "salesman" to listBeatsRetailer[position].client,
                "dist_id" to listBeatsRetailer[position].dist_id
            )
            /*Navigation.findNavController(rvRetailerVisit)
                .navigate(R.id.action_add_sales, bundle)*/
            Navigation.findNavController(rvRetailerVisit)
                .navigate(R.id.retailerVisitCheckIn, bundle)
        }

        alertDialog.show()
    }
    lateinit var clickedd: LocationClicked

    fun onClicked(clicked: LocationClicked)
    {this.clickedd = clicked}




    private fun shareLocation(position: Int) {
        val alertDialog = AlertDialog.Builder(ctx!!)

        PreferenceFile.storeKey(ctx!!,CommonKeys.IS_LOCATION_CHECKED,"true")

        clickedd =  DashBoard()
        clickedd.clicked(true)


        (ctx as DashBoard).locationClickListener.value ="true"


        alertDialog.setTitle("share Location !!")
        alertDialog.setCancelable(false)
        alertDialog.setMessage("Please enable your GPS setting.")
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

                   Log.e("dfasdasdfas","=============${listBeatsRetailer[position].phone}")


                   val bundle = bundleOf(
                       "distributorName" to listBeatsRetailer[position].distributor,
                       "beatName" to listBeatsRetailer[position].beatname,
                       "retailer" to listBeatsRetailer[position].retailer_name,
                       "phone" to listBeatsRetailer[position].mobile,
                       "retailerId" to listBeatsRetailer[position].retailer_id,
                       "salesman" to listBeatsRetailer[position].client,
                       "dist_id" to listBeatsRetailer[position].dist_id
                   )
                   /*Navigation.findNavController(rvRetailerVisit)
                       .navigate(R.id.action_add_sales, bundle)*/
                   Navigation.findNavController(rvRetailerVisit)
                       .navigate(R.id.retailerVisitCheckIn, bundle)
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

            Log.e("dfasdasdfas","=============${listBeatsRetailer[position].phone}")

            val bundle = bundleOf("distributorName" to listBeatsRetailer[position].distributor,
                            "beatName" to listBeatsRetailer[position].beatname,
                            "retailer" to listBeatsRetailer[position].retailer_name,
                            "phone" to listBeatsRetailer[position].mobile,
                            "retailerId" to listBeatsRetailer[position].retailer_id,
                            "salesman" to listBeatsRetailer[position].client,
                            "dist_id" to listBeatsRetailer[position].dist_id
                            )
                       // Navigation.findNavController(rvRetailerVisit).navigate(R.id.action_add_sales,bundle)
            Navigation.findNavController(rvRetailerVisit)
                .navigate(R.id.retailerVisitCheckIn, bundle)

        }

        alertDialog.show()
    }



    //data base work
    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory(((context as Activity).application as AppController).repository)
    }

}