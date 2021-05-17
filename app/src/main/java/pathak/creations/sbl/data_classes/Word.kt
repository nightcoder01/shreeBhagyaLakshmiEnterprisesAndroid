package pathak.creations.sbl.data_classes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


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
             @ColumnInfo(name = "type")val type : String,@ColumnInfo(name = "note")var note : String,
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
             @ColumnInfo(name = "sno")val sno : String,@ColumnInfo(name = "latitude")val latitude : String
                ,@ColumnInfo(name = "longitude")val longitude : String ,@ColumnInfo(name = "todayDone")val todayDone : Boolean
            )


@Entity(tableName = "categories_table")
class Categories(@PrimaryKey @ColumnInfo(name = "categories_table_id") val categories_table_id : String, @ColumnInfo(name = "catgroup")val catgroup: String,
           @ColumnInfo(name = "category")val category: String, @ColumnInfo(name = "code")val code: String,
           @ColumnInfo(name = "description")val description: String, @ColumnInfo(name = "price")val price: String,
           @ColumnInfo(name = "weight")val weight: String, @ColumnInfo(name = "ptrflag")val ptrflag: String)


@Entity(tableName = "word_table")
class Word(@PrimaryKey @ColumnInfo(name = "word") val word : String)


@Entity(tableName = "cart_table")
class Cart(@PrimaryKey (autoGenerate = true) val cartId : Int, @ColumnInfo(name = "distID")var distID : String
           , @ColumnInfo(name = "name")var name : String, @ColumnInfo(name = "price")var price : String
           , @ColumnInfo(name = "customPrice")var customPrice : String, @ColumnInfo(name = "overAllPrice")var overAllPrice : String
           , @ColumnInfo(name = "itemCount")var itemCount : String, @ColumnInfo(name = "beatName")var beatName : String
           , @ColumnInfo(name = "retailer_name")var retailer_name : String, @ColumnInfo(name = "retailer_code")var retailer_code : String
           , @ColumnInfo(name = "dist_name")var dist_name : String, @ColumnInfo(name = "cat_group")var cat_group : String
           , @ColumnInfo(name = "category")var category : String, @ColumnInfo(name = "cat_code")var cat_code : String
           , @ColumnInfo(name = "ptr_price")var ptr_price : String, @ColumnInfo(name = "ptd_price")var ptd_price : String
           , @ColumnInfo(name = "ptr_total")var ptr_total : String, @ColumnInfo(name = "ptd_total")var ptd_total : String) :Serializable


@Entity(tableName = "transaction_table")
class Transactions(@PrimaryKey (autoGenerate = true) val id : Int,
                   @ColumnInfo(name = "transactionNo")var transactionNo : String,
                   @ColumnInfo(name = "distributorID")var distributorID : String,
                   @ColumnInfo(name = "distributorName")var distributorName : String,
                   @ColumnInfo(name = "retailerId")var retailerId : String,
                   @ColumnInfo(name = "retailerName")var retailerName : String,
                   @ColumnInfo(name = "beatName")var beatName : String,
                   @ColumnInfo(name = "itemCount")var itemCount : String,
                   @ColumnInfo(name = "totalAmount")var totalAmount : String)



@Entity(tableName = "order_table")
class Orders(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "saincr") val saincr: String,
    @ColumnInfo(name = "m") val m: String,
    @ColumnInfo(name = "y") val y: String,
    @ColumnInfo(name = "fy") val fy: String,
    @ColumnInfo(name = "invoice") val invoice: String,
    @ColumnInfo(name = "doc_no") val doc_no: String,
    @ColumnInfo(name = "vendorid") val vendorid: String,
    @ColumnInfo(name = "vendor") val vendor: String,
    @ColumnInfo(name = "retailer") val retailer: String,
    @ColumnInfo(name = "retailerid") val retailerid: String,
    @ColumnInfo(name = "beatname") val beatname: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "catgroup") val catgroup: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "quantity") val quantity: String,
    @ColumnInfo(name = "unit") val unit: String,
    @ColumnInfo(name = "finalcost") val finalcost: String,
    @ColumnInfo(name = "pocost") val pocost: String,
    @ColumnInfo(name = "actualrowamount") val actualrowamount: String,
    @ColumnInfo(name = "actualgrandtotal") val actualgrandtotal: String,
    @ColumnInfo(name = "totalquantity") val totalquantity: String,
    @ColumnInfo(name = "totweight") val totweight: String,
    @ColumnInfo(name = "adate") val adate: String,
    @ColumnInfo(name = "aempid") val aempid: String,
    @ColumnInfo(name = "aempname") val aempname: String,
    @ColumnInfo(name = "asector") val asector: String,
    @ColumnInfo(name = "warehouse") val warehouse: String,
    @ColumnInfo(name = "addupdated") val addupdated: String,
    @ColumnInfo(name = "updated") val updated: String,
    @ColumnInfo(name = "client") val client: String,
    @ColumnInfo(name = "empname") val empname: String,
    @ColumnInfo(name = "remarks") val remarks: String,
    @ColumnInfo(name = "salesman") val salesman: String,
    @ColumnInfo(name = "salesmanid") val salesmanid: String,
    @ColumnInfo(name = "sflag") val sflag: String,
    @ColumnInfo(name = "cflag") val cflag: String,
    @ColumnInfo(name = "price") val price: String,
    @ColumnInfo(name = "ptd") val ptd: String,
    @ColumnInfo(name = "taxtype") val taxtype: String,
    @ColumnInfo(name = "taxamount") val taxamount: String,
    @ColumnInfo(name = "totaltaxamount") val totaltaxamount: String
)
