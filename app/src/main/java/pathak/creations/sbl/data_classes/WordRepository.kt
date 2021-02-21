package pathak.creations.sbl.data_classes

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class WordRepository(private val wordDao: WordDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allWords: Flow<List<Word>> = wordDao.getAlphabetizedWords()
    val allDistributors: Flow<List<Distributor>> = wordDao.getDistributors()
    val allCarts: Flow<List<Cart>> = wordDao.getCartList()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(word: Word) {
        wordDao.insert(word)
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertDist(dist: Distributor) {
        wordDao.insertDist(dist)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertCart(cart: Cart) {
        wordDao.insertCart(cart)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete() {
        wordDao.deleteAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllCart() {
        wordDao.deleteAllCart()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllDist() {
        wordDao.deleteAllDist()
    }
}