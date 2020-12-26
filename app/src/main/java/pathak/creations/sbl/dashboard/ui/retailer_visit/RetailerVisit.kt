package pathak.creations.sbl.dashboard.ui.retailer_visit

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.logout_alert.*
import kotlinx.android.synthetic.main.retailer_visit.*
import org.json.JSONObject
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService

class RetailerVisit : Fragment(), RetrofitResponse {


    private lateinit var retailerVisitVM: RetailerVisitVM


    var list : ArrayList<RetailerVisitData> = ArrayList()
    var listCount: ArrayList<CountData> = ArrayList()

    var count = ""
    var countInitial = "-1"


    lateinit var adapter : RetailerVisitAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retailerVisitVM = ViewModelProvider(this).get(RetailerVisitVM::class.java)
        val root = inflater.inflate(R.layout.retailer_visit, container, false)

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


        // setCountList()

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

    override fun onResume() {
        super.onResume()


        callList(countInitial)


    }

    private fun callList(countInitial: String) {
        try {

            var endPoint = ""
            if (countInitial != "-1") {
                endPoint = "?page=$countInitial"
            }


            if (CommonMethods.isNetworkAvailable(context!!)) {
                val json = JSONObject()

                RetrofitService(
                    context!!,
                    this,
                    CommonKeys.RETAILER_LIST + endPoint,
                    CommonKeys.RETAILER_LIST_CODE,
                    1
                ).callService(true, PreferenceFile.retrieveKey(context!!, CommonKeys.TOKEN)!!)

                Log.e("callList", "=====$json")

            } else {
                CommonMethods.alertDialog(
                    context!!,
                    getString(R.string.checkYourConnection)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    lateinit var deleteDialog: Dialog

    private fun deleteMethod(position: Int) {
        deleteDialog = Dialog(context!!)
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

        adapter  = RetailerVisitAdapter(list)

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

                                    setListData(list)

                                }


                            }





                            count = data.getString("last_page")

                            setCountList(count)
                        } else {
                            CommonMethods.alertDialog(context!!, msg)
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