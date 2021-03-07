package pathak.creations.sbl.data_classes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Query("DELETE FROM retailer_table")
    suspend fun deleteAllRetailer()


    //cart
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCart(cartItem: Cart)

    @Query("SELECT * FROM cart_table ORDER BY cartId ASC ")
    fun getCartList(): Flow<List<Cart>>


    @Query("SELECT * FROM cart_table  WHERE retailer_code = :retailer_code ORDER BY cartId ASC ")
    fun getCartFromDist(retailer_code :String): Flow<List<Cart>>



    @Query("DELETE FROM cart_table")
    suspend fun deleteAllCart()


}