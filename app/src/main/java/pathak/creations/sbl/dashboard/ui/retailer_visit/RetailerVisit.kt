package pathak.creations.sbl.dashboard.ui.retailer_visit

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.custom_spinner.view.*
import kotlinx.android.synthetic.main.logout_alert.*
import kotlinx.android.synthetic.main.retailer_visit.*
import org.json.JSONObject
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.custom_adapter.SpinnerCustomAdapter
import pathak.creations.sbl.data_class.BeatData
import pathak.creations.sbl.data_class.BeatRetailerData
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService

class RetailerVisit : Fragment(), RetrofitResponse {


    private lateinit var retailerVisitVM: RetailerVisitVM


    var list : ArrayList<RetailerVisitData> = ArrayList()
    var listBeats : ArrayList<BeatData> = ArrayList()
    var listBeatsRetailer : ArrayList<BeatRetailerData> = ArrayList()
    var listCount: ArrayList<CountData> = ArrayList()

    var count = ""
    var ctx : Context? = null
    var countInitial = "-1"


    lateinit var adapter : RetailerVisitAdapter


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


        callBeatList()



        // setCountList()

    }

    private fun callBeatList() {
        try {

            if (CommonMethods.isNetworkAvailable(ctx!!)) {
                val json = JSONObject()


                json.put("dist_id","DST-002")

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

    private fun setCountList(count: String) {


        rvCount.visibility = View.VISIBLE


        listCount.clear()
        for (i in 0 until count.toInt()) {

            listCount.add(CountData("$i", false))
        }

        if (countInitial != "-1") {
            listCount[countInitial.toInt() - 1].bool = true
        }

        val adapter2 = CountAdapter(listCount)

        rvCount.adapter = adapter2
        adapter2.onClicked(object : CountAdapter.CardInterface {
            override fun clickedSelected(position: Int) {


                if (!listCount[position].bool) {
                    for (i in 0 until listCount.size) {
                        listCount[i].bool = false
                    }
                    listCount[position].bool = true

                    countInitial = (position + 1).toString()

                    adapter2.notifyDataSetChanged()

                    callList(countInitial)

                }
            }

        })

    }

    private fun callList(countInitial: String) {
        try {

            var endPoint = ""
            if (countInitial != "-1") {
                endPoint = "?page=$countInitial"
            }


            if (CommonMethods.isNetworkAvailable(ctx!!)) {
                val json = JSONObject()

                RetrofitService(
                    ctx!!,
                    this,
                    CommonKeys.RETAILER_LIST + endPoint,
                    CommonKeys.RETAILER_LIST_CODE,
                    1
                ).callService(true, PreferenceFile.retrieveKey(ctx!!, CommonKeys.TOKEN)!!)

                Log.e("callList", "=====$json")

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


    private fun setListData(list: ArrayList<RetailerVisitData>) {

        adapter  = RetailerVisitAdapter(listBeatsRetailer)

        rvRetailerVisit.adapter = adapter
        adapter.onClicked(object :RetailerVisitAdapter.CardInterface{
            override fun clickedSelected(position: Int, str: String) {
                if(str=="delete") {
                    Log.e("====delete==","==11==$position")

                    deleteMethod(position)
                } else {
                    Navigation.findNavController(rvRetailerVisit).navigate(R.id.action_edit_visit)
                }
            }
        })

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

                            val data = json.getJSONObject("data")


                            list.clear()
                            val dataData = data.getJSONArray("data")
                            for (i in 0 until dataData.length()) {

                                val dataObj = dataData.getJSONObject(i)

                                //list.add(
                                //                    RetailerVisitData(
                                //                        i.toString(), "12/12/2020",
                                //                        "AAI MATHAJI ENTERPRISES - BANGALORE", "SEEGEHALLI",
                                //                        "MANJUNAT STORE", "Order placed","",resources.getDrawable(R.drawable.content_cell)
                                //                    )
                                //                )


                                val beats = dataObj.getJSONArray("beats")

                                for (j in 0 until beats.length()) {

                                    val beatsObj = beats.getJSONObject(j)

                                    list.add(
                                        RetailerVisitData(
                                            dataObj.getString("id"),
                                            beatsObj.getString("updated"),
                                            beatsObj.getString("distributor"),
                                            beatsObj.getString("beatname"),
                                            dataObj.getString("mobile"),
                                            beatsObj.getString("remarks"),
                                            "",
                                            resources.getDrawable(R.drawable.content_cell)
                                        )
                                    )

                                  //  setListData(list)

                                }


                            }





                            count = data.getString("last_page")

                            setCountList(count)
                        } else {
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

                    val bundle = bundleOf("distributorName" to listBeatsRetailer[position].distributor,
                        "beatName" to listBeatsRetailer[position].beatname,
                        "retailer" to listBeatsRetailer[position].retailer_name,
                        "retailerId" to listBeatsRetailer[position].retailer_id,
                        "salesman" to listBeatsRetailer[position].client
                        )
                    Navigation.findNavController(rvRetailerVisit).navigate(R.id.action_add_sales,bundle)
                }

                if(str=="edit")
                {
                    Navigation.findNavController(rvRetailerVisit).navigate(R.id.action_edit_visit)
                }
            }
        })

    }

    private fun setBeatAdapter(listBeats: ArrayList<BeatData>) {


      //  val adapter = ArrayAdapter<String>(ctx!!,R.layout.spinner_dropdown_item,listShort)
       // adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
     //   spBeatName.adapter = adapter
       // tvBeatName2.setAdapter(adapter)


        tvBeatName2.setOnClickListener {
            openPopShortBy(tvBeatName2,listBeats)
        }











       /* spBeatName.setOnItemSelectedListener(object:OnItemSelectedListener{
            override fun onNothingSelected() {
               /// TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(view: View?, position: Int, id: Long) {
                if(position!=0)
                {
                    callBeatRetailer(listBeats[position].dist_id,listBeats[position].beatname)
                }
            }
        })*/

       /* spBeatName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ){
                if(position!=0)
                {
                    callBeatRetailer(listBeats[position].dist_id,listBeats[position].beatname)
                }
            }
        }*/

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
                        if(listBeats[i].beatname.contains(s))
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

    private fun getList(listBeats: ArrayList<BeatData>): ArrayList<String> {
        val list : ArrayList<String> = ArrayList()

        for(i in 0 until listBeats.size)
        {
            list.add(listBeats[i].beatname)
        }

        return list
    }


}