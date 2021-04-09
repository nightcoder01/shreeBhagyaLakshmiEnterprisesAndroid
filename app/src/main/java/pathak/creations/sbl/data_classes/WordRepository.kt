package pathak.creations.sbl.data_classes

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class WordRepository(private val wordDao: WordDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allWords: Flow<List<Word>> = wordDao.getAlphabetizedWords()

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
    suspend fun delete() {
        wordDao.deleteAll()
    }


    //distributor

    val allDistributors: Flow<List<Distributor>> = wordDao.getDistributors()


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertDist(dist: Distributor) {
        wordDao.insertDist(dist)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllDist() {
        wordDao.deleteAllDist()
    }

    //beat

    val allBeat: Flow<List<Beat>> = wordDao.getBeat()


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertBeat(beat: Beat) {
        wordDao.insertBeat(beat)
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getBeatFromDist(distId: String) : Flow<List<Beat>>{
       return wordDao.getBeatFromDist(distId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllBeat() {
        wordDao.deleteAllBeat()
    }


    //retailer

    val allRetailer: Flow<List<Retailer>> = wordDao.getRetailer()


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertRetailer(retailer: Retailer) {
        wordDao.insertRetailer(retailer)
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getBeatRetailer(beatName: String) : Flow<List<Retailer>>{
        return wordDao.getBeatRetailer(beatName)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllRetailer() {
        wordDao.deleteAllRetailer()
    }



    //retailer

    val allCategories: Flow<List<Categories>> = wordDao.getCategories()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateRetailer(retailer: Retailer) {
        wordDao.updateRetailer(retailer)
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertCategories(categories: Categories) {
        wordDao.insertCategories(categories)
    }




    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllCategories() {
        wordDao.deleteAllCategories()
    }



    //cart

    val allCarts: Flow<List<Cart>> = wordDao.getCartList()


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertCart(cart: Cart) {
        wordDao.insertCart(cart)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateCart(cart: Cart) {
        wordDao.updateCart(cart)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getCartFromDist(retailer_code: String) : Flow<List<Cart>>{
        return wordDao.getCartFromDist(retailer_code)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllCart() {
        wordDao.deleteAllCart()
    }

    //order

    val allOrders: Flow<List<Orders>> = wordDao.getOrdersList()


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertOrders(orders: Orders) {
        wordDao.insertOrders(orders)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateOrders(orders: Orders) {
        wordDao.updateOrders(orders)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getOrdersFromDist(retailer_code: String) : Flow<List<Orders>>{
        return wordDao.getOrdersFromDist(retailer_code)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getOrdersFromRetailer(retailerID: String) : Flow<List<Orders>>{
        return wordDao.getOrdersFromRetailer(retailerID)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllOrders() {
        wordDao.deleteAllOrders()
    }


}