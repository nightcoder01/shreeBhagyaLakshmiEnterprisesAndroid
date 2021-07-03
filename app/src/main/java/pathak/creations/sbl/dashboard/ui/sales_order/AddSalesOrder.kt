package pathak.creations.sbl.dashboard.ui.sales_order

import android.app.Activity
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AlertDialogLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.add_sales_order.*
import kotlinx.android.synthetic.main.custom_spinner.view.*
import pathak.creations.sbl.AppController
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.custom_adapter.SpinnerCustomCategoryAdapter
import pathak.creations.sbl.data_class.SubCat
import pathak.creations.sbl.data_class.SubCategaryAdapter
import pathak.creations.sbl.data_classes.Cart
import pathak.creations.sbl.data_classes.Categories
import pathak.creations.sbl.data_classes.WordViewModel
import pathak.creations.sbl.data_classes.WordViewModelFactory
import java.text.SimpleDateFormat
import java.util.*


class AddSalesOrder : Fragment() {

    private lateinit var addSalesOrderVM: AddSalesOrderVM
    private lateinit var ctx: Context


    var dist_id: String =""
    var distIDName = ""

    var retailerIDMain = ""
    var phone = ""
    var retailerIDName = ""

    var subList :ArrayList<SubCat> = ArrayList()
    var subFilterList :ArrayList<SubCat> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addSalesOrderVM =
            ViewModelProvider(this).get(AddSalesOrderVM::class.java)
        val root = inflater.inflate(R.layout.add_sales_order, container, false)

        ctx = root.context

        val textView: TextView = root.findViewById(R.id.text_send)
        addSalesOrderVM.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getArgumentedData()


        val adapter = ArrayAdapter.createFromResource(view.context, R.array.array_name, R.layout.spinner_item)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spBeatName.adapter = adapter
        spDistributor.adapter = adapter
        spDistributorId.adapter = adapter



