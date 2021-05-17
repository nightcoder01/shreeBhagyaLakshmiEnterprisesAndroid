package pathak.creations.sbl.dashboard.ui.orders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.transaction_item.view.*
import pathak.creations.sbl.R
import pathak.creations.sbl.data_classes.Transactions

class MyTransactionsAdapter(var list: ArrayList<Transactions>) :
    RecyclerView.Adapter<MyTransactionsAdapter.CardsViewHolder>() {


    lateinit var clicked: CardInterface

    interface CardInterface {
        fun clickedSelected(pos: Int,str: String)
        fun valueChanged(pos: Int,str: String)

    }

    fun onClicked(clicked: CardInterface)
    {this.clicked = clicked}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {

        val view  = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item,parent,false)

        return CardsViewHolder(view)
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {

        holder.itemView.tvTransNoValue.text = list[position].transactionNo
        holder.itemView.tvRetailerNameValue.text = list[position].retailerName
        holder.itemView.tvQuantityValue.text = list[position].itemCount
        holder.itemView.tvAmountValue.text = "₹ "+list[position].totalAmount


    }


    inner class CardsViewHolder(v: View) : RecyclerView.ViewHolder(v)

}
