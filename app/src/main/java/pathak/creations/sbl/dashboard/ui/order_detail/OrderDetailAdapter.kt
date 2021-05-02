package pathak.creations.sbl.dashboard.ui.order_detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.orders_detail_item.view.*
import pathak.creations.sbl.R
import pathak.creations.sbl.data_classes.Cart

class OrderDetailAdapter(var list: ArrayList<Cart>) :
    RecyclerView.Adapter<OrderDetailAdapter.CardsViewHolder>() {


    lateinit var clicked: CardInterface

    interface CardInterface {
        fun clickedSelected(pos: Int,str: String)
        fun valueChanged(pos: Int,str: String)

    }

    fun onClicked(clicked: CardInterface)
    {this.clicked = clicked}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {

        val view  = LayoutInflater.from(parent.context).inflate(R.layout.orders_detail_item,parent,false)

        return CardsViewHolder(view)
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {

        holder.itemView.tvName.text = list[position].name
        holder.itemView.tvPriceValue.text = list[position].customPrice
        holder.itemView.tvQtyValue.text = list[position].itemCount
        holder.itemView.tvTotalValue.text = list[position].ptr_total
    }


    inner class CardsViewHolder(v: View) : RecyclerView.ViewHolder(v)

}