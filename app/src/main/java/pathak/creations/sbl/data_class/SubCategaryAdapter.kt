package pathak.creations.sbl.data_class

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.sub_category.view.*
import pathak.creations.sbl.R

class SubCategaryAdapter(var list: List<SubCat>) :
    RecyclerView.Adapter<SubCategaryAdapter.CardsViewHolder>() {


    lateinit var clicked: CardInterface

    interface CardInterface {
        fun clickedSelected(pos: Int,str: String)

    }

    fun onClicked(clicked: CardInterface)
    {this.clicked = clicked}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolder {

        val view  = LayoutInflater.from(parent.context).inflate(R.layout.sub_category,parent,false)

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
        holder.itemView.tvPriceValue.text = "150"
        holder.itemView.tvCount.text = list[position].cartItem


        holder.itemView.flAdd.setOnClickListener{

            clicked.clickedSelected(position,"add")
        }

        holder.itemView.flMinus.setOnClickListener{

            clicked.clickedSelected(position,"remove")
        }

    }


    inner class CardsViewHolder(v: View) : RecyclerView.ViewHolder(v)

}