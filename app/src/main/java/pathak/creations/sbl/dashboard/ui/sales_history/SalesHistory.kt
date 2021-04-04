package pathak.creations.sbl.dashboard.ui.sales_history

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.add_retailer.tvBeatName2
import kotlinx.android.synthetic.main.custom_spinner.view.*
import kotlinx.android.synthetic.main.fragment_sales_history.*
import kotlinx.android.synthetic.main.logout_alert.*
import kotlinx.android.synthetic.main.retailer_master.etSearch
import kotlinx.android.synthetic.main.retailer_master.tvDistributor2
import org.json.JSONObject
import pathak.creations.sbl.AppController
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.custom_adapter.SpinnerCustomAdapter
import pathak.creations.sbl.data_class.BeatRetailerData
import pathak.creations.sbl.data_classes.Beat
import pathak.creations.sbl.data_classes.Distributor
import pathak.creations.sbl.data_classes.WordViewModel
import pathak.creations.sbl.data_classes.WordViewModelFactory
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService


class SalesHistory : Fragment(), RetrofitResponse {

    private lateinit var salesHistoryVM: SalesHistoryVM
    lateinit var adapter: SalesHistoryAdapter

    var list: ArrayList<SalesHistory> = ArrayList()
    var listBeats: ArrayList<Beat> = ArrayList()


    var distributor: String = ""
    var distributorId: String = ""
    var beat: String = ""
    var listBeatsRetailer: ArrayList<BeatRetailerData> = ArrayList()
    var listBeatsRetailerFilter: ArrayList<BeatRetailerData> = ArrayList()

    var listDistName: ArrayList<String> = ArrayList()
    var listDistId: ArrayList<String> = ArrayList()

    private lateinit var ctx: Context


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        salesHistoryVM = ViewModelProvider(this).get(SalesHistoryVM::class.java)
        val root = inflater.inflate(R.layout.fragment_sales_history, container, false)
        ctx = root.context
        salesHistoryVM.dateValue.observe(viewLifecycleOwner, Observer {
            //    tvDateValue.text = it
        })
        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //  tvDate.setOnClickListener { salesHistoryVM.datePicker(view) }
        //   tvDateValue.setOnClickListener { salesHistoryVM.datePicker(view) }
//        tvAdd.setOnClickListener {
//           Navigation.findNavController(view).navigate(R.id.action_add_shoptotal)
//        }

        setSearch()
        //setDistributor()


        if (beat.isNotEmpty()) {
            tvDistributor2.hint = distributor
            tvBeatName2.hint = beat
            callBeatList(distributorId)
        }

        wordViewModel.allDistributor.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.

