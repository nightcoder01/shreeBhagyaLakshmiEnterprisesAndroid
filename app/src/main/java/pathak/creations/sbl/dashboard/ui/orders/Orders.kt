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
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
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
import pathak.creations.sbl.data_classes.Transactions
import pathak.creations.sbl.data_classes.WordViewModel
import pathak.creations.sbl.data_classes.WordViewModelFactory
import pathak.creations.sbl.interfaces.OrderDataChangeListener
import pathak.creations.sbl.interfaces.TransactionsDataChangeListener
import pathak.creations.sbl.retrofit.RetrofitResponse


class Orders : Fragment(),TransactionsDataChangeListener<LiveData<List<Transactions>>>
    , OrderDataChangeListener<LiveData<List<pathak.creations.sbl.data_classes.Orders>>>,
    RetrofitResponse {


    override fun TransactionDataChange(data: LiveData<List<Transactions>>) {
        data.observe(viewLifecycleOwner, Observer { dist ->

            cbAllOrder.isChecked =false
            listTransactions.clear()
            dist?.let {
                listTransactions.addAll(dist)
                  setTransactionAdapter(listTransactions)

            }
        })
    }


    override fun OrderDataChange(data: LiveData<List<pathak.creations.sbl.data_classes.Orders>>) {
        data.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.
            listOrders.clear()
            dist?.let {
                listOrders.addAll(dist)
              //  setCartAdapter(listOrders)

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
    var listTransactions: ArrayList<Transactions> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ctx = container!!.context
        return inflater.inflate(R.layout.orders, container, false)
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cbAllOrder.isChecked =true
        setDistributor()

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
              //  setCartAdapter(listOrders)
            }
        })
        wordViewModel.allTransactions.observe(viewLifecycleOwner, Observer { trans ->
            // Update the cached copy of the words in the adapter.
            listTransactions.clear()
            trans?.let {

                listTransactions.addAll(trans)
                //setCartAdapter(listTransactions)
                setTransactionAdapter(listTransactions)
            }
        })


        cbAllOrder.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked) {
                tvDistributor2.text = ""
                tvRetailer2.text = ""
                wordViewModel.allTransactions.observe(viewLifecycleOwner, Observer { trans ->
                    listTransactions.clear()
                    trans?.let {
                        listTransactions.addAll(trans)
                        setTransactionAdapter(listTransactions)
                    }
                })

            }
        }



    }
    private fun setDistributor() {
            tvDistributor2.hint = PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_DISTRIBUTOR_NAME)
            wordViewModel.getOrdersFromDist(PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_DISTRIBUTOR_NAME)!!, this@Orders)
            callRetailer(PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_DISTRIBUTOR)!!)
    }

    private fun setTransactionAdapter(list: ArrayList<Transactions>) {
        val adapter  = MyTransactionsAdapter(list)

        if(list.isEmpty())
        {
            tvNoData.visibility = View.VISIBLE
        }
        else
        {
            tvNoData.visibility = View.GONE
        }


        rvMyOrders.adapter = adapter

        adapter.onClicked(object : MyTransactionsAdapter.CardInterface{
            override fun clickedSelected(pos: Int) {



                val bundle = bundleOf(
                        "transactionNo" to list[pos].transactionNo,
                    "retailerName" to list[pos].retailerName,
                    "distributorName" to list[pos].distributorName,
                    "total" to list[pos].totalAmount
                )
                Navigation.findNavController(rvMyOrders)
                    .navigate(R.id.action_transaction_detail, bundle)


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

            /*if (CommonMethods.isNetworkAvailable(ctx)) {
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

            }
            else {
                CommonMethods.alertDialog(
                    ctx,
                    getString(R.string.checkYourConnection)
                )
            }*/

            try {



                wordViewModel.allRetailer.observe(viewLifecycleOwner, Observer { retail ->
                    // Update the cached copy of the words in the adapter.
                    listRetailerName.clear()
                    listRetailerId.clear()

                    retail?.let {
                        for (i in it.indices) {
                            //val dataObj = it.getJSONObject(i)

                            listRetailerName.add(it[i].retailer_name)
                            listRetailerId.add(it[i].retailer_id)
                        }
                       // listRetailers.addAll(it)
                        //setRetailerAdapter(it)
                        setRetailerAdapter(listRetailerName,listRetailerId)


                    }
                })

            } catch (e: Exception) {
                e.printStackTrace()
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
                wordViewModel.getTransactionsFromRetailer(listId[position], this@Orders)

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
                            wordViewModel.getTransactionsFromRetailer(listId[position], this@Orders)

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
                            wordViewModel.getTransactionsFromRetailer(listId2[position], this@Orders)

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


    private fun setDistributorAdapter(
        list: List<Distributor>

    ) {
        tvDistributor2.setOnClickListener {
           // openDistributorShort(tvDistributor2,list)
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
