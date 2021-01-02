package pathak.creations.sbl.data_class

import java.io.Serializable

data class CategoriesData(
    var main_category: String = "",
    var sub_cats: ArrayList<SubCat>
):Serializable