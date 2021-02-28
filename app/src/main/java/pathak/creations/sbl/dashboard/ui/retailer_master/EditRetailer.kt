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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.custom_spinner.view.*
import kotlinx.android.synthetic.main.edit_retailer.*
import org.json.JSONObject
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.custom_adapter.SpinnerCustomAdapter
import pathak.creations.sbl.data_class.BeatData
import pathak.creations.sbl.data_classes.Beat
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService


class EditRetailer : Fragment(), RetrofitResponse {

    companion object {
        fun newInstance() = EditRetailer()
    }

    private lateinit var viewModel: EditRetailerVM
     lateinit var ctx: Context

    var distId =""
    var distributor =""

    var listBeats : ArrayList<BeatData> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_retailer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ctx = view.context

        getArgumentData()
        tvCancel.setOnClickListener { (ctx as Activity).onBackPressed() }

    }

    private fun getArgumentData() {
        distId=  arguments?.getString("dist_id")!!
        distributor=  arguments?.getString("distributor")!!





        /*val bundle = bundleOf("dist_id" to listRetailers[position].dist_id,
            "distributor" to listRetailers[position].distributor,
            "retailer_id" to listRetailers[position].retailer_id,
            "retailer_name" to listRetailers[position].retailer_name,
            "beatName" to listRetailers[position].beatname,
            "address" to listRetailers[position].address,
            "mobile" to listRetailers[position].mobile,
            "areaname" to listRetailers[position].areaname,
            "state" to listRetailers[position].state,
            "gstin" to listRetailers[position].gstin,
            "classification" to listRetailers[position].classification,
            "retailer_type" to listRetailers[position].retailer_type
        )*/





        tvDistributor2.text = distributor
        etRetailerName.text =Editable.Factory.getInstance().newEditable(arguments?.getString("retailer_name")!!)
        etRetailerId.text =Editable.Factory.getInstance().newEditable(arguments?.getString("retailer_id")!!)
        tvBeatName2.text =Editable.Factory.getInstance().newEditable(arguments?.getString("beatName")!!)
        etAddress.text =Editable.Factory.getInstance().newEditable(arguments?.getString("address")!!)
        etMobile.text =Editable.Factory.getInstance().newEditable(arguments?.getString("mobile")!!)
        etAreaName.text =Editable.Factory.getInstance().newEditable(arguments?.getString("areaname")!!)
        etStateName.text =Editable.Factory.getInstance().newEditable(arguments?.getString("state")!!)
        etGST.text =Editable.Factory.getInstance().newEditable(arguments?.getString("gstin")!!)
        tvClassification2.text =Editable.Factory.getInstance().newEditable(arguments?.getString("classification")!!)
        tvRetailerType2.text =Editable.Factory.getInstance().newEditable(arguments?.getString("retailer_type")!!)

        //callBeatList(distId)


        tvSubmit.setOnClickListener {

            callEditRetailer(arguments?.getString("idd")!!)
        }

    }

    private fun callEditRetailer(idd: String) {
        try {

            if (CommonMethods.isNetworkAvailable(ctx)) {
                val json = JSONObject()


                json.put("id",idd)
                json.put("mobile",etMobile.text.toString())
                json.put("address",etAddress.text.toString())

                RetrofitService(
                    ctx,
                    this,
                    CommonKeys.EDIT_RETAILER ,
                    CommonKeys.EDIT_RETAILER_CODE,
                    json,2
                ).callService(true, PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!)

                Log.e("callEditRetailer", "=====$json")
                Log.e("callEditRetailer", "=token====${PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!}")

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


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditRetailerVM::class.java)
        // TODO: Use the ViewModel
    }


    private fun callBeatList(distID: String?) {
        try {

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
                          //  callBeatRetailer(listBeats[position].dist_id,listBeats[position].beatname)
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


                         //   setBeatAdapter(listBeats)
                        }

                        else {
                            CommonMethods.alertDialog(ctx, msg)
                        }


                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                CommonKeys.EDIT_RETAILER_CODE -> {
                    try {

                        Log.e("EDIT_RETAILER_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {

                            CommonMethods.alertDialog(ctx, msg)

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


}
