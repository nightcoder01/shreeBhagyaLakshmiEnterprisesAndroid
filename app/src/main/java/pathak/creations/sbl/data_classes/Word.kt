package pathak.creations.sbl.data_classes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey






@Entity(tableName = "distributor_table")
 class Distributor(@PrimaryKey @ColumnInfo(name = "dist") val distID : String, @ColumnInfo(name = "distName")val distName : String)


@Entity(tableName = "word_table")
class Word(@PrimaryKey @ColumnInfo(name = "word") val word : String)

@Entity(tableName = "cart_table")
class Cart(@PrimaryKey @ColumnInfo(name = "cartId") val cartId : String, @ColumnInfo(name = "distID")var distID : String
           , @ColumnInfo(name = "name")var name : String, @ColumnInfo(name = "price")var price : String
           , @ColumnInfo(name = "customPrice")var customPrice : String, @ColumnInfo(name = "overAllPrice")var overAllPrice : String
           , @ColumnInfo(name = "itemCount")var itemCount : String)
