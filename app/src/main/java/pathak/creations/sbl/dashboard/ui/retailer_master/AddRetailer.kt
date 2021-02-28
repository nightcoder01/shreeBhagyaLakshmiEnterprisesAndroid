package pathak.creations.sbl.dashboard.ui.retailer_master

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.add_retailer.*
import kotlinx.android.synthetic.main.custom_spinner.view.*
import org.json.JSONObject
import pathak.creations.sbl.AppController
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.custom_adapter.SpinnerCustomAdapter
import pathak.creations.sbl.custom_adapter.SpinnerCustomCategoryAdapter
import pathak.creations.sbl.custom_adapter.SpinnerCustomDistributorAdapter
import pathak.creations.sbl.data_class.BeatData
import pathak.creations.sbl.data_classes.Beat
import pathak.creations.sbl.data_classes.Distributor
import pathak.creations.sbl.data_classes.WordViewModel
import pathak.creations.sbl.data_classes.WordViewModelFactory
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService


class AddRetailer : Fragment(), RetrofitResponse {


    companion object {
        fun newInstance() = AddRetailer()
    }

    private lateinit var viewModel: AddRetailerVM
    private lateinit var ctx: Context

   // var listDistName : ArrayList<String> = ArrayList()
   // var listDistId : ArrayList<String> = ArrayList()
    var listBeats : ArrayList<BeatData> = ArrayList()
    var listClassification : ArrayList<String> = ArrayList()
    var listRetailerType : ArrayList<String> = ArrayList()



