package pathak.creations.sbl.dashboard.ui.retailer_master

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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.custom_spinner.view.*
import kotlinx.android.synthetic.main.retailer_master.*
import org.json.JSONObject
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.custom_adapter.SpinnerCustomDistributorAdapter
import pathak.creations.sbl.data_class.BeatData
import pathak.creations.sbl.data_class.BeatRetailerData
import pathak.creations.sbl.data_class.RetailerData
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService

class RetailerMaster : Fragment(), RetrofitResponse {

    private lateinit var retailerMasterVM: RetailerMasterVM

    var list: ArrayList<BeatRetailerData> = ArrayList()
    var listRetailers: ArrayList<RetailerData> = ArrayList()

    lateinit var ctx: Context

    lateinit var adapter: RetailerAdapter


    var listDistName: ArrayList<String> = ArrayList()
    var listDistId: ArrayList<String> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retailerMasterVM =
            ViewModelProvider(this).get(RetailerMasterVM::class.java)
        val root = inflater.inflate(R.layout.retailer_master, container, false)

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


        // callBeatList()
        setDistributor()


    }


    private fun setDistributor() {
        if (PreferenceFile.retrieveKey(ctx, CommonKeys.TYPE).equals("distributor")) {
            tvDistributor2.hint = PreferenceFile.retrieveKey(ctx, CommonKeys.NAME)
            // callBeatList(PreferenceFile.retrieveKey(ctx,CommonKeys.NAME)!!)
            callDistRetailer(PreferenceFile.retrieveKey(ctx, CommonKeys.NAME)!!)
        } else {
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
                    CommonKeys.RETAILER_LIST,
                    CommonKeys.RETAILER_LIST_CODE,
                    1
                ).callService(true, PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!)

                Log.e("callDistributorList", "=====$json")
                Log.e(
                    "callDistributorList",
                    "=token====${PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!}"
                )

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

    private fun callDistRetailer(distId: String) {
        try {

            if (CommonMethods.isNetworkAvailable(ctx)) {
                val json = JSONObject()

                json.put("dist_id", distId)

                RetrofitService(
                    ctx,
                    this,
                    CommonKeys.GET_RETAILERS,
                    CommonKeys.GET_RETAILERS_CODE,
                    json, 2
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
        val list: ArrayList<String> = ArrayList()

        for (i in 0 until listBeats.size) {
            list.add(listBeats[i].distributor)
        }

        return list
    }

    private fun getDistIdList(listBeats: ArrayList<BeatData>): ArrayList<String> {
        val list: ArrayList<String> = ArrayList()

        for (i in 0 until listBeats.size) {
            list.add(listBeats[i].dist_id)
        }

        return list
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

                            listDistName.clear()
                            listDistId.clear()

                            for (i in 0 until dataArray.length()) {
                                val dataObj = dataArray.getJSONObject(i)
                                listDistId.add(dataObj.getString("dist_id"))
                                listDistName.add(dataObj.getString("name"))

                            }

                            setDistributorAdapter(listDistName, listDistId)

                        } else {
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


                                listRetailers.add(
                                    RetailerData(
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

                        } else {
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


    private fun setDistributorAdapter(
        listDistName: ArrayList<String>,
        listDistId: ArrayList<String>
    ) {
        tvDistributor2.setOnClickListener {
            openDistributorShort(tvDistributor2, listDistName, listDistId)
        }
    }


    var popupWindow: PopupWindow? = null


    private fun openDistributorShort(
        view: TextView,
        listDistName: ArrayList<String>,
        listDistId: ArrayList<String>
    ) {
        val inflater =
            view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.custom_spinner, null)



        popupWindow = PopupWindow(
            customView,
            view.width,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val adapter = SpinnerCustomDistributorAdapter(listDistName)
        customView.rvSpinner.adapter = adapter
        adapter.onClicked(object : SpinnerCustomDistributorAdapter.CardInterface {
            override fun clickedSelected(position: Int) {

                view.text = listDistName[position]
                popupWindow!!.dismiss()
                // callBeatList(listDistId[position])
                callDistRetailer(listDistId[position])
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

                if (s.isNullOrBlank()) {
                    val adapter2 = SpinnerCustomDistributorAdapter(listDistName)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object : SpinnerCustomDistributorAdapter.CardInterface {
                        override fun clickedSelected(position: Int) {

                            view.text = listDistName[position]
                            popupWindow!!.dismiss()
                            //callBeatList(listDistId[position])
                            callDistRetailer(listDistId[position])

                            //  callBeatRetailer(listBeats[position].dist_id,listBeats[position].beatname)
                        }
                    })
                } else {

                    val list: ArrayList<String> = ArrayList()
                    val list2: ArrayList<String> = ArrayList()

                    for (i in 0 until listDistName.size) {
                        if (listDistName[i].toLowerCase().contains(s.toString().toLowerCase(),false)) {
                            list.add(listDistName[i])
                            list2.add(listDistId[i])

                        }
                    }


                    val adapter2 = SpinnerCustomDistributorAdapter(list)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object : SpinnerCustomDistributorAdapter.CardInterface {
                        override fun clickedSelected(position: Int) {

                            view.text = list[position]
                            popupWindow!!.dismiss()
                            // callBeatList(list2[position])
                            callDistRetailer(list2[position])

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


    private fun setRetailerAdapter(listRetailers: ArrayList<RetailerData>) {

        adapter = RetailerAdapter(listRetailers)

        rvRetailerVisit.adapter = adapter
        adapter.onClicked(object : RetailerAdapter.CardInterface {
            override fun clickedSelected(position: Int, str: String) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })


    }
}