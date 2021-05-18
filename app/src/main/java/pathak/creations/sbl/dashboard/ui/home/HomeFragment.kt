package pathak.creations.sbl.dashboard.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import pathak.creations.sbl.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    var list : ArrayList<String> = ArrayList()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val textView: TextView = root.findViewById(R.id.text_home)
        val rvHome: RecyclerView = root.findViewById(R.id.rvHome)




        list.clear()
        list.add("Orders")
        list.add("Customers")
        list.add("Visited\n137")
        list.add("Ordered\n83")
        list.add("Pending\n83")
        list.add("Zero Sale\n61")
        list.add("Total Item\n65")
        list.add("Total Sale\n₹212188.34")

        val adapter  = HomeAdapter(list)
        rvHome.adapter = adapter
        adapter.onClicked(object :HomeAdapter.CardInterface{
            override fun clickedSelected(pos: Int) {
                if(pos==0)
                {
                Navigation.findNavController(rvHome).navigate(R.id.actionOrders)
            }}
        })




        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}