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
        fun valueChanged(pos: Int)
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
        holder.itemView.tvEditedPriceValue.text = "("+list[position].customPrice+")"
        holder.itemView.tvCount.text = Editable.Factory.getInstance().newEditable(if(list[position].cartItem.toInt()==0){""}else{list[position].cartItem})
        holder.itemView.etPriceEditedValue.text =Editable.Factory.getInstance().newEditable(list[position].customPrice)
        if(holder.itemView.tvCount
                .text.toString().isNotEmpty()) {
            holder.itemView.tvPriceOverallValue.text = String.format(
                "%.2f",
                (holder.itemView.etPriceEditedValue.text.toString()
                    .toFloat() * holder.itemView.tvCount.text.toString().toFloat())
            )
        }

        holder.itemView.tvCount.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //TODO("Not yet implemented")
            }

            override fun afterTextChanged(s: Editable?) {



                if(!s.isNullOrBlank() && s.toString().toInt()!=0)
                {
                    list[position].cartItem = s.toString()
                    holder.itemView.tvPriceOverallValue.text = String.format("%.2f", (holder.itemView.etPriceEditedValue.text.toString().toFloat() * holder.itemView.tvCount.text.toString().toFloat()))
                    clicked.clickedSelected(position,"add")
                }
                else
                {
                    list[position].cartItem = "0"
                    holder.itemView.tvPriceOverallValue.text = ""
                    clicked.clickedSelected(position,"add")
                }

                //TODO("Not yet implemented")
            }
        })

/*

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
*/

        holder.itemView.etPriceEditedValue.setOnClickListener {
            clicked.valueChanged(position)
        }
        /*holder.itemView.etPriceEditedValue.setOnEditorActionListener(object :TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if(actionId==EditorInfo.IME_ACTION_DONE)
                {
                    CommonMethods.hideKeyboard(holder.itemView.etPriceEditedValue)
                    return if(list[position].editMode) {
                        //normal
                        val s = holder.itemView.etPriceEditedValue.text.toString()
                        clicked.valueChanged(position)
                        true
                    }
                    else {
                        val s = list[position].customPrice
                        clicked.valueChanged(position)
                        Toast.makeText(holder.itemView.etPriceEditedValue.context,"once added can't change , go to My cart to make changes.",Toast.LENGTH_SHORT).show()
                        true
                    }
                }
                return false
            }
        })*/


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