package pathak.creations.sbl.dashboard.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.home_item.view.*
import pathak.creations.sbl.R

class HomeAdapter(var list: ArrayList<String>) : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    lateinit var context: Context

    lateinit var clicked: CardInterface

    interface CardInterface {
        fun clickedSelected(pos: Int)

    }

    fun onClicked(clicked: CardInterface)
    {this.clicked = clicked}



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.itemView.tvText.text = list[position]
        holder.itemView.setOnClickListener { clicked.clickedSelected(position) }

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}