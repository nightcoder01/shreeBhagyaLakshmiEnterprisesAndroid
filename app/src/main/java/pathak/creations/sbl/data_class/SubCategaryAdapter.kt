package pathak.creations.sbl.data_class

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
        fun valueChanged(pos: Int,str: String)

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
        holder.itemView.tvPriceValue.text = list[position].price
        holder.itemView.tvCount.text = list[position].cartItem

        holder.itemView.flAdd.setOnClickListener{

            clicked.clickedSelected(position,"add")
        }

        holder.itemView.flMinus.setOnClickListener{

            clicked.clickedSelected(position,"remove")
        }
       // holder.itemView.etPriceEditedValue.text =Editable.Factory.getInstance().newEditable((list[position].price.toFloat()+(list[position].price.toFloat()*(45))/1000 ).toString())
        holder.itemView.etPriceEditedValue.text =Editable.Factory.getInstance().newEditable(list[position].customPrice)



        Log.e("dfad0","======${list[position].customPrice}")
        Log.e("dfad0","======${(list[position].price.toFloat()*(45))/1000}")
        holder.itemView.tvPriceOverallValue.text  = (holder.itemView.etPriceEditedValue.text.toString().toFloat()*holder.itemView.tvCount.text.toString().toFloat()).toString()



        holder.itemView.etPriceEditedValue.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {



               // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
               // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if(!s.isNullOrBlank())
                {
                  //  clicked.valueChanged(position,s.toString())
                  //  holder.itemView.tvPriceOverallValue.text  = (holder.itemView.etPriceEditedValue.text.toString().toFloat()*holder.itemView.tvCount.text.toString().toFloat()).toString()

                }


              //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })


    }


    inner class CardsViewHolder(v: View) : RecyclerView.ViewHolder(v)

}