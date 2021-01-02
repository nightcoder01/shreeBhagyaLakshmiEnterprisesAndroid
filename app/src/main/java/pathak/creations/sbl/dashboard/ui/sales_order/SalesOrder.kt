package pathak.creations.sbl.dashboard.ui.sales_order

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.logout_alert.*
import pathak.creations.sbl.R
import pathak.creations.sbl.dashboard.ui.retailer_visit.RetailerVisitAdapter
import pathak.creations.sbl.data_class.BeatRetailerData

class SalesOrder : Fragment() {

    private lateinit var salesOrderVM: SalesOrderVM

    var list : ArrayList<BeatRetailerData> = ArrayList()

    lateinit var adapter : RetailerVisitAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        salesOrderVM =
            ViewModelProvider(this).get(SalesOrderVM::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)

        salesOrderVM.dateValue.observe(viewLifecycleOwner, Observer {
            tvDateValue.text = it
        })


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDate.setOnClickListener { salesOrderVM.datePicker(view) }
        tvDateValue.setOnClickListener { salesOrderVM.datePicker(view) }
        tvAdd.setOnClickListener {
          //  Navigation.findNavController(view).navigate(R.id.action_add_visit)
        }

        setList()

    }


    override fun onResume() {
        super.onResume()
        setList()
    }

    lateinit var deleteDialog: Dialog

    private fun deleteMethod(position: Int) {
        deleteDialog = Dialog(context!!)
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        deleteDialog.setContentView(R.layout.logout_alert)

        deleteDialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        deleteDialog.setCancelable(true)
        deleteDialog.setCanceledOnTouchOutside(false)
        deleteDialog.window!!.setGravity(Gravity.CENTER)

        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        deleteDialog.tvYes.setOnClickListener {
            deleteDialog.dismiss()
            list.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position,list.size)

        }

        deleteDialog.tvNo.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.show()
    }


    private fun setList() {

        list.clear()


        adapter  = RetailerVisitAdapter(list)

        rvRetailerVisit.adapter = adapter
        adapter.onClicked(object :RetailerVisitAdapter.CardInterface{
            override fun clickedSelected(position: Int, str: String) {
                if(str=="delete")
                {
                    Log.e("====delete==","==11==$position")

                    deleteMethod(position)
                }
                else
                {
                  //  Navigation.findNavController(rvRetailerVisit).navigate(R.id.action_edit_visit)
                }
            }
        })
    }
}