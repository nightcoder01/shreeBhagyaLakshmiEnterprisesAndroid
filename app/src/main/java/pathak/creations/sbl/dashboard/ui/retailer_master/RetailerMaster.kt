package pathak.creations.sbl.dashboard.ui.retailer_master

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_slideshow.*
import kotlinx.android.synthetic.main.logout_alert.*
import org.json.JSONObject
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.data_class.BeatData
import pathak.creations.sbl.data_class.BeatRetailerData
import pathak.creations.sbl.data_class.RetailerData
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService

class RetailerMaster : Fragment(), RetrofitResponse {

    private fun setBeatAdapter(listBeats: ArrayList<BeatData>) {

        val listShort : ArrayList<String>  = getList(listBeats)
        val listIDShort : ArrayList<String>  = getDistIdList(listBeats)

        listShort.distinct()

        val adapter = ArrayAdapter<String>(ctx,R.layout.spinner_item,listShort.distinct())
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spBeatName.adapter = adapter

        spBeatName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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
                    callDistRetailer(listIDShort.distinct()[position])
                }
            }
        }

    }

    private fun callDistRetailer(distId: String) {
        try {

            if (CommonMethods.isNetworkAvailable(ctx)) {
                val json = JSONObject()

                json.put("dist_id",distId)

                RetrofitService(
                    ctx,
                    this,
                    CommonKeys.GET_RETAILERS ,
                    CommonKeys.GET_RETAILERS_CODE,
                    json,2
                ).callService(true, PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!)

                Log.e("callDistRetailer", "=====$json")

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

    private fun getList(listBeats: ArrayList<BeatData>): ArrayList<String> {
        val list : ArrayList<String> = ArrayList()

        for(i in 0 until listBeats.size)
        {
            list.add(listBeats[i].distributor)
        }

        return list
    }

    private fun getDistIdList(listBeats: ArrayList<BeatData>): ArrayList<String> {
        val list : ArrayList<String> = ArrayList()

        for(i in 0 until listBeats.size)
        {
            list.add(listBeats[i].dist_id)
        }

        return list
    }

    private lateinit var retailerMasterVM: RetailerMasterVM

    var list : ArrayList<BeatRetailerData> = ArrayList()
    var listBeats : ArrayList<BeatData> = ArrayList()
    var listRetailers : ArrayList<RetailerData> = ArrayList()

    lateinit  var ctx : Context

    lateinit var adapter : RetailerAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retailerMasterVM =
            ViewModelProvider(this).get(RetailerMasterVM::class.java)
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)

        ctx = root.context

        retailerMasterVM.dateValue.observe(viewLifecycleOwner, Observer {
            tvDateValue.text = it
        })

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDate.setOnClickListener { retailerMasterVM.datePicker(view) }
        tvDateValue.setOnClickListener { retailerMasterVM.datePicker(view) }
        tvAdd.setOnClickListener {
           // Navigation.findNavController(view).navigate(R.id.action_add_visit)
        }

        callBeatList()



    }

    private fun callBeatList() {
        try {

            if (CommonMethods.isNetworkAvailable(ctx)) {
                val json = JSONObject()

                RetrofitService(
                    ctx,
                    this,
                    CommonKeys.BEAT_LIST ,
                    CommonKeys.BEAT_LIST_CODE,
                    1
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
            adapter.notifyItemRangeChanged(position,list.size)

        }

        deleteDialog.tvNo.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.show()
    }




    override fun response(code: Int, response: String) {
        try {
            when (code) {
                CommonKeys.BEAT_LIST_CODE -> {
                    try {

                        Log.e("BEAT_LIST_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {

                            val data = json.getJSONArray("data")



                            listBeats.clear()

                            listBeats.add(
                                BeatData(
                                    "","","","Select Distributor","",""
                                )
                            )

                            for (i in 0 until data.length()) {

                                val dataObj = data.getJSONObject(i)

                                if(dataObj.getString("distributor").isNotEmpty() &&dataObj.getString("distributor")!="null") {
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
                            CommonMethods.alertDialog(ctx, msg)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                CommonKeys.GET_RETAILERS_CODE -> {
                    try {

                        Log.e("GET_RETAILERS_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {

                            val data = json.getJSONObject("data")
                            val dataArray = data.getJSONArray("data")



                            listRetailers.clear()


                            for (i in 0 until dataArray.length()) {

                                val dataObj = dataArray.getJSONObject(i)


                                listRetailers.add(RetailerData(
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

                            setRetailerAdapter(listRetailers)

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

    private fun setRetailerAdapter(listRetailers: ArrayList<RetailerData>) {

        adapter  = RetailerAdapter(listRetailers)

        rvRetailerVisit.adapter = adapter
        adapter.onClicked(object :RetailerAdapter.CardInterface{
            override fun clickedSelected(position: Int, str: String) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })


}
}