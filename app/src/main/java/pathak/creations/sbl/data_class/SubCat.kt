package pathak.creations.sbl.data_class

import java.io.Serializable

data class SubCat(
    var catgroup: String = "",
    var category: String = "",
    var code: String = "",
    var description: String = "",
    var price: String = "",
    var weight: String = "",
    var ptrflag: String = "",
    var cartItem: String = "",
    var customPrice: String = "",
    var overAllPrice: String = "",
    var distIDMain: String = "",
    var editMode: Boolean = true
):Serializable