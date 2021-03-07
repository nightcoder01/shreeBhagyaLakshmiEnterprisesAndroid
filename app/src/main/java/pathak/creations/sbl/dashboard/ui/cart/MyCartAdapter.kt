package pathak.creations.sbl.dashboard.ui.cart

import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.sub_category.view.*
import pathak.creations.sbl.R
import pathak.creations.sbl.data_classes.Cart

class MyCartAdapter(var list: List<Cart>) :
    RecyclerView.Adapter<MyCartAdapter.CardsViewHolder>() {


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



        Log.e("cartDate======","==========${list[position]}")

        holder.itemView.tvName.text = list[position].name
        holder.itemView.tvImageText.text = list[position].name
        holder.itemView.tvPriceValue.text = list[position].price
        holder.itemView.tvCount.text = list[position].itemCount
        holder.itemView.etPriceEditedValue.text = Editable.Factory.getInstance().newEditable(list[position].customPrice)
        holder.itemView.tvPriceOverallValue.text  = (holder.itemView.etPriceEditedValue.text.toString().toFloat()*holder.itemView.tvCount.text.toString().toFloat()).toString()
        holder.itemView.tvAddCart.visibility = View.GONE



        holder.itemView.flAdd.setOnClickListener{

                clicked.clickedSelected(position,"add")

        }

        holder.itemView.flMinus.setOnClickListener{

                clicked.clickedSelected(position, "remove")

        }

        holder.itemView.flAdd.setOnLongClickListener {

                clicked.clickedSelected(position,"long")

            true
        }
        holder.itemView.flMinus.setOnLongClickListener {

                clicked.clickedSelected(position,"long")

            true
        }



        holder.itemView.etPriceEditedValue.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if(actionId== EditorInfo.IME_ACTION_DONE)
                {

                        //normal
                        val s = holder.itemView.etPriceEditedValue.text.toString()
                        clicked.valueChanged(position,s)
                    return  true

                }
                return false
            }
        })


        /*holder.itemView.tvAddCart.setOnClickListener {
            if(list[position].editMode)
            {
                clicked.changeEditMode(position,false)}
        }

        if(list[position].editMode)
        {
            //normal
            holder.itemView.tvAddCart.text  = holder.itemView.tvAddCart.context.getString(R.string.add_to_cart)



        }
        else
        {
            holder.itemView.tvAddCart.text  = holder.itemView.tvAddCart.context.getString(R.string.item_added)

            //editmode removed
            ///and add cart
        }*/


    }


    inner class CardsViewHolder(v: View) : RecyclerView.ViewHolder(v)

}