            dist?.let {

                setDistributorAdapter(it)

            }
        })


    }

    private fun setSearch() {
        etSearch.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //   TODO("Not yet implemented")
            }

            override fun afterTextChanged(s: Editable?) {
                //  TODO("Not yet implemented")
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

            }



        })
    }

    private fun callBeatList(distributorId: String?) {
        try {

            if (CommonMethods.isNetworkAvailable(ctx)) {
                val json = JSONObject()


                json.put("dist_id",distributorId)

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
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDistributor() {
        if (PreferenceFile.retrieveKey(ctx,CommonKeys.TYPE).equals("distributor"))
        {
            tvDistributor2.hint = PreferenceFile.retrieveKey(ctx,CommonKeys.NAME)
            callBeatList(PreferenceFile.retrieveKey(ctx,CommonKeys.NAME))
        }else
        {
           // callDistributorList()
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

    override fun response(code: Int, response: String) {
        try {
            Log.e("SalesHistory", "=====$code=====$response")
            when(code){
                CommonKeys.BEAT_LIST_CODE -> {
                    try {

                        Log.e("BEAT_LIST_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {

                            val data = json.getJSONArray("data")



                            listBeats.clear()



                            for (i in 0 until data.length()) {

                                val dataObj = data.getJSONObject(i)

                                if(dataObj.getString("beatname").isNotEmpty()) {
                                    listBeats.add(
                                        Beat(
                                            dataObj.getString("id"),
                                            dataObj.getString("dist_id"),
                                            dataObj.getString("distributor"),
                                            dataObj.getString("state"),
                                            dataObj.getString("areaname"),
                                            dataObj.getString("beatname")




                                        )
                                    )
                                }
                            }


                            setBeatAdapter(listBeats)
                        }

                        else {
                            CommonMethods.alertDialog(ctx, msg)
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
                            CommonMethods.alertDialog(ctx, msg)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                CommonKeys.RETAILER_LIST_CODE -> {
                    try {

                        Log.e("RETAILER_LIST_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {

                            //val data = json.getJSONObject("data")

                            val dataArray = json.getJSONArray("data")

                            listDistName.clear()
                            listDistId.clear()

                            for(i in 0 until dataArray.length())
                            {
                                val dataObj = dataArray.getJSONObject(i)
                                listDistId.add(dataObj.getString("dist_id"))
                                listDistName.add(dataObj.getString("name"))

                            }

                            //setDistributorAdapter(listDistName,listDistId)

                        }

                        else {
                            CommonMethods.alertDialog(ctx, msg)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }

        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun setDistributorAdapter(
        list: List<Distributor>
    ) {

        tvDistributor2.text =PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_DISTRIBUTOR_NAME)
        callBeatList(PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_DISTRIBUTOR))


    }

    /*private fun openDistributorShort(view: TextView, listDistName: List<String>, listDistId: java.util.ArrayList<String>) {
        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.custom_spinner, null)



        popupWindow = PopupWindow(
            customView,
            view.width,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val adapter = SpinnerCustomDistributorAdapter(listDistName)
        customView.rvSpinner.adapter = adapter
        adapter.onClicked(object :SpinnerCustomDistributorAdapter.CardInterface{
            override fun clickedSelected(position: Int) {

                view.text =listDistName[position]
                popupWindow!!.dismiss()
                callBeatList(listDistId[position])


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
                    val adapter2 = SpinnerCustomDistributorAdapter(listDistName)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomDistributorAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =listDistName[position]
                            popupWindow!!.dismiss()
                            callBeatList(listDistId[position])


                        }
                    })
                }
                else
                {

                    val list : ArrayList<String> = ArrayList()
                    val list2 : ArrayList<String> = ArrayList()

                    for(i in listDistName.indices)
                    {
                        if(listDistName[i].toLowerCase().contains(s.toString().toLowerCase(),false))
                        {
                            list.add(listDistName[i])
                            list2.add(listDistId[i])
                        }
                    }

                    val adapter2 = SpinnerCustomDistributorAdapter(list)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomDistributorAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =list[position]
                            popupWindow!!.dismiss()
                            callBeatList(list2[position])

                        }
                    })
                }
            }
        })

        popupWindow!!.isOutsideTouchable = true

        popupWindow!!.showAsDropDown(view)
        popupWindow!!.isFocusable = true
        popupWindow!!.update()

    }*/

    private fun setBeatRetailerAdapter(listBeatsRetailer: ArrayList<BeatRetailerData>) {
        adapter=SalesHistoryAdapter(listBeatsRetailer)
        rvSalesHistory.adapter=adapter
        adapter.onClicked(object :SalesHistoryAdapter.CardInterface{
            override fun clickedSelected(position: Int, str: String) {
                if(str=="add")
                {
                    Navigation.findNavController(rvSalesHistory).navigate(R.id.action_add_shoptotal)
                }
                if(str=="delete") {
                    Log.e("====delete==","==11==$position")

                    deleteMethod(position)
                }
            }
        })
    }
    lateinit var deleteDialog: Dialog
    private fun deleteMethod(position: Int) {
        deleteDialog = Dialog(ctx)
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
    }

    private fun setBeatAdapter(listBeats: List<Beat>) {
        tvBeatName2.setOnClickListener {
            openPopShortBy(tvBeatName2,listBeats)
        }
    }
    var popupWindow: PopupWindow? = null
    private fun openPopShortBy(view: TextView, listBeats: List<Beat>) {
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

            if (CommonMethods.isNetworkAvailable(ctx)) {
                val json = JSONObject()

                json.put("dist_id",distId)
                json.put("beatname",beatname)

                RetrofitService(
                    ctx,
                    this,
                    CommonKeys.BEAT_RETAILER_LIST ,
                    CommonKeys.BEAT_RETAILER_LIST_CODE,
                    json,2
                ).callService(true, PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!)


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


    //data base work
    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory(((ctx as Activity).application as AppController).repository)
    }

}
