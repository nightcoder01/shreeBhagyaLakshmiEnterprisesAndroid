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
import android.widget.NumberPicker
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AlertDialogLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.custom_spinner.view.*
import kotlinx.android.synthetic.main.my_cart_layout.*
import org.json.JSONArray
import org.json.JSONObject
import pathak.creations.sbl.AppController
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.custom_adapter.SpinnerCustomRetailerAdapter
import pathak.creations.sbl.data_classes.*
import pathak.creations.sbl.interfaces.DataChangeListener
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyCart : Fragment(), DataChangeListener<LiveData<List<Cart>>>, RetrofitResponse {


    override fun DataChange(data: LiveData<List<Cart>>) {
       
       
        data.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.
            listCart.clear()
            dist?.let {
                listCart.addAll(dist)
                setCartAdapter(listCart)

            }
        })
    }


    private lateinit var myCartVM: MyCartVM

    lateinit var ctx: Context
     var listCart: ArrayList<Cart> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myCartVM =
            ViewModelProvider(this).get(MyCartVM::class.java)
        val root = inflater.inflate(R.layout.my_cart_layout , container, false)

        ctx = root.context

        return root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvClearCart.setOnClickListener {
            callDeleteAllDialog()

        }

        tvSubmitCart.setOnClickListener {
            callAddCart()

        }

        //set live data observer
        wordViewModel.allCart.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.
            listCart.clear()
            dist?.let {

                listCart.addAll(dist)
                setCartAdapter(listCart)

            }
        })

        //set live data observer
        wordViewModel.allRetailer.observe(viewLifecycleOwner, Observer { retail ->
            // Update the cached copy of the words in the adapter.

            retail?.let {

                setRetailerAdapter(it)

            }
        })


    }

    private fun callAddCart() {
        try {

            if (CommonMethods.isNetworkAvailable(ctx)) {

                val jsonMain = JSONObject()


                val jsonArray = JSONArray()

                val json = JSONObject()

                val dNow = Date()
                val ft = SimpleDateFormat("yyMMddhhmmssMs")
                val datetime = ft.format(dNow)

                for(i in 0 until listCart.size) {
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
                            ,""
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

                jsonArray.put(json)
                jsonMain.put("items",jsonArray)

                Log.e("fasdfasfdfsd",jsonMain.toString())

                RetrofitService(
                    ctx,
                    this,
                    CommonKeys.ADD_CART ,
                    CommonKeys.ADD_CART_CODE,
                    jsonMain,2
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


    private fun callDeleteAllDialog() {
        val inflater = LayoutInflater.from(ctx)
        val view1 = inflater.inflate(R.layout.logout_alert, null)
        val deleteDialo = AlertDialog.Builder(ctx).create()

        val btnYes: TextView
        val btnNo: TextView

        deleteDialo.setView(view1)
        btnYes = view1.findViewById(R.id.tvYes)
        btnNo = view1.findViewById(R.id.tvNo)
        btnYes.setOnClickListener { view2 ->

            deleteDialo.dismiss()
            wordViewModel.deleteAllCart()
        }
        btnNo.setOnClickListener { view22 -> deleteDialo.dismiss() }

        deleteDialo.show()

    }


    private fun setCartAdapter(list: List<Cart>) {
        val adapter  = MyCartAdapter(list)


        if(list.isEmpty())
        {
            tvSubmitCart.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
            tvClearCart.visibility = View.INVISIBLE
        }
        else
        {
            tvSubmitCart.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE

            tvClearCart.visibility = View.VISIBLE
        }

        rvMyCart.adapter = adapter
        adapter.onClicked(object: MyCartAdapter.CardInterface{


            override fun valueChanged(pos: Int, str: String) {
                list[pos].customPrice = str

                wordViewModel.updateCart(list[pos])
                adapter.notifyItemChanged(pos)

            }

            override fun clickedSelected(pos: Int, str: String) {
                if(str=="add")
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
                    }
                }
                if(str=="remove")
                {
                    if(list[pos].itemCount.toInt()<1)
                    {
                        //  Toast.makeText(ctx,"minimum limit crossed",Toast.LENGTH_SHORT).show()
                    }

                    else
                    {
                        list[pos].itemCount = (list[pos].itemCount.toInt()-1).toString()

                        wordViewModel.updateCart(list[pos])
                        adapter.notifyItemChanged(pos)
                    }
                }
                if(str=="long")
                {

                    callNumberList(list,adapter,pos)

                    Toast.makeText(ctx,"long",Toast.LENGTH_SHORT).show()
                }
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

        dialogBuilderMain = dialogBuilder.create()
        dialogBuilderMain.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBuilderMain.setCancelable(false)
        dialogBuilderMain.setCanceledOnTouchOutside(true)

        tvSubmit.setOnClickListener {

            Toast.makeText(ctx,npItem.value.toString(),Toast.LENGTH_SHORT).show()
            subList[pos].itemCount = npItem.value.toString()

            wordViewModel.updateCart(subList[pos])
            adapter2.notifyItemChanged(pos)
            dialogBuilderMain.dismiss()
        }
        npItem.maxValue = 9999
        npItem.minValue = 0

        dialogBuilderMain.show()
    }

    private fun setRetailerAdapter(list: List<Retailer>) {
        tvDistributor2.setOnClickListener {
            openDistributorShort(tvDistributor2,list)
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

                view.hint = list[position].retailer_name
                popupWindow!!.dismiss()
                Log.e("wordViewModel.allCart--",list[position].retailer_id)

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

                            view.hint = list[position].retailer_name
                            popupWindow!!.dismiss()


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

                            view.hint = list2[position].retailer_name
                            popupWindow!!.dismiss()

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



                            // listDistName.clear()
                            // listDistId.clear()

                            wordViewModel.deleteAllCart()



                            Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show()

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