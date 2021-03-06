package pathak.creations.sbl.dashboard.ui.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item.view.*
import pathak.creations.sbl.R
import pathak.creations.sbl.data_classes.Orders
import java.util.*

class ItemsAdapter(var list: ArrayList<Orders>) :
    RecyclerView.Adapter<ItemsAdapter.CardsViewHolder>() {


    lateinit var clicked: CardInterface

    interface CardInterface {
        fun clickedSelected(pos: Int)

    }

    fun onClicked(clicked: CardInterface)
    {this.clicked = clicked}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {

        val view  = LayoutInflater.from(parent.context).inflate(R.layout.item,parent,false)

        return CardsViewHolder(view)
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {

        holder.itemView.itemCount.text = (position+1).toString()
        holder.itemView.itemPrice.text = "₹ "+list[position].price
        holder.itemView.itemName.text = list[position].description
        holder.itemView.itemQuantity.text = list[position].quantity

        holder.itemView.setOnClickListener {
           // clicked.clickedSelected(position)
        }

    }

    inner class CardsViewHolder(v: View) : RecyclerView.ViewHolder(v)
}