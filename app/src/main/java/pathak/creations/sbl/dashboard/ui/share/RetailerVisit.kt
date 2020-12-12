package pathak.creations.sbl.dashboard.ui.share

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.logout_alert.*
import kotlinx.android.synthetic.main.retailer_visit.*
import pathak.creations.sbl.R

class RetailerVisit : Fragment() {

    private lateinit var retailerVisitVM: RetailerVisitVM


    var list : ArrayList<RetailerVisitData> = ArrayList()
   lateinit var adapter : RetailerVisitAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retailerVisitVM = ViewModelProvider(this).get(RetailerVisitVM::class.java)
        val root = inflater.inflate(R.layout.retailer_visit, container, false)

        retailerVisitVM.dateValue.observe(viewLifecycleOwner, Observer {
            tvDateValue.text = it
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDate.setOnClickListener { retailerVisitVM.datePicker(view) }
        tvDateValue.setOnClickListener { retailerVisitVM.datePicker(view) }
        tvAdd.setOnClickListener { Navigation.findNavController(view).navigate(R.id.action_add_visit) }

        setList()

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
                    Navigation.findNavController(view).navigate(R.id.action_edit_visit)
                }
            }

        })

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

        deleteDialog.tv_yes.setOnClickListener {
            deleteDialog.dismiss()
            list.removeAt(position)
            adapter.notifyItemRemoved(position)
            adapter.notifyItemRangeChanged(position,list.size)

        }

        deleteDialog.tv_no.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.show()
    }


    private fun setList() {

        list.clear()
        for(i in 0 until 12)
        {

            if(i!=0) {

                list.add(
                    RetailerVisitData(
                        i.toString(), "12/12/2020",
                        "AAI MATHAJI ENTERPRISES - BANGALORE", "SEEGEHALLI",
                        "MANJUNAT STORE", "Order placed","",resources.getDrawable(R.drawable.content_cell)
                    )
                )
            }
            else
            {
                list.add(
                    RetailerVisitData(
                        i.toString(), "Date",
                        "Distributor", "Beat Name",
                        "Retailer Name", "Remarks","Actions",resources.getDrawable(R.drawable.content_header)
                    )
                )
            }

        }

    }


    data class RetailerVisitData(var id: String = "",var date: String = "",
                                 var distributor: String = "",var beat: String = "",
                                 var retailer: String = "",var remarks: String = "",
                                 var action: String = "",var drawable: Drawable
                                 )


}