    var distIDD = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.add_retailer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddRetailerVM::class.java)
        // TODO: Use the ViewModel
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ctx = view.context
        setDistributor()
        setClassification()
        setRetailerType()


        tvCancel.setOnClickListener { (ctx as Activity).onBackPressed() }
        tvSubmit.setOnClickListener {

            if(valid()){
            callAddRetailer()
        }




        }



        //set live data observer
        wordViewModel.allDistributor.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.


            dist?.let {

                setDistributorAdapter(it)

            }
        })



    }

    private fun valid(): Boolean {

        if(tvDistributor2.text.isEmpty())
        {

            CommonMethods.alertDialog(ctx, "Please select a Distributor")
            return false
        }

        if(tvClassification2.text.isEmpty())
        {

            CommonMethods.alertDialog(ctx, "Please select a Classification")
            return false
        }

        if(tvRetailerType2.text.isEmpty())
        {

            CommonMethods.alertDialog(ctx, "Please select a Retailer Type")
            return false
        }

        if(tvBeatName2.text.isEmpty())
        {

            CommonMethods.alertDialog(ctx, "Please select a Beat Name")
            return false
        }

        if(etRetailerName.text.isEmpty())
        {

            CommonMethods.alertDialog(ctx, "Please enter Retailer Name")
            return false
        }

        if(etRetailerId.text.isEmpty())
        {

            CommonMethods.alertDialog(ctx, "Please enter Retailer ID")
            return false
        }

        if(etAddress.text.isEmpty())
        {

            CommonMethods.alertDialog(ctx, "Please enter Address")
            return false
        }

        if(etMobile.text.isEmpty())
        {

            CommonMethods.alertDialog(ctx, "Please enter mobile number")
            return false
        }

        if(etAreaName.text.isEmpty())
        {

            CommonMethods.alertDialog(ctx, "Please enter Area Name")
            return false
        }

        if(etStateName.text.isEmpty())
        {

            CommonMethods.alertDialog(ctx, "Please enter State Name")
            return false
        }

        if(etGST.text.isEmpty())
        {

            CommonMethods.alertDialog(ctx, "Please enter GSTIN")
            return false
        }

        return true
    }

    private fun callAddRetailer() {
        try {


            if (CommonMethods.isNetworkAvailable(ctx)) {
                val json = JSONObject()


                json.put("dist_id",distIDD)
                json.put("distributor",tvDistributor2.text.toString())
                json.put("retailer_id",etRetailerId.text.toString())
                json.put("retailer_name",etRetailerName.text.toString())
                json.put("beatname",tvBeatName2.text.toString())
                json.put("address",etAddress.text.toString())
                json.put("state",etStateName.text.toString())
                json.put("mobile",etMobile.text.toString())
                json.put("areaname",etAreaName.text.toString())
                json.put("classification",tvClassification2.text.toString())
                json.put("retailer_type",tvRetailerType2.text.toString())

                RetrofitService(
                    ctx,
                    this,
                    CommonKeys.ADD_RETAILER ,
                    CommonKeys.ADD_RETAILER_CODE,
                    json,2
                ).callService(true, PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!)

                Log.e("callAddRetailer", "=====$json")
                Log.e("callAddRetailer", "=token====${PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!}")

            } else {
                CommonMethods.alertDialog(
                    ctx,
                    getString(R.string.checkYourConnection)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setRetailerType() {

        listRetailerType.clear()

        listRetailerType.add("Whole Sale Trader")
        listRetailerType.add("General Store")
        listRetailerType.add("Bhajji Store")
        listRetailerType.add("Stand Alone Outlet")
        listRetailerType.add("Semi Whole Sale Trader")
        listRetailerType.add("Hotels")
        listRetailerType.add("Caterer")
        listRetailerType.add("Paying Guest")

        tvRetailerType2.setOnClickListener {
            openPopRetailerType(tvRetailerType2,listRetailerType)
        }

    }



    fun openPopRetailerType(
        view: TextView,
        listRetailerType: ArrayList<String>
    ) {
        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.custom_spinner, null)



        popupWindow = PopupWindow(
            customView,
            view.width,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val adapter = SpinnerCustomCategoryAdapter(listRetailerType)
        customView.rvSpinner.adapter = adapter
        adapter.onClicked(object :SpinnerCustomCategoryAdapter.CardInterface{
            override fun clickedSelected(position: Int) {

                view.text =listRetailerType[position]
                popupWindow!!.dismiss()
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
                    val adapter2 = SpinnerCustomCategoryAdapter(listRetailerType)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomCategoryAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =listRetailerType[position]
                            popupWindow!!.dismiss()
                            // callBeatRetailer(listBeats[position].dist_id,listBeats[position].beatname)
                        }
                    })
                }
                else
                {

                    val list : ArrayList<String> = ArrayList()

                    for(i in 0 until listRetailerType.size)
                    {
                        if(listRetailerType[i].toLowerCase().contains(s.toString().toLowerCase(),false))
                        {
                            list.add(listRetailerType[i])

                        }
                    }


                    val adapter2 = SpinnerCustomCategoryAdapter(list)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomCategoryAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =list[position]
                            popupWindow!!.dismiss()
                            // callBeatRetailer(list[position].dist_id,list[position].beatname)
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




    private fun setClassification() {

        listClassification.clear()

        listClassification.add("A")
        listClassification.add("B")
        listClassification.add("C")
        listClassification.add("D")

        tvClassification2.setOnClickListener {
            openPopRetailerType(tvClassification2,listClassification)
        }     }


    private fun setDistributor() {
        if(PreferenceFile.retrieveKey(ctx, CommonKeys.TYPE).equals("distributor"))
        {
            tvDistributor2.hint = PreferenceFile.retrieveKey(ctx,CommonKeys.NAME)
            callBeatList(PreferenceFile.retrieveKey(ctx,CommonKeys.NAME))
        }
        else
        {
            callDistributorList()
        }
    }

    private fun callDistributorList() {
        try {

            if (CommonMethods.isNetworkAvailable(ctx)) {
                val json = JSONObject()



                RetrofitService(
                    ctx,
                    this,
                    CommonKeys.RETAILER_LIST ,
                    CommonKeys.RETAILER_LIST_CODE,
                    1
                ).callService(true, PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!)

                Log.e("callDistributorList", "=====$json")
                Log.e("callDistributorList", "=token====${PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!}")

            } else {
                CommonMethods.alertDialog(
                    ctx,
                    getString(R.string.checkYourConnection)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callBeatList(distID: String?) {
        try {

            distIDD = distID!!
            tvBeatName2.text = ""
/*
            if (CommonMethods.isNetworkAvailable(ctx)) {
                val json = JSONObject()


                json.put("dist_id",distID)

                RetrofitService(
                    ctx,
                    this,
                    CommonKeys.BEAT_LIST ,
                    CommonKeys.BEAT_LIST_CODE,
                    json,2
                ).callService(true, PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!)

                Log.e("callBeatList", "=====$json")
                Log.e("callBeatList", "=token====${PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!}")

            } else {
                CommonMethods.alertDialog(
                    ctx,
                    getString(R.string.checkYourConnection)
                )
            }*/





//            wordViewModel.getBeatFromDist(distIDD, this)



            //set live data observer
            wordViewModel.allBeat.observe(viewLifecycleOwner, Observer { dist ->
                // Update the cached copy of the words in the adapter.


                dist?.let {

                    setBeatAdapter(it)

                }
            })

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

                            //listDistName.clear()
                           // listDistId.clear()

                            wordViewModel.deleteAllDist()

                            for(i in 0 until dataArray.length())
                            {
                                val dataObj = dataArray.getJSONObject(i)
                              //  listDistId.add(dataObj.getString("dist_id"))
                              //  listDistName.add(dataObj.getString("name"))

                                wordViewModel.insertDist(
                                    Distributor(dataObj.getString("dist_id")
                                        ,dataObj.getString("name")))

                            }

                           // setDistributorAdapter(listDistName,listDistId)

                        }

                        else {
                            CommonMethods.alertDialog(ctx, msg)
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


                          //  setBeatAdapter(listBeats)
                        }

                        else {
                            CommonMethods.alertDialog(ctx, msg)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                CommonKeys.ADD_RETAILER_CODE -> {
                    try {

                        Log.e("ADD_RETAILER_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {



                            Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show()

                            (ctx as Activity).onBackPressed()




                        }

                        else {
                            CommonMethods.alertDialog(ctx, msg)
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



    private fun setBeatAdapter(listBeats: List<Beat>) {


        tvBeatName2.setOnClickListener {
            openPopShortBy(tvBeatName2,listBeats)
        }

    }

    var popupWindow: PopupWindow? = null

    private fun openPopShortBy(
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
                    val adapter2 = SpinnerCustomAdapter(listBeats)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =listBeats[position].beatname
                            popupWindow!!.dismiss()
                           // callBeatRetailer(listBeats[position].dist_id,listBeats[position].beatname)
                        }
                    })
                }
                else
                {

                    val list : ArrayList<Beat> = ArrayList()

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
                           // callBeatRetailer(list[position].dist_id,list[position].beatname)
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



    private fun setDistributorAdapter(list: List<Distributor>) {
        tvDistributor2.setOnClickListener {
            openDistributorShort(tvDistributor2,list)
        }    }

    private fun openDistributorShort(
        view: TextView,
        list: List<Distributor>
        ) {
        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.custom_spinner, null)



        popupWindow = PopupWindow(
            customView,
            view.width,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val adapter = SpinnerCustomDistributorAdapter(list)
        customView.rvSpinner.adapter = adapter
        adapter.onClicked(object :SpinnerCustomDistributorAdapter.CardInterface{
            override fun clickedSelected(position: Int) {

                view.text =list[position].distName
                popupWindow!!.dismiss()
                callBeatList(list[position].distID)

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
                    val adapter2 = SpinnerCustomDistributorAdapter(list)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomDistributorAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =list[position].distName
                            popupWindow!!.dismiss()
                            callBeatList(list[position].distID)

                            //  callBeatRetailer(listBeats[position].dist_id,listBeats[position].beatname)
                        }
                    })
                }
                else
                {

                    val list2 : ArrayList<Distributor> = ArrayList()

                    for(i in list.indices)
                    {
                        if(list[i].distName.toLowerCase().contains(s.toString().toLowerCase(),false))
                        {
                            list2.add(list[i])

                        }
                    }


                    val adapter2 = SpinnerCustomDistributorAdapter(list)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomDistributorAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =list2[position].distName
                            popupWindow!!.dismiss()
                            callBeatList(list2[position].distID)

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


    //data base work
    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory(((context as Activity).application as AppController).repository)
    }
}
