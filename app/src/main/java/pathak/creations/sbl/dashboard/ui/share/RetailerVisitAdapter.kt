package pathak.creations.sbl.dashboard.ui.share

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.retailer_item.view.*
import pathak.creations.sbl.R

class RetailerVisitAdapter(var list: ArrayList<RetailerVisit.RetailerVisitData>) : RecyclerView.Adapter<RetailerVisitAdapter.CardsViewHolder>() {


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


        if(position!=0)
        {
            holder.itemView.llAction.visibility = View.VISIBLE
            holder.itemView.tvAction.visibility = View.GONE
        }
        else
        {
            holder.itemView.llAction.visibility = View.GONE
            holder.itemView.tvAction.visibility = View.VISIBLE

        }


        holder.tvDate.background = list[position].drawable
        holder.itemView.tvBeat.background = list[position].drawable
        holder.itemView.tvDistributor.background = list[position].drawable
        holder.itemView.tvRetailer.background = list[position].drawable
        holder.itemView.tvRemarks.background = list[position].drawable
        holder.itemView.tvAction.background = list[position].drawable
        holder.itemView.llAction.background = list[position].drawable

        holder.tvDate.text = list[position].date
        holder.itemView.tvBeat.text = list[position].beat
        holder.itemView.tvDistributor.text = list[position].distributor
        holder.itemView.tvRetailer.text = list[position].retailer
        holder.itemView.tvRemarks.text = list[position].remarks
        holder.itemView.tvAction.text = list[position].action

        holder.itemView.ivDelete.setOnClickListener { clicked.clickedSelected(position,"delete") }
        holder.itemView.ivEdit.setOnClickListener { clicked.clickedSelected(position,"edit") }
    }





    inner class CardsViewHolder(v: View) : RecyclerView.ViewHolder(v){

        var tvDate = itemView.findViewById<TextView>(R.id.tvDate)


    }

}