package pathak.creations.sbl.data_class

import android.text.Editable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.sub_category.view.*
import pathak.creations.sbl.R

class SubCategaryAdapter(var list: List<SubCat>) :
    RecyclerView.Adapter<SubCategaryAdapter.CardsViewHolder>() {


    lateinit var clicked: CardInterface

    interface CardInterface {
        fun clickedSelected(pos: Int,str: String)
        fun valueChanged(pos: Int,str: String)
        fun changeEditMode(pos: Int,editMode: Boolean)

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

        holder.itemView.npMain.maxValue = 999
        holder.itemView.npMain.minValue= 0
        holder.itemView.tvName.text = list[position].description
        holder.itemView.tvImageText.text = list[position].description
        holder.itemView.tvPriceValue.text = list[position].price
        holder.itemView.tvCount.text = list[position].cartItem
        holder.itemView.etPriceEditedValue.text =Editable.Factory.getInstance().newEditable(list[position].customPrice)
        holder.itemView.tvPriceOverallValue.text  = String.format("%.2f",(holder.itemView.etPriceEditedValue.text.toString().toFloat()*holder.itemView.tvCount.text.toString().toFloat()))

        holder.itemView.flAdd.setOnClickListener{
            if(list[position].editMode)
            {
            clicked.clickedSelected(position,"add")}
        }


        holder.itemView.flAdd.setOnLongClickListener {
            if(list[position].editMode)
            {
                clicked.clickedSelected(position,"long")
            }
            true
        }
        holder.itemView.flMinus.setOnLongClickListener {
            if(list[position].editMode)
            {
                clicked.clickedSelected(position,"long")
            }
            true
        }

        holder.itemView.flMinus.setOnClickListener{
            if(list[position].editMode) {
                clicked.clickedSelected(position, "remove")
            }
        }

        holder.itemView.etPriceEditedValue.setOnEditorActionListener(object :TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if(actionId==EditorInfo.IME_ACTION_DONE)
                {
                    return if(list[position].editMode) {
                        //normal
                        val s = holder.itemView.etPriceEditedValue.text.toString()
                        clicked.valueChanged(position,s)
                        true
                    } else {
                        val s = list[position].customPrice
                        clicked.valueChanged(position, s)
                        Toast.makeText(holder.itemView.etPriceEditedValue.context,"once added can't change , go to My cart to make changes.",Toast.LENGTH_SHORT).show()
                        true
                    }
                }
                return false
            }
        })


        holder.itemView.tvAddCart.setOnClickListener {
            if(list[position].editMode)
            {
            clicked.changeEditMode(position,false)}
        }

        if(list[position].editMode)
        {
            holder.itemView.tvAddCart.text  = holder.itemView.tvAddCart.context.getString(R.string.add_to_cart)
        }
        else
        {
            holder.itemView.tvAddCart.text  = holder.itemView.tvAddCart.context.getString(R.string.item_added)
        }

    }

    inner class CardsViewHolder(v: View) : RecyclerView.ViewHolder(v)

}