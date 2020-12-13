package pathak.creations.sbl.dashboard.ui.sales_order

import android.app.DatePickerDialog
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class SalesOrderVM : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = ""
    }
    val text: LiveData<String> = _text

    var _date = MutableLiveData<String>().apply {
        value = "DD/MM/YYYY"
    }
    val dateValue: LiveData<String> = _date



    fun datePicker(view: View)
    {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)


        Log.e("sdfdsfds","dfdsfdsfd")

        val dpd = DatePickerDialog(
            view.context!!,
            DatePickerDialog.OnDateSetListener { view2, year2, monthOfYear, dayOfMonth ->

                val month2 = monthOfYear + 1

                _date.value= "$dayOfMonth/$month2/$year2"
            },
            year,
            month,
            day
        )
        dpd.datePicker.maxDate = c.timeInMillis

        dpd.show()

    }


}