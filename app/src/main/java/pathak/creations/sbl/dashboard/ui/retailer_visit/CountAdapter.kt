package pathak.creations.sbl.dashboard.ui.retailer_visit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.count_item.view.*
import pathak.creations.sbl.R

class CountAdapter(var list: ArrayList<RetailerVisit.CountData>) :
    RecyclerView.Adapter<CountAdapter.CardsViewHolder>() {


    lateinit var clicked: CardInterface

    interface CardInterface {
        fun clickedSelected(position: Int)

    }


    fun onClicked(clicked: CardInterface) {
        this.clicked = clicked
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.count_item, parent, false)

        return CardsViewHolder(view)
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {


        holder.itemView.tvCount.text = (position + 1).toString()

        if (!list[position].bool) {

            holder.itemView.tvCount.setBackgroundColor(holder.itemView.context.resources.getColor(R.color.white))
            holder.itemView.tvCount.setTextColor(holder.itemView.context.resources.getColor(R.color.black))
        } else {
            holder.itemView.tvCount.setBackgroundColor(holder.itemView.context.resources.getColor(R.color.colorAccent))
            holder.itemView.tvCount.setTextColor(holder.itemView.context.resources.getColor(R.color.white))
        }

        holder.itemView.setOnClickListener {
            clicked.clickedSelected(position)
        }

    }


    inner class CardsViewHolder(v: View) : RecyclerView.ViewHolder(v)

}