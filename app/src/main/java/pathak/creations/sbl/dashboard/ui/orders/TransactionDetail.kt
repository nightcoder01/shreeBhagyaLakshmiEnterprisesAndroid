package pathak.creations.sbl.dashboard.ui.orders

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import kotlinx.android.synthetic.main.transaction_detail.*
import pathak.creations.sbl.AppController
import pathak.creations.sbl.R
import pathak.creations.sbl.data_classes.Orders
import pathak.creations.sbl.data_classes.WordViewModel
import pathak.creations.sbl.data_classes.WordViewModelFactory
import pathak.creations.sbl.interfaces.OrderDataChangeListener
import java.text.SimpleDateFormat
import java.util.*

class TransactionDetail : Fragment(), OrderDataChangeListener<LiveData<List<Orders>>> {

    lateinit var ctx: Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ctx = container!!.context
        return inflater.inflate(R.layout.transaction_detail, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments!=null) {

            tvDistributorNameValue.text = arguments?.getString("distributorName")
            tvDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            tvRetailerNameValue.text = arguments?.getString("retailerName")
            tvTransNoValue.text = arguments?.getString("transactionNo")
            tvTotalValue.text = arguments?.getString("total")
            tvGrandTotalValue.text = arguments?.getString("total")

            wordViewModel.getOrdersFromTransaction(tvTransNoValue.text.toString(),this@TransactionDetail)
        }


    }
    var listOrders: ArrayList<Orders> = ArrayList()

    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory(((ctx as Activity).application as AppController).repository)
    }

    override fun OrderDataChange(data: LiveData<List<Orders>>) {

        data.observe(viewLifecycleOwner, androidx.lifecycle.Observer { dist ->
            // Update the cached copy of the words in the adapter.
           // Log.e("sdafdfaf","=========$dist")
            listOrders.clear()
            dist?.let {
                listOrders.addAll(dist)
                  setItemsAdapter(listOrders)

            }
        })

    }

    private fun setItemsAdapter(listOrders: ArrayList<Orders>) {

        val adapter  = ItemsAdapter(listOrders)

        if(listOrders.isEmpty())
        {
           // tvNoData.visibility = View.VISIBLE
        }
        else
        {
            //tvNoData.visibility = View.GONE
        }

        rvTransaction.adapter = adapter

    }
}