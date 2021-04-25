package pathak.creations.sbl.dashboard.ui.order_detail

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.order_detail.*
import pathak.creations.sbl.R


class OrderDetail : Fragment() {

    companion object {
        fun newInstance() = OrderDetail()
    }

    private lateinit var viewModel: OrderDetailVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.order_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(OrderDetailVM::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        tvBeatNameValue.text = arguments?.getString("beatName")
        tvGrandTotalValue.text = arguments?.getString("grandTotal")
        tvTotalValue.text = arguments?.getString("total")
        tvRetailerNameValue.text = arguments?.getString("retailerName")
        tvDetail.text ="You have successfully placed order of ${arguments?.getString("count")} items."


        tvDone.setOnClickListener { (view.context as Activity).onBackPressed() }
    }

}
