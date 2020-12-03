package pathak.creations.sbl.dashboard.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pathak.creations.sbl.R

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.home_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return 8
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        


    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // val li_book: LinearLayout = itemView.findViewById(R.id.li_book)
        // val tv_status: TextView = itemView.findViewById(R.id.tv_status)

    }
}