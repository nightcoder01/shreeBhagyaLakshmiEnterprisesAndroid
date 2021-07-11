package pathak.creations.sbl.dashboard.ui.retailer_master

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.retailer_master_item.view.*
import pathak.creations.sbl.R
import pathak.creations.sbl.data_classes.Retailer

class RetailerAdapter(var list: List<Retailer>) :
    RecyclerView.Adapter<RetailerAdapter.CardsViewHolder>() {


    lateinit var clicked: CardInterface

    interface CardInterface {
        fun clickedSelected(position: Int,str: String)

    }

    fun onClicked(clicked: CardInterface)
    {this.clicked = clicked}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {

        val view  = LayoutInflater.from(parent.context).inflate(R.layout.retailer_master_item,parent,false)

        return CardsViewHolder(view)
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {

       /// val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)
      //  val date = LocalDate.parse(list[position].date, formatter)

        holder.tvDate.text = list[position].date  //list[position].date
        holder.itemView.tvBeat.text = list[position].beatname
        holder.itemView.tvDistributor.text = list[position].distributor
        holder.itemView.tvRetailer.text = list[position].retailer_name
        holder.itemView.tvRemarks.text =if(list[position].address == "null") "" else  list[position].address
        holder.itemView.tvPhoneValue.text = list[position].mobile

        holder.itemView.ivDelete.setOnClickListener { clicked.clickedSelected(position,"delete") }
        holder.itemView.ivEdit.setOnClickListener { clicked.clickedSelected(position,"edit") }
        holder.itemView.tvAdd.setOnClickListener { clicked.clickedSelected(position,"add") }
    }


    inner class CardsViewHolder(v: View) : RecyclerView.ViewHolder(v){

        var tvDate = itemView.findViewById<TextView>(R.id.tvDate)


    }

}