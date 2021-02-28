package pathak.creations.sbl.data_classes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey






@Entity(tableName = "distributor_table")
 class Distributor(@PrimaryKey @ColumnInfo(name = "dist") val distID : String, @ColumnInfo(name = "distName")val distName : String)

@Entity(tableName = "beat_table")
 class Beat(@PrimaryKey @ColumnInfo(name = "beat_id") val beat_id : String, @ColumnInfo(name = "dist_id")val dist_id : String,
             @ColumnInfo(name = "distributor")val distributor : String,@ColumnInfo(name = "state")val state : String,
             @ColumnInfo(name = "areaname")val areaname : String,@ColumnInfo(name = "beatname")val beatname : String
            )

@Entity(tableName = "retailer_table")
 class Retailer(@PrimaryKey @ColumnInfo(name = "retailer_table_id") val retailer_table_id : String, @ColumnInfo(name = "date")val date : String,
             @ColumnInfo(name = "dist_id")val dist_id : String,@ColumnInfo(name = "distributor")val distributor : String,
             @ColumnInfo(name = "retailer_id")val retailer_id : String,@ColumnInfo(name = "retailer_name")val retailer_name : String,
             @ColumnInfo(name = "beatname")val beatname : String,@ColumnInfo(name = "address")val address : String,
             @ColumnInfo(name = "phone")val phone : String,@ColumnInfo(name = "mobile")val mobile : String,
             @ColumnInfo(name = "type")val type : String,@ColumnInfo(name = "note")val note : String,
             @ColumnInfo(name = "place")val place : String,@ColumnInfo(name = "firstname")val firstname : String,
             @ColumnInfo(name = "lastname")val lastname : String,@ColumnInfo(name = "state")val state : String,
             @ColumnInfo(name = "areaname")val areaname : String,@ColumnInfo(name = "country")val country : String,
             @ColumnInfo(name = "pincode")val pincode : String,@ColumnInfo(name = "cst")val cst : String,
             @ColumnInfo(name = "cst_registerationdate")val cst_registerationdate : String,@ColumnInfo(name = "vattin")val vattin : String,
             @ColumnInfo(name = "csttin")val csttin : String,@ColumnInfo(name = "pan")val pan : String,
             @ColumnInfo(name = "updated")val updated : String,@ColumnInfo(name = "client")val client : String,
             @ColumnInfo(name = "empname")val empname : String,@ColumnInfo(name = "ca")val ca : String,
             @ColumnInfo(name = "cac")val cac : String,@ColumnInfo(name = "rid")val rid : String,
             @ColumnInfo(name = "classification")val classification : String,@ColumnInfo(name = "retailer_type")val retailer_type : String,
             @ColumnInfo(name = "dvisit")val dvisit : String,@ColumnInfo(name = "cperson")val cperson : String,
             @ColumnInfo(name = "email")val email : String,@ColumnInfo(name = "gstin")val gstin : String,
             @ColumnInfo(name = "sno")val sno : String,@ColumnInfo(name = "latitude")val latitude : String,
             @ColumnInfo(name = "longitude")val longitude : String
            )


@Entity(tableName = "word_table")
class Word(@PrimaryKey @ColumnInfo(name = "word") val word : String)

@Entity(tableName = "cart_table")
class Cart(@PrimaryKey @ColumnInfo(name = "cartId") val cartId : String, @ColumnInfo(name = "distID")var distID : String
           , @ColumnInfo(name = "name")var name : String, @ColumnInfo(name = "price")var price : String
           , @ColumnInfo(name = "customPrice")var customPrice : String, @ColumnInfo(name = "overAllPrice")var overAllPrice : String
           , @ColumnInfo(name = "itemCount")var itemCount : String, @ColumnInfo(name = "beatName")var beatName : String
           , @ColumnInfo(name = "retailer_name")var retailer_name : String)
