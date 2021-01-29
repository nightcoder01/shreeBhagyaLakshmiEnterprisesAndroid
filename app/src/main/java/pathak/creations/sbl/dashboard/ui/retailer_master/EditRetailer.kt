package pathak.creations.sbl.dashboard.ui.retailer_master

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import pathak.creations.sbl.R


class EditRetailer : Fragment() {

    companion object {
        fun newInstance() = EditRetailer()
    }

    private lateinit var viewModel: EditRetailerVM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_retailer, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditRetailerVM::class.java)
        // TODO: Use the ViewModel
    }

}
