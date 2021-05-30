package pathak.creations.sbl.dashboard.ui.retailer_master

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.retailer_master.*
import pathak.creations.sbl.AppController
import pathak.creations.sbl.R
import pathak.creations.sbl.common.CommonKeys
import pathak.creations.sbl.common.CommonMethods
import pathak.creations.sbl.common.PreferenceFile
import pathak.creations.sbl.data_class.BeatRetailerData
import pathak.creations.sbl.data_classes.Distributor
import pathak.creations.sbl.data_classes.Retailer
import pathak.creations.sbl.data_classes.WordViewModel
import pathak.creations.sbl.data_classes.WordViewModelFactory

class RetailerMaster : Fragment(){

    private lateinit var retailerMasterVM: RetailerMasterVM

    var list: ArrayList<BeatRetailerData> = ArrayList()
    var listRetailers: ArrayList<Retailer> = ArrayList()
    var listRetailersFilter: ArrayList<Retailer> = ArrayList()

    var distributor = ""


    lateinit var ctx: Context

    lateinit var adapter: RetailerAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        retailerMasterVM =
            ViewModelProvider(this).get(RetailerMasterVM::class.java)
        val root = inflater.inflate(R.layout.retailer_master, container, false)

        ctx = root.context

        retailerMasterVM.dateValue.observe(viewLifecycleOwner, Observer {
            tvDateValue.text = it
        })

        return root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvDate.setOnClickListener { retailerMasterVM.datePicker(view) }
        tvDateValue.setOnClickListener { retailerMasterVM.datePicker(view) }
        tvAddRetailer.setOnClickListener {

            CommonMethods.hideKeyboard(rvRetailerVisit)

            Log.e("tvDistributor2","====${tvDistributor2.hint}")

            distributor = if(tvDistributor2.hint!="Select Distributor Name")

            { tvDistributor2.hint.toString() }
            else
            {""}

            Navigation.findNavController(view).navigate(R.id.action_add_retailer) }


        setDistributor()

        setSearch()

        //set live data observer
        wordViewModel.allDistributor.observe(viewLifecycleOwner, Observer { dist ->
            // Update the cached copy of the words in the adapter.

            dist?.let {

                setDistributorAdapter(it)

            }
        })




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
                    setRetailerAdapter(listRetailers)
                }
                else
                {
                    listRetailersFilter.clear()


                    listRetailersFilter.addAll(listRetailers.filter
                    { it.retailer_name.toLowerCase().contains(s.toString().toLowerCase())
                            ||
                            it.beatname.toLowerCase().contains(s.toString().toLowerCase())
                            ||
                            it.distributor.toLowerCase().contains(s.toString().toLowerCase())
                    })

                    setRetailerAdapter(listRetailersFilter)
                   // setBeatRetailerAdapter(listBeatsRetailerFilter)

                }

                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

            }
        })
    }


    private fun setDistributor() {

       if(distributor.isNotEmpty())
       {
           tvDistributor2.hint = distributor
       }

        if (PreferenceFile.retrieveKey(ctx, CommonKeys.TYPE).equals("distributor")) {
            tvDistributor2.hint = PreferenceFile.retrieveKey(ctx, CommonKeys.NAME)
            // callBeatList(PreferenceFile.retrieveKey(ctx,CommonKeys.NAME)!!)
            callDistRetailer(PreferenceFile.retrieveKey(ctx, CommonKeys.NAME)!!)
        }
        else {
         //   callDistributorList()
        }
    }


    private fun callDistRetailer(distId: String) {
        try {

            wordViewModel.allRetailer.observe(viewLifecycleOwner, Observer { retail ->
                // Update the cached copy of the words in the adapter.

                retail?.let {
                    listRetailers.addAll(it)
                    setRetailerAdapter(it)

                }
            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }





    private fun setDistributorAdapter(list: List<Distributor>) {

        tvDistributor2.text =PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_DISTRIBUTOR_NAME)

        callDistRetailer(PreferenceFile.retrieveKey(ctx,CommonKeys.SELECTED_DISTRIBUTOR)!!)


    }

    var popupWindow: PopupWindow? = null




    private fun setRetailerAdapter(listRetailers: List<Retailer>) {

        adapter = RetailerAdapter(listRetailers)

        rvRetailerVisit.adapter = adapter
        adapter.onClicked(object : RetailerAdapter.CardInterface {
            override fun clickedSelected(position: Int, str: String) {

                if(str=="edit") {

                    distributor = tvDistributor2.hint.toString()
                    CommonMethods.hideKeyboard(rvRetailerVisit)

                    val bundle = bundleOf("dist_id" to listRetailers[position].dist_id,
                        "idd" to listRetailers[position].retailer_table_id,
                        "distributor" to listRetailers[position].distributor,
                        "retailer_id" to listRetailers[position].retailer_id,
                        "retailer_name" to listRetailers[position].retailer_name,
                        "beatName" to listRetailers[position].beatname,
                        "address" to listRetailers[position].address,
                        "mobile" to listRetailers[position].mobile,
                        "areaname" to listRetailers[position].areaname,
                        "state" to listRetailers[position].state,
                        "gstin" to listRetailers[position].gstin,
                        "classification" to listRetailers[position].classification,
                        "retailer_type" to listRetailers[position].retailer_type
                        )

                    Navigation.findNavController(rvRetailerVisit)
                        .navigate(R.id.action_edit_retailer,bundle)
                }
            }
        })
    }




    //data base work
    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory(((context as Activity).application as AppController).repository)
    }

}