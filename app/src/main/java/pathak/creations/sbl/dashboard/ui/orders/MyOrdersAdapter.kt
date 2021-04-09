package pathak.creations.sbl.dashboard.ui.orders
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.orders_item.view.*
import pathak.creations.sbl.R
import pathak.creations.sbl.data_classes.Orders

class MyOrdersAdapter(var list: List<Orders>) :
    RecyclerView.Adapter<MyOrdersAdapter.CardsViewHolder>() {


    lateinit var clicked: CardInterface

    interface CardInterface {
        fun clickedSelected(pos: Int,str: String)
        fun valueChanged(pos: Int,str: String)

    }

    fun onClicked(clicked: CardInterface)
    {this.clicked = clicked}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {

        val view  = LayoutInflater.from(parent.context).inflate(R.layout.orders_item,parent,false)

        return CardsViewHolder(view)
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {

        holder.itemView.tvName.text = list[position].description
        holder.itemView.tvImageText.text = list[position].description
        holder.itemView.tvPriceValue.text = list[position].ptd
        holder.itemView.tvCount.text = list[position].quantity
        holder.itemView.tvPriceEditedValue.text = list[position].price
        holder.itemView.tvPriceOverallValue.text  = (holder.itemView.tvPriceEditedValue.text.toString().toFloat()*holder.itemView.tvCount.text.toString().toFloat()).toString()
    }


    inner class CardsViewHolder(v: View) : RecyclerView.ViewHolder(v)

}
