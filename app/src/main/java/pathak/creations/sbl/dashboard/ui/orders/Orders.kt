package pathak.creations.sbl.dashboard.ui.orders

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
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.custom_spinner.view.*
import kotlinx.android.synthetic.main.orders.*
import org.json.JSONObject
import pathak.creations.sbl.AppController
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.custom_adapter.SpinnerCustomDistributorAdapter
import pathak.creations.sbl.custom_adapter.StringCustomAdapter
import pathak.creations.sbl.data_classes.Distributor
import pathak.creations.sbl.data_classes.WordViewModel
import pathak.creations.sbl.data_classes.WordViewModelFactory
import pathak.creations.sbl.interfaces.OrderDataChangeListener
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService


class Orders : Fragment(), OrderDataChangeListener<LiveData<List<pathak.creations.sbl.data_classes.Orders>>>,
    RetrofitResponse {


    override fun OrderDataChange(data: LiveData<List<pathak.creations.sbl.data_classes.Orders>>) {
        data.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.
            listOrders.clear()
            dist?.let {
                listOrders.addAll(dist)
                setCartAdapter(listOrders)

            }
        })
    }

    companion object {
        fun newInstance() = Orders()
    }

    private lateinit var viewModel: OrdersVM
    lateinit var ctx: Context


    var listRetailerName : ArrayList<String> = ArrayList()
    var listRetailerId : ArrayList<String> = ArrayList()



    var listOrders: ArrayList<pathak.creations.sbl.data_classes.Orders> = ArrayList()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ctx = container!!.context
        return inflater.inflate(R.layout.orders, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OrdersVM::class.java)
        // TODO: Use the ViewModel
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wordViewModel.allDistributor.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.

            dist?.let {

                setDistributorAdapter(it)

            }
        })

        //set live data observer
        wordViewModel.allOrders.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.
            listOrders.clear()
            dist?.let {

                listOrders.addAll(dist)
                setCartAdapter(listOrders)
            }
        })

    }

    private fun setCartAdapter(list: List<pathak.creations.sbl.data_classes.Orders>) {
        val adapter  = MyOrdersAdapter(list)

        if(list.isEmpty())
        {
            tvNoData.visibility = View.VISIBLE
        }
        else
        {
            tvNoData.visibility = View.GONE
        }


        rvMyOrders.adapter = adapter

    }

    private fun callRetailer(distID: String) {
        try {

            //retailer list
            if (CommonMethods.isNetworkAvailable(ctx)) {
                val json = JSONObject()
                json.put("dist_id", distID)
                RetrofitService(
                    ctx,
                    this@Orders,
                    CommonKeys.GET_RETAILERS,
                    CommonKeys.GET_RETAILERS_CODE,
                    json,2
                ).callService(true, PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!)

                Log.e("callRetailer", "=====$json")
                Log.e(
                    "callRetailer",
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

    override fun response(code: Int, response: String) {
        try {
            when (code) {
                CommonKeys.GET_RETAILERS_CODE -> {
                    try {

                        Log.e("ALL_RETAILERS_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {

                            val data = json.getJSONObject("data")
                            val dataArray = data.getJSONArray("data")



                            listRetailerName.clear()
                            listRetailerId.clear()

                            for (i in 0 until dataArray.length()) {
                                val dataObj = dataArray.getJSONObject(i)

                                listRetailerName.add(dataObj.getString("retailer_name"))
                                listRetailerId.add(dataObj.getString("retailer_id"))
                            }
                            Log.e("listRetailerName", "=====${listRetailerName.size}==$listRetailerName")

                            setRetailerAdapter(listRetailerName,listRetailerId)

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
        }    }

    private fun setRetailerAdapter(
        list: ArrayList<String>,
        listId: ArrayList<String>
    ) {
        tvRetailer2.setOnClickListener {
            openRetailerShort(tvRetailer2,list,listId)
        }
    }
    private fun openRetailerShort(
        view: TextView,
        list: List<String>,
        listId: ArrayList<String>
    ) {
        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.custom_spinner, null)

        popupWindow = PopupWindow(
            customView,
            view.width,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val adapter = StringCustomAdapter(list)
        customView.rvSpinner.adapter = adapter
        adapter.onClicked(object :StringCustomAdapter.CardInterface{
            override fun clickedSelected(position: Int) {

                view.text =list[position]
                popupWindow!!.dismiss()
                wordViewModel.getOrdersFromRetailer(listId[position], this@Orders)

               // retailerIDMain =listBeatsRetailer[position].retailer_id
               // retailerIDName =listBeatsRetailer[position].retailer_name

                // callCategory(listBeatsRetailer[position].retailer_id)
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
                    val adapter2 = StringCustomAdapter(list)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :StringCustomAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =list[position]
                            popupWindow!!.dismiss()
                            wordViewModel.getOrdersFromRetailer(listId[position], this@Orders)

                            //  retailerIDMain =listBeatsRetailer[position].retailer_id
                          //  retailerIDName =listBeatsRetailer[position].retailer_name

                            //  callCategory(listBeatsRetailer[position].retailer_id)
                            //  callBeatRetailer(listBeatsRetailer[position].dist_id,listBeats[position].beatname)
                        }


                    })
                }
                else
                {

                    val list2 : ArrayList<String> = ArrayList()
                    val listId2 : ArrayList<String> = ArrayList()

                    for(i in list.indices)
                    {
                        if(list[i].toLowerCase().contains(s.toString().toLowerCase(),false))
                        {
                            list2.add(list[i])
                            listId2.add(listId[i])

                        }
                    }


                    val adapter2 = StringCustomAdapter(list2)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :StringCustomAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =list2[position]
                            popupWindow!!.dismiss()
                            wordViewModel.getOrdersFromRetailer(listId2[position], this@Orders)

                           // retailerIDMain =list[position].retailer_id
                           // retailerIDName =list[position].retailer_name
                            //  callCategory(listBeatsRetailer[position].retailer_id)
                            //callBeatRetailer(list[position].dist_id,list[position].beatname)
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


    private fun setDistributorAdapter(
        list: List<Distributor>

    ) {
        tvDistributor2.setOnClickListener {
            openDistributorShort(tvDistributor2,list)
        }    }

    var popupWindow: PopupWindow? = null


    private fun openDistributorShort(
        view: TextView,
        listDist: List<Distributor>

    )
    {
        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.custom_spinner, null)

        popupWindow = PopupWindow(customView, view.width, WindowManager.LayoutParams.WRAP_CONTENT)

        val adapter = SpinnerCustomDistributorAdapter(listDist)
        customView.rvSpinner.adapter = adapter
        adapter.onClicked(object : SpinnerCustomDistributorAdapter.CardInterface{
            override fun clickedSelected(position: Int) {

                view.text =listDist[position].distName
                popupWindow!!.dismiss()


                wordViewModel.getOrdersFromDist(listDist[position].distID, this@Orders)
                callRetailer(listDist[position].distID)
                // PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.SELECTED_DISTRIBUTOR,listDist[position].distID)
                // PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.SELECTED_DISTRIBUTOR_NAME,listDist[position].distName)

               //  val dNow = Date()
               //  val ft = SimpleDateFormat("yyMMddhhmmssMs")
               //  val currentDate = ft.format(dNow)
               //  PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.CURRENT_DATE,currentDate)

                // val it = Intent(this@SelectDistributor, DashBoard::class.java)
                // it.flags =
                // Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                // startActivity(it)

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


                            wordViewModel.getOrdersFromDist(listDist[position].distID, this@Orders)
                            callRetailer(listDist[position].distID)


                          //  PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.SELECTED_DISTRIBUTOR,listDist[position].distID)
                          //  PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.SELECTED_DISTRIBUTOR_NAME,listDist[position].distName)

                           // val dNow = Date()
                          //  val ft = SimpleDateFormat("yyMMddhhmmssMs")
                           // val currentDate = ft.format(dNow)
                          //  PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.CURRENT_DATE,currentDate)

                            /*if (PreferenceFile.retrieveKey(this@SelectDistributor, CommonKeys.IS_FIRST_CHECKED).equals("false", false)) {
                               // callAllServices()
                            }*/
                          //  val it = Intent(this@SelectDistributor, DashBoard::class.java)
                           // it.flags =
                         //       Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                          //  startActivity(it)

                            //  callBeatList(listDist[position].distID)
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

                            wordViewModel.getOrdersFromDist(list[position].distID, this@Orders)
                            callRetailer(list[position].distID)


                            //  PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.SELECTED_DISTRIBUTOR,list[position].distID)
                          //  PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.SELECTED_DISTRIBUTOR_NAME,list[position].distName)

                          //  val dNow = Date()
                          //  val ft = SimpleDateFormat("yyMMddhhmmssMs")
                          //  val currentDate = ft.format(dNow)
                          //  PreferenceFile.storeKey(this@SelectDistributor,CommonKeys.CURRENT_DATE,currentDate)

                            /*if (PreferenceFile.retrieveKey(this@SelectDistributor, CommonKeys.IS_FIRST_CHECKED).equals("false", false)) {
                                callAllServices()
                            }*/

                          //  val it = Intent(this@SelectDistributor, DashBoard::class.java)
                          //  it.flags =
                          //      Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                           // startActivity(it)

                            //  callBeatList(list[position].distID)

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


    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory(((context as Activity).application as AppController).repository)
    }


}