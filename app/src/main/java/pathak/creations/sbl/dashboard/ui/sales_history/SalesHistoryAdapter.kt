package pathak.creations.sbl.dashboard.ui.sales_history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.shop_totalpage.view.*
import pathak.creations.sbl.R
import pathak.creations.sbl.data_class.BeatRetailerData

class SalesHistoryAdapter (var list: ArrayList<BeatRetailerData>): RecyclerView.Adapter<SalesHistoryAdapter.CardsViewHolder>() {

    lateinit var clicked: CardInterface

    interface CardInterface {
        fun clickedSelected(position: Int,str: String)

    }

    inner class CardsViewHolder(v: View) : RecyclerView.ViewHolder(v){

        var tvDate = itemView.findViewById<TextView>(R.id.tvDate)


    }
    fun onClicked(clicked: CardInterface)
    {
        this.clicked = clicked
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder
    {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.shop_totalpage,parent,false)

        return CardsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        //holder.tvDate.text = list[position].date
//        holder.itemView.tvBeat.text = list[position].beatname
//        holder.itemView.tvRetailer.text = list[position].retailer_name
//       holder.itemView.tvDistributor2.text = list[position].distributor
       holder.itemView.tvAdd1.setOnClickListener { clicked.clickedSelected(position,"add")}
    }

}
