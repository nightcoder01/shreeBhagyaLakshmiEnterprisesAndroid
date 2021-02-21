package pathak.creations.sbl.dashboard.ui.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyCartVM  : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = ""
    }
    val text: LiveData<String> = _text

    var _date = MutableLiveData<String>().apply {
        value = "DD/MM/YYYY"
    }
    val dateValue: LiveData<String> = _date







}