        tvCancel.setOnClickListener { (view.context as Activity).onBackPressed() }
        clCart.setOnClickListener { addCart() }
        setSearch()
    }


    private fun setSearch() {
        etSearch.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                if(s.isNullOrEmpty())
                {
                    subFilterList.clear()
                    subFilterList.addAll(subList)
                }
                else
                {
                    Log.e("=============","==========$s++++++")
                    subFilterList.clear()
                    subFilterList.addAll(subList.filter
                    { it.description.toLowerCase().contains(s.toString().toLowerCase()) })
                }
                    val adapter2  = SubCategaryAdapter(subFilterList)
                    rvSubCategories.adapter = adapter2

                    adapter2.onClicked(object: SubCategaryAdapter.CardInterface{
                        override fun changeEditMode(pos: Int, editMode: Boolean) {

                            if(subFilterList[pos].cartItem.toInt()>0) {
                                if(subFilterList[pos].price.toDouble()<=subFilterList[pos].customPrice.toDouble()) {

                                    subFilterList[pos].editMode = editMode
                                    adapter2.notifyItemChanged(pos)


                                    Log.e("dfdsafasfa","======${subFilterList[pos]}")

                                    val dNow = Date()
                                    val ft = SimpleDateFormat("yyMMddhhmmssMs")
                                    val datetime = ft.format(dNow)
                                    wordViewModel.insertCart(
                                        Cart(
                                            0,
                                            subFilterList[pos].distIDMain,
                                            subFilterList[pos].description,
                                            subFilterList[pos].price,
                                            subFilterList[pos].customPrice,
                                            (subFilterList[pos].customPrice.toFloat() * subFilterList[pos].cartItem.toFloat()).toString(),
                                            subFilterList[pos].cartItem,
                                            etBeatName.text.toString(),
                                            subFilterList[pos].retailerIDName,
                                            subFilterList[pos].retailerIDMain,
                                            subFilterList[pos].distIDName,
                                            subFilterList[pos].catgroup,
                                            subFilterList[pos].category,
                                            subFilterList[pos].code,
                                            subFilterList[pos].customPrice,
                                            subFilterList[pos].price,
                                            (subFilterList[pos].customPrice.toFloat() * subFilterList[pos].cartItem.toFloat()).toString(),
                                            (subFilterList[pos].price.toFloat()*subFilterList[pos].cartItem.toFloat()).toString()

                                        )
                                    )

                                    Toast.makeText(
                                        rvSubCategories.context,
                                        "Item Added to Cart Successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else
                                {
                                    CommonMethods.alertDialog(ctx,"Ptd price cannot be less than Ptr price")

                                }}
                            else
                            {
                                CommonMethods.alertDialog(
                                    ctx,
                                    "Count cannot be 0"
                                )
                            }


                        }
                        override fun valueChanged(pos: Int) {

                            callCustomPrice(subFilterList,adapter2,pos)

                        }

                        override fun clickedSelected(pos: Int, str: String) {

                            Log.e("adfdsfasfa","======$pos========")
                            Log.e("adfdsfasfa","======${subFilterList.size}========")
                            setCart()
                           // callNumberList(adapter2,pos,subFilterList)

                            /*if(str=="add")
                            {
                                if(subFilterList[pos].cartItem.toInt()>9999)
                                {
                                    Toast.makeText(ctx,"max limit crossed", Toast.LENGTH_SHORT).show()
                                }
                                else
                                {

                                    subFilterList[pos].cartItem = (subFilterList[pos].cartItem.toInt()+1).toString()
                                    adapter2.notifyDataSetChanged()
                                    setCart()

                                }
                            }
                            if(str=="remove")
                            {
                                if(subFilterList[pos].cartItem.toInt()<1)
                                {
                                    //  Toast.makeText(ctx,"minimum limit crossed",Toast.LENGTH_SHORT).show()
                                }

                                else
                                {
                                    subFilterList[pos].cartItem = (subFilterList[pos].cartItem.toInt()-1).toString()
                                    adapter2.notifyDataSetChanged()
                                    setCart()
                                }
                            }
                            if(str=="long")
                            {

                                callNumberList(adapter2,pos)

                               /// Toast.makeText(ctx,"long",Toast.LENGTH_SHORT).show()
                            }*/
                        }
                    })


            }
        })
    }


    private fun addCart() {
        for(pos in 0 until subList.size)
        {

            if(subList[pos].cartItem != "0") {

                Log.e("dfohsdfhapbnf ","===========${subList[pos].cartItem}")

                val dNow = Date()
                val ft = SimpleDateFormat("yyMMddhhmmssMs")
                val datetime = ft.format(dNow)
                wordViewModel.insertCart(
                    Cart(0,
                        subList[pos].distIDMain,
                        subList[pos].description,
                        subList[pos].price,
                        subList[pos].customPrice,
                        (subList[pos].customPrice.toFloat() * subList[pos].cartItem.toFloat()).toString(),
                        subList[pos].cartItem,
                        etBeatName.text.toString(),
                        subList[pos].retailerIDName,
                        subList[pos].retailerIDMain,
                        subList[pos].distIDName,
                        subList[pos].catgroup,
                        subList[pos].category,
                        subList[pos].code,
                        subList[pos].customPrice,
                        subList[pos].price,
                         (subList[pos].customPrice.toFloat() * subList[pos].cartItem.toFloat()).toString(),
                        (subList[pos].price.toFloat() * subList[pos].cartItem.toFloat()).toString()

                    )
                )
            }
        }
        Toast.makeText(
            rvSubCategories.context,
            "Item Added to Cart Successfully.",
            Toast.LENGTH_SHORT
        ).show()


        val bundle = bundleOf(
            "beatName" to etBeatName.text.toString(),
            "retailer" to etRetailerName.text.toString(),
            "retailerId" to retailerIDMain
        )

        Log.e("gdfgdfsgsdfgdsfg","=====${etBeatName.text}===${etRetailerName.text}===${retailerIDMain}")

        Navigation.findNavController(clCart).navigate(R.id.action_addCart,bundle)


    }


    private fun getArgumentedData() {

        etDistributor.text = Editable.Factory.getInstance().newEditable(arguments?.getString("distributorName"))
        etBeatName.text = Editable.Factory.getInstance().newEditable(arguments?.getString("beatName"))
        etRetailerName.text = Editable.Factory.getInstance().newEditable(arguments?.getString("retailer"))
        etRetailerId.text = Editable.Factory.getInstance().newEditable(arguments?.getString("retailerId"))
        etPhone.text = Editable.Factory.getInstance().newEditable(arguments?.getString("phone"))

        dist_id = arguments?.getString("dist_id")!!
        distIDName = arguments?.getString("distributorName")!!
        retailerIDMain = arguments?.getString("retailerId")!!
        phone = arguments?.getString("phone")!!
        retailerIDName = arguments?.getString("retailer")!!

        etDate.text =Editable.Factory.getInstance().newEditable(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
        tvDateMain.text =SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        wordViewModel.allCategories.observe(viewLifecycleOwner, Observer { cat ->
            // Update the cached copy of the words in the adapter.

            cat?.let {

                setCategories(it)
            }
        })

    }

    private fun setCategories(listCategories: List<Categories>) {
        tvCategory2.setOnClickListener {
            openCategoryShort(tvCategory2,listCategories)
        }
    }

    var popupWindow: PopupWindow? = null


    private fun openCategoryShort(view: TextView, listCategories: List<Categories>)
    {
        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.custom_spinner, null)



        popupWindow = PopupWindow(
            customView,
            view.width,
            WindowManager.LayoutParams.WRAP_CONTENT
        )


        val listFiltered : ArrayList<String> = getListFiltered(listCategories)

        val adapter = SpinnerCustomCategoryAdapter(listFiltered)
        customView.rvSpinner.adapter = adapter
        adapter.onClicked(object :SpinnerCustomCategoryAdapter.CardInterface{
            override fun clickedSelected(position: Int) {


                ////////clickeddddddd

                view.text =listFiltered[position]
                popupWindow!!.dismiss()

                //setSubCategory
                subList.clear()
                 subList = getSubListFiltered(listFiltered[position],listCategories,view.text.toString())

                val adapter2  = SubCategaryAdapter(subList)
                rvSubCategories.adapter = adapter2

                adapter2.onClicked(object: SubCategaryAdapter.CardInterface{
                    override fun changeEditMode(pos: Int, editMode: Boolean) {

                        if(subList[pos].cartItem.toInt()>0) {
                            if(subList[pos].price.toDouble()<=subList[pos].customPrice.toDouble()) {

                                subList[pos].editMode = editMode
                                adapter2.notifyItemChanged(pos)


                                Log.e("dfdsafasfa","======${subList[pos]}")

                                val dNow = Date()
                                val ft = SimpleDateFormat("yyMMddhhmmssMs")
                                val datetime = ft.format(dNow)
                                wordViewModel.insertCart(
                                    Cart(
                                        0,
                                        subList[pos].distIDMain,
                                        subList[pos].description,
                                        subList[pos].price,
                                        subList[pos].customPrice,
                                        (subList[pos].customPrice.toFloat() * subList[pos].cartItem.toFloat()).toString(),
                                        subList[pos].cartItem,
                                        etBeatName.text.toString(),
                                        subList[pos].retailerIDName,
                                        subList[pos].retailerIDMain,
                                        subList[pos].distIDName,
                                        subList[pos].catgroup,
                                        subList[pos].category,
                                        subList[pos].code,
                                        subList[pos].customPrice,
                                        subList[pos].price,
                                        (subList[pos].customPrice.toFloat() * subList[pos].cartItem.toFloat()).toString(),
                                        (subList[pos].price.toFloat()*subList[pos].cartItem.toFloat()).toString()

                                    )
                                )

                                Toast.makeText(
                                    rvSubCategories.context,
                                    "Item Added to Cart Successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else
                            {
                                CommonMethods.alertDialog(ctx,"Ptd price cannot be less than Ptr price")

                            }}
                        else
                        {
                            CommonMethods.alertDialog(
                                ctx,
                                "Count cannot be 0"
                            )
                        }


                    }
                    override fun valueChanged(pos: Int) {

                        callCustomPrice(subList,adapter2,pos)

                    }

                    override fun clickedSelected(pos: Int, str: String) {

                        setCart()
                       //  (adapter2,pos,subList)

                       /* if(str=="add")
                        {
                            if(subList[pos].cartItem.toInt()>9999)
                            {
                                Toast.makeText(ctx,"max limit crossed", Toast.LENGTH_SHORT).show()
                            }
                            else
                            {

                                subList[pos].cartItem = (subList[pos].cartItem.toInt()+1).toString()
                                adapter2.notifyDataSetChanged()
                                setCart()

                            }
                        }
                        if(str=="remove")
                        {
                            if(subList[pos].cartItem.toInt()<1)
                            {
                                //  Toast.makeText(ctx,"minimum limit crossed",Toast.LENGTH_SHORT).show()
                            }

                            else
                            {
                                subList[pos].cartItem = (subList[pos].cartItem.toInt()-1).toString()
                                adapter2.notifyDataSetChanged()
                                setCart()
                            }
                        }
                        if(str=="long")
                        {

                            callNumberList(adapter2,pos)

                            Toast.makeText(ctx,"long",Toast.LENGTH_SHORT).show()
                        }*/
                    }
                })
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
                    val listFiltered2 : ArrayList<String> = getListFiltered(listCategories)

                    val adapter2 = SpinnerCustomCategoryAdapter(listFiltered2)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomCategoryAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {



                            ///////////clicked

                            view.text =listFiltered2[position]
                            popupWindow!!.dismiss()

                            subList.clear()
                             subList  =  getSubListFiltered(listFiltered2[position],listCategories,view.text.toString())

                            val adapter3  = SubCategaryAdapter(subList)
                            rvSubCategories.adapter = adapter3

                            adapter3.onClicked(object: SubCategaryAdapter.CardInterface{

                                override fun changeEditMode(pos: Int, editMode: Boolean) {

                                    if(subList[pos].cartItem.toInt()!=0) {

                                        if (subList[pos].price.toDouble() <= subList[pos].customPrice.toDouble()) {

                                            subList[pos].editMode = editMode
                                            adapter3.notifyItemChanged(pos)

                                            val dNow = Date()
                                            val ft = SimpleDateFormat("yyMMddhhmmssMs")
                                            val datetime = ft.format(dNow)
                                            wordViewModel.insertCart(
                                                Cart(
                                                    0,
                                                    subList[pos].distIDMain,
                                                    subList[pos].description,
                                                    subList[pos].price,
                                                    subList[pos].customPrice,
                                                    (subList[pos].customPrice.toFloat() * subList[pos].cartItem.toFloat()).toString(),
                                                    subList[pos].cartItem,
                                                    etBeatName.text.toString(),
                                                    subList[pos].retailerIDName,
                                                    subList[pos].retailerIDMain,
                                                    subList[pos].distIDName,
                                                    subList[pos].catgroup,
                                                    subList[pos].category,
                                                    subList[pos].code,
                                                    subList[pos].customPrice,
                                                    subList[pos].price,
                                                    (subList[pos].customPrice.toFloat() * subList[pos].cartItem.toFloat()).toString(),
                                                    (subList[pos].price.toFloat()*subList[pos].cartItem.toFloat()).toString()

                                                )
                                            )

                                            Toast.makeText(
                                                rvSubCategories.context,
                                                "Item Added to Cart Successfully.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            CommonMethods.alertDialog(
                                                ctx,
                                                "Ptd price cannot be less than Ptr price"
                                            )

                                        }
                                    }
                                    else
                                    {
                                        CommonMethods.alertDialog(
                                            ctx,
                                            "Count cannot be 0"
                                        )
                                    }
                                }

                                override fun valueChanged(pos: Int) {
                                  //  subList[pos].customPrice = str
                                   // adapter3.notifyDataSetChanged()
                                    callCustomPrice(subList,adapter3,pos)

                                }

                                override fun clickedSelected(pos: Int, str: String) {
                                    setCart()
                                   // callNumberList(adapter3,pos,subList)

                                    /*if(str=="add")
                                    {
                                        if(subList[pos].cartItem.toInt()>9999)
                                        {
                                            Toast.makeText(ctx,"max limit crossed", Toast.LENGTH_SHORT).show()
                                        }
                                        else
                                        {
                                            subList[pos].cartItem = (subList[pos].cartItem.toInt()+1).toString()
                                            adapter3.notifyDataSetChanged()
                                            setCart()
                                        }
                                    }
                                    if(str=="remove")
                                    {
                                        if(subList[pos].cartItem.toInt()<1)
                                        {
                                            //  Toast.makeText(ctx,"minimum limit crossed",Toast.LENGTH_SHORT).show()
                                        }

                                        else
                                        {
                                            subList[pos].cartItem = (subList[pos].cartItem.toInt()-1).toString()
                                            adapter3.notifyDataSetChanged()
                                            setCart()
                                        }
                                    }
                                    if(str=="long")
                                    {

                                        callNumberList(adapter3,pos)

                                        Toast.makeText(ctx,"long",Toast.LENGTH_SHORT).show()
                                    }*/
                                }
                            })

                        }
                    })
                }
                else
                {
                    val listFiltered2 : ArrayList<String> = ArrayList()

                    for(i in 0 until listFiltered.size)
                    {

                        if(listFiltered[i].toString().toLowerCase().contains(s.toString().toLowerCase(),false))
                        {
                            listFiltered2.add(listFiltered[i])
                        }
                    }

                    val adapter2 = SpinnerCustomCategoryAdapter(listFiltered2)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomCategoryAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =listFiltered2[position]
                            popupWindow!!.dismiss()

                            subList.clear()
                             subList  = getSubListFiltered(listFiltered2[position],listCategories,view.text.toString())


                            val adapter3  = SubCategaryAdapter(subList)
                            rvSubCategories.adapter = adapter3

                            adapter3.onClicked(object: SubCategaryAdapter.CardInterface{

                                override fun changeEditMode(pos: Int, editMode: Boolean) {

                                    if(subList[pos].cartItem.toInt()!=0) {
                                        if (subList[pos].price.toDouble() <= subList[pos].customPrice.toDouble()) {

                                            subList[pos].editMode = editMode
                                            adapter3.notifyItemChanged(pos)

                                            val dNow = Date()
                                            val ft = SimpleDateFormat("yyMMddhhmmssMs")
                                            val datetime = ft.format(dNow)


                                            wordViewModel.insertCart(
                                                Cart(
                                                    0,
                                                    subList[pos].distIDMain,
                                                    subList[pos].description,
                                                    subList[pos].price,
                                                    subList[pos].customPrice,
                                                    (subList[pos].customPrice.toFloat() * subList[pos].cartItem.toFloat()).toString(),
                                                    subList[pos].cartItem,
                                                    etBeatName.text.toString(),
                                                    subList[pos].retailerIDName,
                                                    subList[pos].retailerIDMain,
                                                    subList[pos].distIDName,
                                                    subList[pos].catgroup,
                                                    subList[pos].category,
                                                    subList[pos].code,
                                                    subList[pos].customPrice,
                                                    subList[pos].price,
                                                    (subList[pos].customPrice.toFloat() * subList[pos].cartItem.toFloat()).toString(),
                                                    (subList[pos].price.toFloat()*subList[pos].cartItem.toFloat()).toString()

                                                )
                                            )

                                            Toast.makeText(
                                                rvSubCategories.context,
                                                "Item Added to Cart Successfully.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            CommonMethods.alertDialog(
                                                ctx,
                                                "Ptd price cannot be less than Ptr price"
                                            )

                                        }
                                    }
                                    else
                                    {
                                        CommonMethods.alertDialog(
                                            ctx,
                                            "Count cannot be 0"
                                        )
                                    }
                                }


                                override fun valueChanged(pos: Int) {
                                    //
                                  //  subList[pos].customPrice = str
                                   // adapter2.notifyDataSetChanged()
                                    callCustomPrice(subList,adapter3,pos)
                                }

                                override fun clickedSelected(pos: Int, str: String) {
                                    setCart()
                                   // callNumberList(adapter3,pos,subList)

                                   /* if(str=="add")
                                    {
                                        if(subList[pos].cartItem.toInt()>9999)
                                        {
                                            Toast.makeText(ctx,"max limit crossed", Toast.LENGTH_SHORT).show()
                                        }
                                        else
                                        {
                                            subList[pos].cartItem = (subList[pos].cartItem.toInt()+1).toString()
                                            adapter3.notifyDataSetChanged()
                                            setCart()
                                        }
                                    }
                                    if(str=="remove")
                                    {
                                        if (subList[pos].cartItem.toInt() >= 1) {
                                            subList[pos].cartItem = (subList[pos].cartItem.toInt()-1).toString()
                                            adapter3.notifyDataSetChanged()
                                            setCart()
                                        }
                                    }
                                    if(str=="long")
                                    {

                                        callNumberList(adapter3,pos)

                                        Toast.makeText(ctx,"long",Toast.LENGTH_SHORT).show()
                                    }*/
                                }
                            })
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

    private fun setCart() {

        var count =0
        var price =""
        for(i in 0 until subList.size)
        {
            if (subList[i].cartItem.toInt() > 0)
            {
                count += 1

                price = if(price.isNotEmpty())
                {  String.format("%.2f",price.toFloat()+(subList[i].customPrice.toFloat()* subList[i].cartItem.toFloat()))}
                else{String.format("%.2f",(subList[i].customPrice.toFloat()* subList[i].cartItem.toFloat()))}

            }
        }
        tvCountValue.text = count.toString()
        tvTotalValue.text = "₹ $price"

        if(count!=0)  clCart.visibility = View.VISIBLE else  clCart.visibility = View.GONE


    }


    private lateinit var dialogBuilderMain  : AlertDialog

    private fun callNumberList(
        adapter2: SubCategaryAdapter,
        pos: Int,
        list: ArrayList<SubCat>
    ) {

        val dialogBuilder = AlertDialog.Builder(ctx)
        val layout = AlertDialogLayout.inflate(ctx, R.layout.custom_count,null)
        dialogBuilder.setView(layout)

        val tvSubmit :TextView= layout.findViewById(R.id.tvSubmit)
        val tvCancel :TextView= layout.findViewById(R.id.tvCancel)
        val npItem : NumberPicker = layout.findViewById(R.id.npItem)
        val etEnterPrice : EditText = layout.findViewById(R.id.etEnterPrice)

        dialogBuilderMain = dialogBuilder.create()
        dialogBuilderMain.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBuilderMain.setCancelable(false)
        dialogBuilderMain.setCanceledOnTouchOutside(true)

        tvSubmit.setOnClickListener {


            if(etEnterPrice.text.toString().isEmpty())
            {
                Toast.makeText(ctx,"Item count is empty.",Toast.LENGTH_SHORT).show()
            }
            else
            {
                list[pos].cartItem = etEnterPrice.text.toString()
                adapter2.notifyItemChanged(pos)
                dialogBuilderMain.dismiss()
                setCart()
            }

        }
        tvCancel.setOnClickListener {

                dialogBuilderMain.dismiss()
        }
        npItem.maxValue = 9999
        npItem.minValue = 0

        dialogBuilderMain.show()
    }

    private fun callCustomPrice(
        subList: ArrayList<SubCat>,
        adapter2: SubCategaryAdapter,
        pos: Int
    ) {

        val dialogBuilder = AlertDialog.Builder(ctx)
        val layout = AlertDialogLayout.inflate(ctx, R.layout.custom_price,null)
        dialogBuilder.setView(layout)

        val tvSubmit :TextView= layout.findViewById(R.id.tvSubmit)
        val tvCancel :TextView= layout.findViewById(R.id.tvCancel)
        val etEnterPrice :EditText= layout.findViewById(R.id.etEnterPrice)

        etEnterPrice.text =   Editable.Factory.getInstance().newEditable(subList[pos].customPrice)

        dialogBuilderMain = dialogBuilder.create()
        dialogBuilderMain.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogBuilderMain.setCancelable(false)
        dialogBuilderMain.setCanceledOnTouchOutside(true)

        tvSubmit.setOnClickListener {

            if(subList[pos].price.toDouble() <= etEnterPrice.text.toString().toDouble())
            {
                subList[pos].customPrice = etEnterPrice.text.toString()
                adapter2.notifyDataSetChanged()
                dialogBuilderMain.dismiss()
                setCart()
            }
                else
            {
                CommonMethods.alertDialog(
                    ctx,
                    "Ptd price cannot be less than Ptr price"
                )
            }


            //dialogBuilderMain.dismiss()
        }
        tvCancel.setOnClickListener {
            dialogBuilderMain.dismiss()
        }

        dialogBuilderMain.show()
    }

    private fun getSubListFiltered(s: String, listCategories: List<Categories>,all: String): ArrayList<SubCat> {

        val list :ArrayList<SubCat>  = ArrayList()

        for(i in listCategories.indices)
        {
            if(all!="All") {
                if (listCategories[i].catgroup == s) {
                    list.add(
                        SubCat(
                            listCategories[i].catgroup, listCategories[i].catgroup,
                            listCategories[i].code, listCategories[i].description,
                            listCategories[i].price, listCategories[i].weight,
                            listCategories[i].ptrflag, "0",
                            String.format(
                                "%.2f",
                                (listCategories[i].price.toFloat() + (listCategories[i].price.toFloat() * (45)) / 1000)
                            ),
                            "0.0", dist_id, distIDName, retailerIDMain, retailerIDName
                        )
                    )
                }
            }
            else
            {
                list.add(
                    SubCat(
                        listCategories[i].catgroup, listCategories[i].catgroup,
                        listCategories[i].code, listCategories[i].description,
                        listCategories[i].price, listCategories[i].weight,
                        listCategories[i].ptrflag, "0",
                        String.format(
                            "%.2f",
                            (listCategories[i].price.toFloat() + (listCategories[i].price.toFloat() * (45)) / 1000)
                        ),
                        "0.0", dist_id, distIDName, retailerIDMain, retailerIDName
                    )
                )
            }

        }
        return list

    }

    private fun getListFiltered(listCategories: List<Categories>): ArrayList<String> {

        var list :ArrayList<String> = ArrayList()


        for(element in listCategories)
        {
            list.add(element.catgroup)
        }

        if(list.isNotEmpty()) {
            list = list.distinct() as ArrayList<String>
        }

        list.sort()
        list.add(0,"All")


        return list

    }

    //data base work
    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory(((context as Activity).application as AppController).repository)
    }

}