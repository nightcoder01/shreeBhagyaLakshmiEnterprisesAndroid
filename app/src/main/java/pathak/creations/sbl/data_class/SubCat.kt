package pathak.creations.sbl.data_class

import java.io.Serializable

data class SubCat(
    var cat: String = "",
    var catgroup: String = "",
    var code: String = "",
    var cunits: String = "",
    var description: String = "",
    var sunits: String = "",
    var cartItem: String = ""
):Serializable