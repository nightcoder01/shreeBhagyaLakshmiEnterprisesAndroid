package pathak.creations.sbl.dashboard.ui.cart

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.widget.AlertDialogLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.custom_spinner.view.*
import kotlinx.android.synthetic.main.my_cart_layout.*
import kotlinx.android.synthetic.main.my_cart_layout.tvBeatName
import kotlinx.android.synthetic.main.my_cart_layout.tvDistributor2
import kotlinx.android.synthetic.main.sales_order.*
import org.json.JSONArray
import org.json.JSONObject
import pathak.creations.sbl.AppController
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.custom_adapter.SpinnerCustomAdapter
import pathak.creations.sbl.custom_adapter.SpinnerCustomRetailerAdapter
import pathak.creations.sbl.data_classes.*
import pathak.creations.sbl.interfaces.CartDataChangeListener
import pathak.creations.sbl.interfaces.DataChangeListener
import pathak.creations.sbl.interfaces.RetailerDataChangeListener
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService
import java.text.SimpleDateFormat
import java.util.*


class MyCart : Fragment(), DataChangeListener<LiveData<List<Beat>>>,
    CartDataChangeListener<LiveData<List<Cart>>>, RetrofitResponse,
    RetailerDataChangeListener<LiveData<List<Retailer>>> {

    override fun RetailerDataChange(data: LiveData<List<Retailer>>) {
        data.observe(viewLifecycleOwner, Observer { retailer ->

            retailer?.let {
                setRetailerAdapter(it)

            }
        })
    }

    override fun CartDataChange(data: LiveData<List<Cart>>) {
       
       
        data.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.

            if(listCart.isEmpty())
            {
            dist?.let {
                listCart.addAll(dist)

                Log.e("dfsfdsfsdfsdfsdfs","=====${listCart.size}")

                setCartAdapter(listCart)

            }
            }
        })
    }

    var totalMute = MutableLiveData<String>().apply {
        value = ""
    }
    val totalLive: LiveData<String> = totalMute

    var size = ""
    var transactionNo = ""
    var beatName = ""
    var retailerName = ""

    private lateinit var myCartVM: MyCartVM

    lateinit var ctx: Context
    lateinit var tvSubmit: View
     var listCart: ArrayList<Cart> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        myCartVM = ViewModelProvider(this).get(MyCartVM::class.java)
        val root = inflater.inflate(R.layout.my_cart_layout , container, false)

        ctx = root.context
        callBeatList(PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_DISTRIBUTOR))

        return root
    }

    private fun callBeatList(distID: String?) {
        try {

            wordViewModel.getBeatFromDist(distID!!, this)

        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun DataChange(data: LiveData<List<Beat>>) {

        data.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.
            Log.e("callBeatRetailer", "==sadsd===${dist.size}===")

            dist?.let {
                setBeatAdapter(it)
            }
        })
    }

    private fun setBeatAdapter(listBeats: List<Beat>) {

        tvBeatName.setOnClickListener {
            openPopShortBy(tvBeatName,listBeats)
        }

        if(PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_BEATNAME)!=null)
        {
            tvBeatName.text =PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_BEATNAME)

            callBeatRetailer(PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_BEATNAME)!!)
        }

    }

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

                callBeatRetailer(listBeats[position].beatname)
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
                            callBeatRetailer(listBeats[position].beatname)
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
                            callBeatRetailer(list[position].beatname)
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

    private fun callBeatRetailer(beatname: String) {
        try {


            wordViewModel.getBeatRetailer(beatname, this)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getArgumentedData()
        tvContinueShopping.setOnClickListener { (ctx as Activity).onBackPressed() }

        totalLive.observe(viewLifecycleOwner, Observer {
            tvTotalValue.text = it
            tvGrandTotalValue.text = it
        })


        tvClearCart.setOnClickListener {
            callDeleteAllDialog()
        }


        tvSubmitCart.setOnClickListener {
            tvSubmit = it
            if(valid()) callAddCart()
        }

        //set live data observer
        wordViewModel.allCart.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.

            if(listCart.isEmpty())
            {
            dist?.let {

                listCart.addAll(dist)
                Log.e("dsffsdfds","=====${listCart.size}")


                setCartAdapter(listCart)

            }
            }
        })

        wordViewModel.allOrders.observe(viewLifecycleOwner, Observer { dist ->
             size = (dist.size+1).toString()

            val dNow = Date()
            val ft = SimpleDateFormat("yyyy")
            val year = ft.format(dNow)

            size = if(size.length!=2){"000$size"}else{"00$size"}
            transactionNo = "SO/${PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_DISTRIBUTOR)}/$year/$size"
            tvTransactionValue.text = transactionNo

        })
    }

    private fun getArgumentedData() {

        if(arguments!=null) {
            tvBeatName.text =arguments?.getString("beatName")

            val beatName :String= arguments?.getString("beatName")!!
            val retailer :String= arguments?.getString("retailer")!!
            val retailerId :String= arguments?.getString("retailerId")!!

            callBeatRetailer(beatName)

            tvDistributor2.text = retailer
            listCart.clear()
            wordViewModel.getCartFromDist(retailerId, this@MyCart)
        }
    }

    private fun valid(): Boolean {

        return when {
            tvBeatName.text.isNullOrBlank() -> {
                CommonMethods.alertDialog(ctx,"Please select Beat Name")
                false
            }
            tvDistributor2.text.isNullOrBlank() -> {
                CommonMethods.alertDialog(ctx,"Please select Retailer Name")
                false
            }
            else -> true
        }
    }

    private fun setTotal(listCart: List<Cart>): String {

        var str = "0"

        for(element in listCart)
        {
            str =  String.format("%.2f",str.toFloat()+(element.customPrice.toFloat()* element.itemCount.toFloat()))
        }
        return str
    }

    private fun callAddCart() {
        try {


            val jsonMain = JSONObject()

            val jsonArray = JSONArray()

            val json = JSONObject()

            val dNow = Date()
            val ft = SimpleDateFormat("yyMMddhhmmssMs")
            val datetime = ft.format(dNow)
            val year = SimpleDateFormat("yyyy").format(dNow)

            for(i in 0 until listCart.size) {

                Log.e("sadfsdfasd","=======${listCart[i].distID}====$year===$size=")

                json.put("dist_code", listCart[i].distID)
                json.put("dist", listCart[i].dist_name)
                json.put("retailer_code", listCart[i].retailer_code)
                json.put("retailer", listCart[i].retailer_name)
                json.put("beatname", listCart[i].beatName)
                json.put("catgroup", listCart[i].cat_group)
                json.put("category", listCart[i].category)
                json.put("category_code", listCart[i].cat_code)
                json.put("category_description", listCart[i].name)
                json.put("qty", listCart[i].itemCount)
                json.put("ptr_price", listCart[i].ptr_price)
                json.put("ptd_price", listCart[i].ptd_price)
                json.put("total_ptr_price", listCart[i].overAllPrice)
                json.put("total_ptd_price", listCart[i].ptd_total)



                wordViewModel.insertOrders(
                    Orders(datetime+i
                        ,""
                        ,""
                        ,""
                        ,""
                        ,transactionNo
                        ,""
                        ,listCart[i].distID
                        ,listCart[i].dist_name
                        ,listCart[i].retailer_name
                        ,listCart[i].retailer_code
                        ,listCart[i].beatName
                        ,""
                        ,listCart[i].cat_group
                        ,listCart[i].category
                        ,listCart[i].cat_code
                        ,listCart[i].name
                        ,listCart[i].itemCount
                        ,""
                        ,listCart[i].overAllPrice
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,""
                        ,listCart[i].ptr_price
                        ,listCart[i].ptd_price
                        ,""
                        ,""
                        ,""
                    )
                )
            }

            wordViewModel.insertTransactions(
                Transactions(
                0,
                    transactionNo,
                    PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_DISTRIBUTOR)!!,
                    PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_DISTRIBUTOR)!!,
                    listCart[0].retailer_code,
                    listCart[0].retailer_name,
                    listCart[0].beatName,
                    listCart.size.toString(),
                    tvTotalValue.text.toString()
            ))




            jsonArray.put(json)
            jsonMain.put("items",jsonArray)

            Log.e("fasdfasfdfsd",jsonMain.toString())


            if (CommonMethods.isNetworkAvailable(ctx)) {

                RetrofitService(
                    ctx,
                    this,
                    CommonKeys.ADD_CART ,
                    CommonKeys.ADD_CART_CODE,
                    jsonMain,2
                ).callService(true, PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!)

            }
            else {

                for(i in 0 until listCart.size)
                {
                    wordViewModel.deleteCart(listCart[i].cartId)
                }

                Toast.makeText(ctx,"Data submitted offline",Toast.LENGTH_SHORT).show()



                val bundle = bundleOf(
                    "count" to listCart.size.toString(),
                    "beatName" to tvBeatName.text.toString(),
                    "retailerName" to tvDistributor2.text.toString(),
                    "total" to tvTotalValue.text.toString(),
                    "grandTotal" to tvGrandTotalValue.text.toString(),
                    "listCart" to listCart
                )
                Navigation.findNavController(tvSubmit)
                    .navigate(R.id.action_order_detail, bundle)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun callDeleteAllDialog() {
        val inflater = LayoutInflater.from(ctx)
        val view1 = inflater.inflate(R.layout.logout_alert, null)
        val deleteDialo = AlertDialog.Builder(ctx).create()

        deleteDialo.setView(view1)
        val btnYes: TextView = view1.findViewById(R.id.tvYes)
        val btnNo: TextView = view1.findViewById(R.id.tvNo)
        btnYes.setOnClickListener { view2 ->

            deleteDialo.dismiss()
            wordViewModel.deleteAllCart()
        }
        btnNo.setOnClickListener { view22 -> deleteDialo.dismiss() }

        deleteDialo.show()

    }


    private fun setCartAdapter(listt: List<Cart>) {

        val list :MutableList<Cart> = mutableListOf()
         list.addAll(listt)

        val adapter  = MyCartAdapter(list)

        if(list.isEmpty())
        {

            clCart.visibility = View.GONE
            tvTransaction.visibility = View.GONE
            tvTransactionValue.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
            tvTotal.visibility = View.GONE
            tvTotalValue.visibility = View.GONE
            tvGrandTotalValue.visibility = View.GONE
            tvGrandTotal.visibility = View.GONE
        }
        else
        {
            clCart.visibility = View.VISIBLE
            tvTransaction.visibility = View.VISIBLE
            tvTransactionValue.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
            tvTotal.visibility = View.VISIBLE
            tvTotalValue.visibility = View.VISIBLE
            tvGrandTotalValue.visibility = View.VISIBLE
            tvGrandTotal.visibility = View.VISIBLE
        }

        totalMute.value = setTotal(list)

        rvMyCart.adapter = adapter
        adapter.onClicked(object: MyCartAdapter.CardInterface{


            override fun valueChanged(pos: Int, str: String) {
                list[pos].customPrice = str

                wordViewModel.updateCart(list[pos])
                adapter.notifyItemChanged(pos)
                totalMute.value = setTotal(list)


            }

            override fun clickedSelected(pos: Int, str: String) {

                wordViewModel.updateCart(list[pos])
                totalMute.value = setTotal(list)

                if(list[pos].itemCount=="0")
                {
                    wordViewModel.deleteCart(list[pos].cartId)
                    if(list.size==1)
                    {
                        list.clear()
                        setCartAdapter(list)
                    }
                    else
                    {
                        list.removeAt(pos)
                        setCartAdapter(list)
                    }
                }



                //callNumberList(list,adapter,pos)

               /* if(str=="add")
                {
                    if(list[pos].itemCount.toInt()>9999)
                    {
                        Toast.makeText(ctx,"max limit crossed", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        list[pos].itemCount = (list[pos].itemCount.toInt()+1).toString()
                        wordViewModel.updateCart(list[pos])
                        adapter.notifyItemChanged(pos)
                        totalMute.value = setTotal(list)

                    }
                }
                if(str=="remove")
                {
                    if(list[pos].itemCount.toInt()<2)
                    {

                        wordViewModel.deleteCart(list[pos].cartId)

                        if(list.size==1)
                            {
                                list.clear()
                               setCartAdapter(list)
                        }
                        else
                            {
                                list.removeAt(pos)
                                setCartAdapter(list)
                            }


                    }

                    else
                    {
                        list[pos].itemCount = (list[pos].itemCount.toInt()-1).toString()

                        wordViewModel.updateCart(list[pos])
                        adapter.notifyItemChanged(pos)
                        totalMute.value = setTotal(list)

                    }
                }
                if(str=="long")
                {

                    callNumberList(list,adapter,pos)

                    Toast.makeText(ctx,"long",Toast.LENGTH_SHORT).show()
                }*/

            }
        })

    }

    private lateinit var dialogBuilderMain  : AlertDialog

    private fun callNumberList(
        subList: List<Cart>,
        adapter2: MyCartAdapter,
        pos: Int
    ) {

        val dialogBuilder = AlertDialog.Builder(ctx)
        val layout = AlertDialogLayout.inflate(ctx, R.layout.custom_count,null)
        dialogBuilder.setView(layout)

        val tvSubmit :TextView= layout.findViewById(R.id.tvSubmit)
        val npItem : NumberPicker = layout.findViewById(R.id.npItem)
        val etEnterPrice : EditText = layout.findViewById(R.id.etEnterPrice)

        dialogBuilderMain = dialogBuilder.create()
        dialogBuilderMain.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBuilderMain.setCancelable(false)
        dialogBuilderMain.setCanceledOnTouchOutside(true)

        tvSubmit.setOnClickListener {

            Toast.makeText(ctx,npItem.value.toString(),Toast.LENGTH_SHORT).show()
            if(etEnterPrice.text.toString()!="0" && etEnterPrice.text.toString().isEmpty() ) {
                subList[pos].itemCount = etEnterPrice.text.toString()
                wordViewModel.updateCart(subList[pos])
                adapter2.notifyItemChanged(pos)
                totalMute.value = setTotal(subList)
            }
            dialogBuilderMain.dismiss()


        }
        npItem.maxValue = 9999
        npItem.minValue = 1

        dialogBuilderMain.show()
    }

    private fun setRetailerAdapter(list: List<Retailer>) {
        tvDistributor2.setOnClickListener {
            openDistributorShort(tvDistributor2,list)
        }



        if(PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_RETAILERNAME)!=null)
        {

            for(i in 0 until list.size)
            {
                if(list[i].retailer_name==PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_RETAILERNAME))
                {
                    tvDistributor2.text =list[i].retailer_name
                    listCart.clear()
                    wordViewModel.getCartFromDist(list[i].retailer_id, this@MyCart)

                }
            }




            //  callBeatRetailer(listBeats[0].dist_id,PreferenceFile.retrieveKey(ctx!!,CommonKeys.SELECTED_BEATNAME)!!)
        }
    }

    var popupWindow: PopupWindow? = null

    private fun openDistributorShort(
        view: TextView,
        list: List<Retailer>
    ) {
        val inflater =
            view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.custom_spinner, null)

        popupWindow = PopupWindow(
            customView,
            view.width,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val adapter = SpinnerCustomRetailerAdapter(list)
        customView.rvSpinner.adapter = adapter
        adapter.onClicked(object : SpinnerCustomRetailerAdapter.CardInterface {
            override fun clickedSelected(position: Int) {

                view.text = list[position].retailer_name
                popupWindow!!.dismiss()
                Log.e("wordViewModel.allCart--",list[position].retailer_id)
                listCart.clear()
                wordViewModel.getCartFromDist(list[position].retailer_id, this@MyCart)

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
                    val adapter2 = SpinnerCustomRetailerAdapter(list)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object : SpinnerCustomRetailerAdapter.CardInterface {
                        override fun clickedSelected(position: Int) {

                            view.text = list[position].retailer_name
                            popupWindow!!.dismiss()

                            listCart.clear()
                            wordViewModel.getCartFromDist(list[position].retailer_id, this@MyCart)

                        }
                    })
                } else {

                    val list2: ArrayList<Retailer> = ArrayList()

                    for (i in list.indices) {
                        if (list[i].retailer_name.toLowerCase().contains(s.toString().toLowerCase(),false)) {
                            list2.add(list[i])

                        }
                    }


                    val adapter2 = SpinnerCustomRetailerAdapter(list2)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object : SpinnerCustomRetailerAdapter.CardInterface {
                        override fun clickedSelected(position: Int) {

                            view.text = list2[position].retailer_name
                            popupWindow!!.dismiss()
                            listCart.clear()
                            wordViewModel.getCartFromDist(list2[position].retailer_id, this@MyCart)

                        } }) } }
        })

        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.showAsDropDown(view)
        popupWindow!!.isFocusable = true
        popupWindow!!.update()

    }


    override fun response(code: Int, response: String) {
        try {
            when (code) {
                CommonKeys.ADD_CART_CODE -> {
                    try {

                        Log.e("ADD_CART_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {

                            for(i in 0 until listCart.size)
                            {
                                wordViewModel.deleteCart(listCart[i].cartId)
                            }

                            Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show()



                            val bundle = bundleOf(
                                "count" to listCart.size.toString(),
                                "beatName" to tvBeatName.text.toString(),
                                "retailerName" to tvDistributor2.text.toString(),
                                "total" to tvTotalValue.text.toString(),
                                "grandTotal" to tvGrandTotalValue.text.toString(),
                                "listCart" to listCart
                            )
                            Navigation.findNavController(tvSubmit)
                                .navigate(R.id.action_order_detail, bundle)
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
    //data base work
    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory(((context as Activity).application as AppController).repository)
    }

}