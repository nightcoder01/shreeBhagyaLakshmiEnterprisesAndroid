package pathak.creations.sbl.custom_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.TextView
import pathak.creations.sbl.R


class CustomAdapter(
    var context: Context,
   var listShort: ArrayList<String>
) : BaseAdapter() {


    var mInflater: LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {

        val view: View

        val vh: ItemRowHolder

        if (convertView == null) {


            view = mInflater.inflate(R.layout.spinner_dropdown_item2, viewGroup, false)

            vh = ItemRowHolder(view)

            view?.tag = vh

        } else {


            view = convertView
            vh = view.tag as ItemRowHolder

        }

        val dataModel = listShort[position]

        vh.tvText.text = dataModel.capitalize()

        if (position == 1) {
            vh.etSearch.visibility = View.VISIBLE
        } else {
            vh.etSearch.visibility = View.GONE
        }



        return view

    }

    override fun getItem(p0: Int): Any? {
        return null
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return listShort.size
    }

    private class ItemRowHolder(row: View?) {
        val tvText: TextView = row?.findViewById(R.id.tvText) as TextView
        val etSearch: EditText = row?.findViewById(R.id.etSearch) as EditText

    }

}
