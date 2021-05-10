package pathak.creations.sbl.dashboard.ui.retailer_visit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.retailer_item.view.*
import pathak.creations.sbl.R
import pathak.creations.sbl.data_classes.Retailer

class RetailerVisitAdapter(var list: List<Retailer>) :
    RecyclerView.Adapter<RetailerVisitAdapter.CardsViewHolder>() {


    lateinit var clicked: CardInterface

    interface CardInterface {
        fun clickedSelected(position: Int,str: String)

    }

    fun onClicked(clicked: CardInterface)
    {this.clicked = clicked}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {

        val view  = LayoutInflater.from(parent.context).inflate(R.layout.retailer_item,parent,false)

        return CardsViewHolder(view)
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {

        holder.tvDate.text = list[position].date
        holder.itemView.tvBeat.text = list[position].beatname
        holder.itemView.tvDistributor.text = list[position].distributor
        holder.itemView.tvRetailer.text = list[position].retailer_name
        holder.itemView.tvAreaName.text = list[position].areaname
        holder.itemView.tvNumber.text = list[position].mobile

        holder.itemView.ivDelete.setOnClickListener { clicked.clickedSelected(position,"delete") }
        holder.itemView.ivEdit.setOnClickListener { clicked.clickedSelected(position,"edit") }
        holder.itemView.tvAdd.setOnClickListener { clicked.clickedSelected(position,"add") }
       // holder.itemView.tvAreaName.setOnClickListener { clicked.clickedSelected(position,"remarks") }
       // holder.itemView.tvRemarks.setOnClickListener { clicked.clickedSelected(position,"remarks") }


        if(list[position].todayDone)
        {
            holder.itemView.clRetailer.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.selectedGreen))
        }
        else
        {
            // holder.itemView.clRetailer.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.white))
        }

    }


    inner class CardsViewHolder(v: View) : RecyclerView.ViewHolder(v){

        var tvDate = itemView.findViewById<TextView>(R.id.tvDate)


    }

}