package pathak.creations.sbl.custom_adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.spinner_dropdown_item2.view.*
import pathak.creations.sbl.R
import pathak.creations.sbl.data_classes.Distributor

class SpinnerCustomDistributorAdapter(var listShort: List<Distributor>) :
     RecyclerView.Adapter<SpinnerCustomDistributorAdapter.CardsViewHolder>() {


    lateinit var clicked: CardInterface

    interface CardInterface {
        fun clickedSelected(position: Int)

    }

    fun onClicked(clicked: CardInterface)
    {this.clicked = clicked}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {

        val view  = LayoutInflater.from(parent.context).inflate(R.layout.spinner_dropdown_item2,parent,false)

        return CardsViewHolder(view)
    }

    override fun getItemCount(): Int {

        return listShort.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {

        holder.itemView.tvText.text = listShort[position].distName
        holder.itemView.setOnClickListener { clicked.clickedSelected(position) }
    }


    inner class CardsViewHolder(v: View) : RecyclerView.ViewHolder(v)


}