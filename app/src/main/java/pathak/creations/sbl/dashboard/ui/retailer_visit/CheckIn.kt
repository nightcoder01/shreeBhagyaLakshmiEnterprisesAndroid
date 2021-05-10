package pathak.creations.sbl.dashboard.ui.retailer_visit

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.check_in.*
import org.json.JSONObject
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService

class CheckIn : Fragment(), RetrofitResponse {

    companion object {
        fun newInstance() = CheckIn()
    }

    private lateinit var viewModel: CheckInVM
    private lateinit var ctx: Context
     lateinit var tvText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ctx = container!!.context
        return inflater.inflate(R.layout.check_in, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getArgumentData()


        tvText = tvCheckIn

        tvCheckIn.setOnClickListener {

            if(valid())
            {callRemarksAdd(etRemarks.text.toString())}
        }
    }


    private fun valid(): Boolean {

        return when {

            etRemarks.text.isEmpty() -> {
                CommonMethods.alertDialog(ctx,"Please enter remarks.")
                false
            }

            else -> true
        }
    }


    var distributorName = ""
    var beatName = ""
    var retailer = ""
    var phone = ""
    var retailerId = ""
    var dist_id = ""
    private fun getArgumentData() {

        distributorName =   arguments?.getString("distributorName")!!
        beatName =   arguments?.getString("beatName")!!
        retailer =  arguments?.getString("retailer")!!
        phone =  arguments?.getString("phone")!!
        retailerId =   arguments?.getString("retailerId")!!
        dist_id =   arguments?.getString("dist_id")!!

    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CheckInVM::class.java)
        // TODO: Use the ViewModel
    }


    private fun callRemarksAdd(
        toString: String
    ) {
        try {

            if (CommonMethods.isNetworkAvailable(ctx)) {
                val json = JSONObject()

                json.put("beatname",beatName)
                json.put("dist_id",dist_id)
                json.put("distributor",distributorName)
                json.put("retailer_name",retailer)
                json.put("retailer_id",retailerId)
                json.put("remarks",toString)
                json.put("latitude","0.0")
                json.put("longitude","0.0")

                RetrofitService(
                    ctx,
                    this,
                    CommonKeys.ADD_VISIT,
                    CommonKeys.ADD_VISIT_CODE,
                    json,2
                ).callService(true, PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!)

                Log.e("callDistributorList", "=====$json")
                Log.e("callDistributorList", "=token====${PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!}")

            }
            else {
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
            when (code) {
                CommonKeys.ADD_VISIT_CODE -> {
                    try {

                        Log.e("RETAILER_LIST_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {


                            if(rbNoOrder.isChecked)
                            {
                                (ctx as Activity).onBackPressed()
                                Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show()

                            }
                            else
                            {
                                val bundle = bundleOf("distributorName" to distributorName,
                                    "beatName" to beatName,
                                    "retailer" to retailer,
                                    "phone" to phone,
                                    "retailerId" to retailerId,
                                    "salesman" to "",
                                    "dist_id" to dist_id
                                )

                                CommonMethods.hideKeyboard(tvText)
                                Navigation.findNavController(tvText).navigate(R.id.action_add_sales,bundle)

                               Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show()
                            }

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


}
