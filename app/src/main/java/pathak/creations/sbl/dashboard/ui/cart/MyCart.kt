package pathak.creations.sbl.dashboard.ui.cart

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.custom_spinner.view.*
import kotlinx.android.synthetic.main.my_cart_layout.*
import pathak.creations.sbl.AppController
import pathak.creations.sbl.R
import pathak.creations.sbl.custom_adapter.SpinnerCustomDistributorAdapter
import pathak.creations.sbl.data_classes.Cart
import pathak.creations.sbl.data_classes.Distributor
import pathak.creations.sbl.data_classes.WordViewModel
import pathak.creations.sbl.data_classes.WordViewModelFactory

class MyCart : Fragment() {

    private lateinit var myCartVM: MyCartVM

    lateinit var ctx: Context

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



        //set live data observer
        wordViewModel.allCart.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.

            dist?.let {

                setCartAdapter(it)

            }
        })

        //set live data observer
        wordViewModel.allDistributor.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.

            dist?.let {

                setDistributorAdapter(it)

            }
        })









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
        rvMyCart.adapter = adapter
        adapter.onClicked(object: MyCartAdapter.CardInterface{


            override fun valueChanged(pos: Int, str: String) {
                list[pos].customPrice = str
                adapter.notifyItemChanged(pos)                        }

            override fun clickedSelected(pos: Int, str: String) {
                if(str=="add")
                {
                    if(list[pos].itemCount.toInt()>999)
                    {
                        Toast.makeText(ctx,"max limit crossed", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        list[pos].itemCount = (list[pos].itemCount.toInt()+1).toString()
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
                        adapter.notifyItemChanged(pos)
                    }
                }
            }
        })

    }

    private fun setDistributorAdapter(list: List<Distributor>) {
        tvDistributor2.setOnClickListener {
            openDistributorShort(tvDistributor2,list)
        }
    }

    var popupWindow: PopupWindow? = null

    private fun openDistributorShort(
        view: TextView,
        list: List<Distributor>

    ) {
        val inflater =
            view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val customView = inflater.inflate(R.layout.custom_spinner, null)

        popupWindow = PopupWindow(
            customView,
            view.width,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val adapter = SpinnerCustomDistributorAdapter(list)
        customView.rvSpinner.adapter = adapter
        adapter.onClicked(object : SpinnerCustomDistributorAdapter.CardInterface {
            override fun clickedSelected(position: Int) {

                view.hint = list[position].distName
                popupWindow!!.dismiss()
                // callBeatList(listDistId[position])
               // callDistRetailer(list[position].distID)
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

                if (s.isNullOrBlank()) {
                    val adapter2 = SpinnerCustomDistributorAdapter(list)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object : SpinnerCustomDistributorAdapter.CardInterface {
                        override fun clickedSelected(position: Int) {

                            view.hint = list[position].distName
                            popupWindow!!.dismiss()
                            //callBeatList(listDistId[position])
                           // callDistRetailer(list[position].distID)

                            //  callBeatRetailer(listBeats[position].dist_id,listBeats[position].beatname)
                        }
                    })
                } else {

                    val list2: ArrayList<Distributor> = ArrayList()

                    for (i in list.indices) {
                        if (list[i].distName.toLowerCase().contains(s.toString().toLowerCase(),false)) {
                            list2.add(list[i])

                        }
                    }


                    val adapter2 = SpinnerCustomDistributorAdapter(list)
                    customView.rvSpinner.adapter = adapter2
                    adapter2.onClicked(object : SpinnerCustomDistributorAdapter.CardInterface {
                        override fun clickedSelected(position: Int) {

                            view.hint = list2[position].distName
                            popupWindow!!.dismiss()
                            // callBeatList(list2[position])
                          //  callDistRetailer(list2[position].distID)

                            // callBeatRetailer(list[position].dist_id,list[position].beatname)
                        } }) } }
        })

        popupWindow!!.isOutsideTouchable = true
        popupWindow!!.showAsDropDown(view)
        popupWindow!!.isFocusable = true
        popupWindow!!.update()

    }

    //data base work
    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory(((context as Activity).application as AppController).repository)
    }

}