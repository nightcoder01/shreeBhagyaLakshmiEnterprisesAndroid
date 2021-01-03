package pathak.creations.sbl.dashboard.ui.sales_order

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.add_sales_order.*
import org.json.JSONArray
import org.json.JSONObject
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.data_class.CategoriesData
import pathak.creations.sbl.data_class.SubCat
import pathak.creations.sbl.data_class.SubCategaryAdapter
import pathak.creations.sbl.retrofit.RetrofitResponse
import pathak.creations.sbl.retrofit.RetrofitService
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddSalesOrder : Fragment(), RetrofitResponse {

    private lateinit var addSalesOrderVM: AddSalesOrderVM
    private lateinit var ctx: Context


    var listCategories: ArrayList<CategoriesData> = ArrayList()
    var listSubCategories: ArrayList<SubCat> = ArrayList()

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
        callCategories()

        val adapter =
            ArrayAdapter.createFromResource(view.context, R.array.array_name, R.layout.spinner_item)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spBeatName.adapter = adapter
        spDistributor.adapter = adapter
        spDistributorId.adapter = adapter
        spCategory.adapter = adapter

        tvCancel.setOnClickListener { (view.context as Activity).onBackPressed() }

    }

    private fun callCategories() {
        try {

            if (CommonMethods.isNetworkAvailable(ctx)) {
                val json = JSONObject()

                RetrofitService(
                    ctx,
                    this,
                    CommonKeys.CATEGORIES ,
                    CommonKeys.CATEGORIES_CODE,
                    1
                ).callService(true, PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!)

                Log.e("callCategories", "=====$json")
                Log.e("callCategories", "=token====${PreferenceFile.retrieveKey(ctx, CommonKeys.TOKEN)!!}")

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

    private fun getArgumentedData() {

        etDistributor.text = Editable.Factory.getInstance().newEditable(arguments?.getString("distributorName"))
        etBeatName.text = Editable.Factory.getInstance().newEditable(arguments?.getString("beatName"))
        etRetailerName.text = Editable.Factory.getInstance().newEditable(arguments?.getString("retailer"))
        etRetailerId.text = Editable.Factory.getInstance().newEditable(arguments?.getString("retailerId"))
        etSalesman.text = Editable.Factory.getInstance().newEditable(arguments?.getString("salesman"))

        etDate.text =Editable.Factory.getInstance().newEditable(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
        tvDateMain.text =SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    }


    override fun response(code: Int, response: String) {
        try {
            when (code) {
                CommonKeys.CATEGORIES_CODE -> {
                    try {

                        Log.e("CATEGORIES_CODE", "=====$code==$response")

                        val json = JSONObject(response)
                        val status = json.optBoolean("status")
                        val msg = json.getString("message")
                        if (status) {

                            val data = json.getJSONArray("data")



                            listCategories.clear()

                            listCategories.add(
                                CategoriesData(
                                "Select Category",ArrayList()
                            )
                            )

                            for (i in 0 until data.length()) {

                                val dataObj = data.getJSONObject(i)
                                val arr = dataObj.getJSONArray("sub_cats")


                                listSubCategories.clear()

                                for(j in 0 until arr.length())
                                {
                                    val obj = arr.getJSONObject(j)

                                    listSubCategories.add(SubCat(obj.getString("cat"),obj.getString("catgroup"),
                                        obj.getString("code"),obj.getString("cunits"),
                                        obj.getString("description"),obj.getString("sunits")
                                    ))
                                }


                                if(dataObj.getString("main_category").isNotEmpty()) {

                                    listCategories.add(
                                        CategoriesData(
                                            dataObj.getString("main_category"),setSubList(dataObj.getJSONArray("sub_cats"))
                                        )
                                    )
                                }


                                Log.e("data in list ","======${listCategories[i]}")
                            }


                            setCategories(listCategories)
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

    private fun setCategories(listCategories: ArrayList<CategoriesData>) {

        val listShort : ArrayList<String>  = getList(listCategories)

        val adapter = ArrayAdapter<String>(ctx,R.layout.spinner_item,listShort)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spCategory.adapter = adapter

        spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ){

                if(position!=0)
                {
                    Log.e("data in list222$position ","======${listCategories[position]}")


                    val adapter2  = SubCategaryAdapter(listCategories[position].sub_cats)
                    rvSubCategories.adapter = adapter2


                    adapter2.onClicked(object: SubCategaryAdapter.CardInterface{
                        override fun clickedSelected(pos: Int, str: String) {
                            if(str=="add")
                            {
                                if(listCategories[position].sub_cats[pos].cartItem.toInt()>999)
                                {
                                    Toast.makeText(ctx,"max limit crossed",Toast.LENGTH_SHORT).show()
                                }
                                else
                                {
                                    listCategories[position].sub_cats[pos].cartItem = (listCategories[position].sub_cats[pos].cartItem.toInt()+1).toString()
                                    adapter2.notifyDataSetChanged()
                                }
                            }
                             if(str=="remove")
                            {
                                if(listCategories[position].sub_cats[pos].cartItem.toInt()<1)
                                {
                                    Toast.makeText(ctx,"minimum limit crossed",Toast.LENGTH_SHORT).show()
                                }
                                else
                                {
                                    listCategories[position].sub_cats[pos].cartItem = (listCategories[position].sub_cats[pos].cartItem.toInt()-1).toString()
                                    adapter2.notifyDataSetChanged()
                                }
                            }
                        }
                    })

                }
            }
        }

    }

    private fun getList(listCategories: ArrayList<CategoriesData>): ArrayList<String> {

        val list : ArrayList<String> = ArrayList()

        for(i in 0 until listCategories.size)
        {
            list.add(listCategories[i].main_category)
        }

        return list

    }

    private fun setSubList(arr: JSONArray): ArrayList<SubCat> {

        listSubCategories.clear()
        val list : ArrayList<SubCat> = ArrayList()


        for(i in 0 until arr.length())
        {
            val obj = arr.getJSONObject(i)

            list.add(SubCat(obj.getString("cat"),obj.getString("catgroup"),
                obj.getString("code"),obj.getString("cunits"),
                obj.getString("description"),obj.getString("sunits"),"0"
                ))
        }

        return list
    }


}