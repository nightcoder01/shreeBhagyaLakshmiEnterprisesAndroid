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
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.custom_spinner.view.*
import kotlinx.android.synthetic.main.retailer_visit.*
import kotlinx.android.synthetic.main.sales_order.*
import kotlinx.android.synthetic.main.sales_order.tvBeatName2
import kotlinx.android.synthetic.main.sales_order.tvDistributor2
import pathak.creations.sbl.AppController
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.custom_adapter.SpinnerCustomAdapter
import pathak.creations.sbl.custom_adapter.SpinnerCustomCategoryAdapter
import pathak.creations.sbl.custom_adapter.SpinnerCustomDistributorAdapter
import pathak.creations.sbl.custom_adapter.SpinnerCustomRetailerAdapter
import pathak.creations.sbl.dashboard.ui.retailer_visit.RetailerVisitAdapter
import pathak.creations.sbl.data_class.SubCat
import pathak.creations.sbl.data_class.SubCategaryAdapter
import pathak.creations.sbl.data_classes.*
import pathak.creations.sbl.interfaces.DataChangeListener
import pathak.creations.sbl.interfaces.RetailerDataChangeListener
import java.text.SimpleDateFormat
import java.util.*


class SalesOrder : Fragment(),  DataChangeListener<LiveData<List<Beat>>>,
    RetailerDataChangeListener<LiveData<List<Retailer>>> {



    override fun RetailerDataChange(data: LiveData<List<Retailer>>) {
        data.observe(viewLifecycleOwner, Observer { retailer ->
            // Update the cached copy of the words in the adapter.
            Log.e("callBeatRetailer", "==33===${retailer.size}===")

            retailer?.let {
                listBeatsRetailer.addAll(it)
                setBeatRetailerAdapter(it)

            }
        })
    }


    private lateinit var salesOrderVM: SalesOrderVM
    lateinit var adapter: RetailerVisitAdapter
    private lateinit var ctx: Context



    var listBeatsRetailer : ArrayList<Retailer> = ArrayList()



    var distIDMain = ""
    var distIDName = ""

    var retailerIDMain = ""
    var retailerIDName = ""
    var phone = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        salesOrderVM =
            ViewModelProvider(this).get(SalesOrderVM::class.java)
        val root = inflater.inflate(R.layout.sales_order, container, false)
        ctx = root.context

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         distIDMain = PreferenceFile.retrieveKey(view.context,CommonKeys.SELECTED_DISTRIBUTOR)!!
         distIDName = PreferenceFile.retrieveKey(view.context,CommonKeys.SELECTED_DISTRIBUTOR)!!


        tvDateMain.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        setDistributor()

        //set live data observer
        wordViewModel.allDistributor.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.

            dist?.let {

                setDistributorAdapter(it)

            }
        })


        tvOrder.setOnClickListener {
            if(valid()){

                val bundle = bundleOf("distributorName" to tvDistributor2.text,
                    "beatName" to tvBeatName2.text,
                    "retailer" to retailerIDName,
                    "phone" to phone,
                    "retailerId" to retailerIDMain,
                    "salesman" to "",
                    "dist_id" to distIDMain
                )


                PreferenceFile.storeKey(ctx,CommonKeys.SELECTED_RETAILERNAME,retailerIDName)


                CommonMethods.hideKeyboard(tvOrder)
                Navigation.findNavController(tvOrder).navigate(R.id.action_sales_order,bundle)
            }
        }
    }

    private fun valid(): Boolean {

        if(tvBeatName2.text.isNullOrEmpty())
        {
           CommonMethods.alertDialog(ctx,"please select Beat Name.")
            return false
        }
       else if(tvRetailer2.text.isNullOrEmpty())
        {
            CommonMethods.alertDialog(ctx,"please select Retailer Name.")
            return false
        }

        return true
    }

    private fun setDistributor() {
        if(PreferenceFile.retrieveKey(ctx,CommonKeys.TYPE).equals("distributor"))
        {
            tvDistributor2.hint = PreferenceFile.retrieveKey(ctx,CommonKeys.NAME)
            callCategories(PreferenceFile.retrieveKey(ctx,CommonKeys.NAME))
            callBeatList(PreferenceFile.retrieveKey(ctx,CommonKeys.NAME))
        } }


    private fun callBeatList(distID: String?) {
        try {



            wordViewModel.getBeatFromDist(distID!!, this)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun DataChange(data: LiveData<List<Beat>>) {
        data.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.


            dist?.let {
                setBeatAdapter(it)

            }
        })
    }

    private fun callCategories(id: String?) {
        try {

            wordViewModel.allCategories.observe(viewLifecycleOwner, Observer { cat ->
                // Update the cached copy of the words in the adapter.

                cat?.let {

                    setCategories(it)
                }
            })


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun setCategories(listCategories: List<Categories>) {
       /* tvCategory2.setOnClickListener {
            openCategoryShort(tvCategory2,listCategories)
        }*/
    }

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

                view.text =listFiltered[position]
                popupWindow!!.dismiss()

                //setSubCategory

                val subList :ArrayList<SubCat> = getSubListFiltered(listFiltered[position],listCategories)



                    val adapter2  = SubCategaryAdapter(subList)
                    rvSubCategories.adapter = adapter2

                    adapter2.onClicked(object: SubCategaryAdapter.CardInterface{

                        override fun changeEditMode(pos: Int, editMode: Boolean) {

                            if (isValid()) {
                                if(subList[pos].cartItem.toInt()!=0){
                                if(subList[pos].price.toDouble()<=subList[pos].customPrice.toDouble()) {
                                    subList[pos].editMode = editMode
                                    adapter2.notifyItemChanged(pos)


                                    val dNow = Date()
                                    val ft = SimpleDateFormat("yyMMddhhmmssMs")
                                    val datetime = ft.format(dNow)

                                    Log.e("dfasdfs0","=====${subList[pos].distIDMain}")
                                    Log.e("dfasdfs0","=====${subList[pos].distIDMain}")

                                    wordViewModel.insertCart(
                                        Cart(
                                            0,
                                            subList[pos].distIDMain,
                                            subList[pos].description,
                                            subList[pos].price,
                                            subList[pos].customPrice,
                                            (subList[pos].customPrice.toFloat() * subList[pos].cartItem.toFloat()).toString(),
                                            subList[pos].cartItem,
                                            tvBeatName2.text.toString(),
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
                                    CommonMethods.alertDialog(ctx,"Count cannot be 0")

                                }
                            }
                        }

                        override fun valueChanged(pos: Int) {
                           /* subList[pos].customPrice = str
                            adapter2.notifyItemChanged(pos)*/
                            callCustomPrice(subList,adapter2,pos)

                        }

                        override fun clickedSelected(pos: Int, str: String) {
                            if(str=="add")
                            {
                                if(subList[pos].cartItem.toInt()>9999)
                                {
                                    Toast.makeText(ctx,"max limit crossed", Toast.LENGTH_SHORT).show()
                                }
                                else
                                {
                                    subList[pos].cartItem = (subList[pos].cartItem.toInt()+1).toString()
                                    adapter2.notifyItemChanged(pos)
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
                                    adapter2.notifyItemChanged(pos)
                                }
                            }
                            if(str=="long")
                            {

                                callNumberList(subList,adapter2,pos)

                               Toast.makeText(ctx,"long",Toast.LENGTH_SHORT).show()
                            }
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

                            view.text =listFiltered2[position]
                            popupWindow!!.dismiss()

                            val subList :ArrayList<SubCat> = getSubListFiltered(listFiltered2[position],listCategories)



                            val adapter3  = SubCategaryAdapter(subList)
                            rvSubCategories.adapter = adapter3

                            adapter3.onClicked(object: SubCategaryAdapter.CardInterface{


                                override fun changeEditMode(pos: Int, editMode: Boolean) {



                                    if (isValid()) {


                                        if(subList[pos].cartItem.toInt()!=0){
                                            if(subList[pos].price.toDouble()<=subList[pos].customPrice.toDouble()) {

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
                                                        (subList[pos].customPrice.toFloat() * subList[pos].cartItem.toFloat()).toString(),
                                                        subList[pos].overAllPrice,
                                                        subList[pos].cartItem,
                                                        tvBeatName2.text.toString(),
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



                                }

                                override fun valueChanged(pos: Int) {
                                   /* subList[pos].customPrice = str
                                    adapter3.notifyItemChanged(pos)*/

                                    callCustomPrice(subList,adapter3,pos)
                                }
                                override fun clickedSelected(pos: Int, str: String) {
                                    if(str=="add")
                                    {
                                        if(subList[pos].cartItem.toInt()>9999)
                                        {
                                            Toast.makeText(ctx,"max limit crossed", Toast.LENGTH_SHORT).show()
                                        }
                                        else
                                        {
                                            subList[pos].cartItem = (subList[pos].cartItem.toInt()+1).toString()
                                            adapter3.notifyItemChanged(pos)
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
                                            adapter3.notifyItemChanged(pos)
                                        }
                                    }
                                    if(str=="long")
                                    {

                                        callNumberList(subList,adapter3,pos)

                                        Toast.makeText(ctx,"long",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })



                        }
                    })
                }
                else
                {

                    val list : List<Categories> = ArrayList()



                    for(i in listCategories.indices)
                    {
                        if(listCategories[i].catgroup.toString().toLowerCase().contains(s.toString().toLowerCase(),false))
                        {
                            list.plus(listCategories[i])

                        }
                    }

                    val listFiltered2 : ArrayList<String> = getListFiltered(list)

                    val adapter2 = SpinnerCustomCategoryAdapter(listFiltered2)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomCategoryAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =listFiltered2[position]
                            popupWindow!!.dismiss()


                            val subList :ArrayList<SubCat> = getSubListFiltered(listFiltered2[position],list)


                            val adapter3  = SubCategaryAdapter(subList)
                            rvSubCategories.adapter = adapter3

                            adapter3.onClicked(object: SubCategaryAdapter.CardInterface{

                                override fun changeEditMode(pos: Int, editMode: Boolean) {



                                    if (isValid()) {

                                        if(subList[pos].cartItem.toInt()!=0){
                                            if(subList[pos].price.toDouble()<=subList[pos].customPrice.toDouble()) {

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
                                                        tvBeatName2.text.toString(),
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


                                }

                                override fun valueChanged(pos: Int) {
                                   /* subList[pos].customPrice = str
                                    adapter3.notifyItemChanged(pos)
*/
                                    callCustomPrice(subList,adapter3,pos)
                                }

                                override fun clickedSelected(pos: Int, str: String) {
                                    if(str=="add")
                                    {
                                        if(subList[pos].cartItem.toInt()>9999)
                                        {
                                            Toast.makeText(ctx,"max limit crossed", Toast.LENGTH_SHORT).show()
                                        }
                                        else
                                        {
                                            subList[pos].cartItem = (subList[pos].cartItem.toInt()+1).toString()
                                            adapter3.notifyItemChanged(pos)

                                        }
                                    }
                                    if(str=="remove")
                                    {
                                        if(subList[pos].cartItem.toInt()<1)
                                        {
                                           // Toast.makeText(ctx,"minimum limit crossed",Toast.LENGTH_SHORT).show()
                                        }

                                         else
                                        {
                                            subList[pos].cartItem = (subList[pos].cartItem.toInt()-1).toString()
                                            adapter3.notifyItemChanged(pos)
                                        }
                                    }
                                    if(str=="long")
                                    {

                                        callNumberList(subList,adapter3,pos)

                                        Toast.makeText(ctx,"long",Toast.LENGTH_SHORT).show()
                                    }
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


    private lateinit var dialogBuilderMain  : AlertDialog

    private fun callNumberList(
        subList: ArrayList<SubCat>,
        adapter2: SubCategaryAdapter,
        pos: Int
    ) {


        val dialogBuilder = AlertDialog.Builder(ctx)
        val layout = AlertDialogLayout.inflate(ctx, R.layout.custom_count,null)
        dialogBuilder.setView(layout)

        val tvSubmit :TextView= layout.findViewById(R.id.tvSubmit)
        val npItem :NumberPicker= layout.findViewById(R.id.npItem)
        val tvCancel :NumberPicker= layout.findViewById(R.id.tvCancel)

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
                        else {


                subList[pos].cartItem = etEnterPrice.text.toString()
                adapter2.notifyItemChanged(pos)
                dialogBuilderMain.dismiss()

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

        val etEnterPrice : EditText = layout.findViewById(R.id.etEnterPrice)


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


    private fun isValid(): Boolean {

        return when {
            tvBeatName2.text.isNullOrBlank() -> {
                CommonMethods.alertDialog(ctx,"Please select Beat Name")
                false
            }
            tvRetailer2.text.isNullOrBlank() -> {
                CommonMethods.alertDialog(ctx,"Please select Retailer Name")
                false
            }
            else -> true
        }

    }

    private fun getSubListFiltered(s: String, listCategories: List<Categories>): ArrayList<SubCat> {


        val list :ArrayList<SubCat>  = ArrayList()

        for(i in listCategories.indices)
        {
            if(listCategories[i].catgroup==s)
            {
            list.add(SubCat(listCategories[i].catgroup,listCategories[i].catgroup,
                listCategories[i].code,listCategories[i].description,
                listCategories[i].price,listCategories[i].weight,
                listCategories[i].ptrflag,"0",
                String.format("%.2f", (listCategories[i].price.toFloat()+(listCategories[i].price.toFloat()*(45))/1000 )) ,
                "0.0",distIDMain,distIDName,retailerIDMain,retailerIDName
                ))
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
        return list

    }

    private fun setDistributorAdapter(listDist: List<Distributor>) {

        tvDistributor2.text =PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_DISTRIBUTOR_NAME)
        callBeatList(PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_DISTRIBUTOR))
        callCategories(PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_DISTRIBUTOR))

        /*tvDistributor2.setOnClickListener {
            openDistributorShort(tvDistributor2,listDist)
        }*/

    }

    private fun openDistributorShort(
        view: TextView,
        listDist: List<Distributor>) {
        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.custom_spinner, null)



        popupWindow = PopupWindow(
            customView,
            view.width,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val adapter = SpinnerCustomDistributorAdapter(listDist)
        customView.rvSpinner.adapter = adapter
        adapter.onClicked(object :SpinnerCustomDistributorAdapter.CardInterface{
            override fun clickedSelected(position: Int) {

                view.text =listDist[position].distName
                popupWindow!!.dismiss()
                distIDMain = listDist[position].distID
                distIDName = listDist[position].distName
                callBeatList(listDist[position].distID)
                callCategories(listDist[position].distID)

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
                    val adapter2 = SpinnerCustomDistributorAdapter(listDist)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomDistributorAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =listDist[position].distName
                            popupWindow!!.dismiss()
                            distIDMain = listDist[position].distID
                            distIDName = listDist[position].distName

                            callBeatList(listDist[position].distID)
                            callCategories(listDist[position].distID)

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

                            distIDMain = listDist[position].distID
                            distIDName = listDist[position].distName

                            callBeatList(list[position].distID)
                            callCategories(list[position].distID)

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

    private fun setBeatRetailerAdapter(listBeatsRetailer: List<Retailer>) {
        tvRetailer2.setOnClickListener {
            openRetailerSpinner(tvRetailer2,listBeatsRetailer)
        }

        if(PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_RETAILERNAME)!=null)
        {

           for(i in 0 until listBeatsRetailer.size)
           {
               if(listBeatsRetailer[i].retailer_name==PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_RETAILERNAME))
               {
                   tvRetailer2.text =listBeatsRetailer[i].retailer_name
                   retailerIDMain =listBeatsRetailer[i].retailer_id
                   retailerIDName =listBeatsRetailer[i].retailer_name
                   phone =listBeatsRetailer[i].mobile
               }
           }




          //  callBeatRetailer(listBeats[0].dist_id,PreferenceFile.retrieveKey(ctx!!,CommonKeys.SELECTED_BEATNAME)!!)
        }

    }

    private fun openRetailerSpinner(view: TextView, listBeatsRetailer: List<Retailer>) {
        val inflater = view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.custom_spinner, null)

        popupWindow = PopupWindow(
            customView,
            view.width,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val adapter = SpinnerCustomRetailerAdapter(listBeatsRetailer)
        customView.rvSpinner.adapter = adapter
        adapter.onClicked(object :SpinnerCustomRetailerAdapter.CardInterface{
            override fun clickedSelected(position: Int) {

                view.text =listBeatsRetailer[position].retailer_name
                popupWindow!!.dismiss()

                retailerIDMain =listBeatsRetailer[position].retailer_id
                retailerIDName =listBeatsRetailer[position].retailer_name
                phone =listBeatsRetailer[position].mobile

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
                    val adapter2 = SpinnerCustomRetailerAdapter(listBeatsRetailer)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomRetailerAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =listBeatsRetailer[position].retailer_name
                            popupWindow!!.dismiss()
                            retailerIDMain =listBeatsRetailer[position].retailer_id
                            retailerIDName =listBeatsRetailer[position].retailer_name
                            phone =listBeatsRetailer[position].mobile

                        }


                    })
                }
                else
                {

                    val list : ArrayList<Retailer> = ArrayList()

                    for(i in listBeatsRetailer.indices)
                    {
                        if(listBeatsRetailer[i].retailer_name.toLowerCase().contains(s.toString().toLowerCase(),false))
                        {
                            list.add(listBeatsRetailer[i])

                        }
                    }


                    val adapter2 = SpinnerCustomRetailerAdapter(list)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object :SpinnerCustomRetailerAdapter.CardInterface{
                        override fun clickedSelected(position: Int) {

                            view.text =list[position].retailer_name
                            popupWindow!!.dismiss()

                            retailerIDMain =list[position].retailer_id
                            retailerIDName =list[position].retailer_name
                            phone =list[position].mobile

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

    private fun setBeatAdapter(listBeats: List<Beat>) {
        tvBeatName2.setOnClickListener {
            openPopShortBy(tvBeatName2,listBeats)
        }
        if(PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_BEATNAME)!=null)
        {
            tvBeatName2.text =PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_BEATNAME)

            callBeatRetailer(listBeats[0].dist_id,PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_BEATNAME)!!)
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
                callBeatRetailer(listBeats[position].dist_id,listBeats[position].beatname)
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
                            callBeatRetailer(listBeats[position].dist_id,listBeats[position].beatname)
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
                            callBeatRetailer(list[position].dist_id,list[position].beatname)
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

    private fun callBeatRetailer(distId: String, beatname: String) {
        try {


            Log.e("callBeatRetailer", "=====$distId===$beatname")
            wordViewModel.getBeatRetailer(beatname, this)
            PreferenceFile.storeKey(ctx,CommonKeys.SELECTED_BEATNAME,beatname)



        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //data base work
    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory(((context as Activity).application as AppController).repository)
    }


}