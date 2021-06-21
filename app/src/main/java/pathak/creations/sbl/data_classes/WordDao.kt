package pathak.creations.sbl.data_classes

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface WordDao {

    /*@Query("SELECT * FROM word_table ORDER BY word ASC")
    fun getAlphabetizedWords(): List<Word>*/

    @Query("SELECT * FROM word_table ORDER BY word ASC")
    fun getAlphabetizedWords(): Flow<List<Word>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(word: Word)

    @Query("DELETE FROM word_table")
    suspend fun deleteAll()




    //distributor
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDist(dist: Distributor)

    @Query("SELECT * FROM distributor_table ORDER BY dist ASC")
    fun getDistributors(): Flow<List<Distributor>>

    @Query("DELETE FROM distributor_table")
    suspend fun deleteAllDist()

    //beat
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBeat(beat: Beat)

    @Query("SELECT * FROM beat_table ORDER BY beat_id ASC")
    fun getBeat(): Flow<List<Beat>>

    @Query("SELECT * FROM beat_table  WHERE dist_id = :distId ORDER BY beat_id ASC ")
    fun getBeatFromDist(distId :String): Flow<List<Beat>>

    @Query("DELETE FROM beat_table")
    suspend fun deleteAllBeat()

    //retailer
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRetailer(retailer: Retailer)

    @Query("SELECT * FROM retailer_table ORDER BY retailer_table_id ASC")
    fun getRetailer(): Flow<List<Retailer>>

    @Query("SELECT * FROM retailer_table  WHERE beatname = :beatName  ORDER BY retailer_table_id ASC ")
    fun getBeatRetailer(beatName :String): Flow<List<Retailer>>


    @Update
    suspend fun updateRetailer(retailerItem: Retailer)


    @Query("UPDATE retailer_table SET todayDone =:retailerDone  WHERE retailer_name = :retailerName")
    suspend fun updateRetailerColor(retailerName: String,retailerDone: Boolean)

    @Query("UPDATE retailer_table SET mobile =:retailerNumber , address =:retailerAddress   WHERE retailer_name = :retailerName")
    suspend fun updateRetailerNumber(retailerName: String,retailerNumber: String,retailerAddress: String)

    @Query("DELETE FROM retailer_table")
    suspend fun deleteAllRetailer()


    //retailer
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategories(categories: Categories)

    @Query("SELECT * FROM categories_table ORDER BY categories_table_id ASC")
    fun getCategories(): Flow<List<Categories>>



    @Query("DELETE FROM categories_table")
    suspend fun deleteAllCategories()


    //cart
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCart(cartItem: Cart)


    @Update
    suspend fun updateCart(cartItem: Cart)

    @Query("SELECT * FROM cart_table ORDER BY cartId ASC ")
    fun getCartList(): Flow<List<Cart>>


    @Query("SELECT * FROM cart_table  WHERE retailer_code = :retailer_code ORDER BY cartId ASC ")
    fun getCartFromDist(retailer_code :String): Flow<List<Cart>>



    @Query("DELETE FROM cart_table")
    suspend fun deleteAllCart()

    @Query("DELETE  FROM cart_table WHERE cartId = :carId")
    suspend fun deleteCart(carId :Int)

    //@Delete()
    //void delete(Details details);


    //order
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrders(orders: Orders)


    @Update
    suspend fun updateOrders(orders: Orders)

    @Query("SELECT * FROM order_table ORDER BY id ASC ")
    fun getOrdersList(): Flow<List<Orders>>


    @Query("SELECT * FROM order_table  WHERE vendorid = :vendorId ORDER BY id ASC ")
    fun getOrdersFromDist(vendorId :String): Flow<List<Orders>>

    @Query("SELECT * FROM order_table  WHERE retailerid = :retailerId ORDER BY id ASC ")
    fun getOrdersFromRetailer(retailerId :String): Flow<List<Orders>>

    @Query("SELECT * FROM order_table  WHERE invoice = :transactionNo ORDER BY id ASC ")
    fun getOrdersFromTransaction(transactionNo :String): Flow<List<Orders>>



    @Query("DELETE FROM order_table")
    suspend fun deleteAllOrders()



    //transactions
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransactions(trans: Transactions)


    @Update
    suspend fun updateTransactions(trans: Transactions)

    @Query("SELECT * FROM transaction_table ORDER BY id ASC ")
    fun getTransactionsList(): Flow<List<Transactions>>


    @Query("SELECT * FROM transaction_table  WHERE distributorID = :vendorId ORDER BY id ASC ")
    fun getTransactionsFromDist(vendorId :String): Flow<List<Transactions>>

    @Query("SELECT * FROM transaction_table  WHERE retailerId = :retailerId ORDER BY id ASC ")
    fun getTransactionsFromRetailer(retailerId :String): Flow<List<Transactions>>



    @Query("DELETE FROM transaction_table")
    suspend fun deleteAllTransactions()


}