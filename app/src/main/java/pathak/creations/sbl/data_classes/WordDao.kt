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




    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDist(dist: Distributor)



    @Query("SELECT * FROM distributor_table ORDER BY dist ASC")
    fun getDistributors(): Flow<List<Distributor>>

    @Query("DELETE FROM distributor_table")
    suspend fun deleteAllDist()


}