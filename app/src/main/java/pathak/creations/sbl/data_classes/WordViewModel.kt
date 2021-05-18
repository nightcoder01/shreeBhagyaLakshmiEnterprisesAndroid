package pathak.creations.sbl.data_classes

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import pathak.creations.sbl.interfaces.*

class WordViewModel(private val repository: WordRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allWords: LiveData<List<Word>> = repository.allWords.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */


    fun insert(word: Word) = viewModelScope.launch {
        repository.insert(word)
    }

    fun delete() = viewModelScope.launch {
        repository.delete()
    }


    //distributor

    val allDistributor: LiveData<List<Distributor>> = repository.allDistributors.asLiveData()


    fun insertDist(dist: Distributor) = viewModelScope.launch {
        repository.insertDist(dist)
    }

    fun deleteAllDist() = viewModelScope.launch {
        repository.deleteAllDist()
    }

    //beat

    val allBeat: LiveData<List<Beat>> = repository.allBeat.asLiveData()

    fun insertBeat(beat: Beat) = viewModelScope.launch {
        repository.insertBeat(beat)
    }
    fun getBeatFromDist(distID: String, listener : DataChangeListener<LiveData<List<Beat>>>) {
         viewModelScope.launch {
          listener.DataChange(repository.getBeatFromDist(distID).asLiveData())
      }
    }

    fun deleteAllBeat() = viewModelScope.launch {
        repository.deleteAllBeat()
    }

    //retailer

    val allRetailer: LiveData<List<Retailer>> = repository.allRetailer.asLiveData()

    fun insertRetailer(retailer: Retailer) = viewModelScope.launch {
        repository.insertRetailer(retailer)
    }

    fun getBeatRetailer(beatName: String, listener : RetailerDataChangeListener<LiveData<List<Retailer>>>) {
        viewModelScope.launch {
            listener.RetailerDataChange(repository.getBeatRetailer(beatName).asLiveData())
        }
    }


    fun updateRetailer(retailer: Retailer) = viewModelScope.launch {
        repository.updateRetailer(retailer)
    }

    fun updateRetailerColor(retailerName: String,retailerDone: Boolean) = viewModelScope.launch {
        repository.updateRetailerColor(retailerName,retailerDone)
    }


    fun deleteAllRetailer() = viewModelScope.launch {
        repository.deleteAllRetailer()
    }

    //Categories

    val allCategories: LiveData<List<Categories>> = repository.allCategories.asLiveData()

    fun insertCategories(categories: Categories) = viewModelScope.launch {
        repository.insertCategories(categories)
    }




    fun deleteAllCategories() = viewModelScope.launch {
        repository.deleteAllCategories()
    }

    //cart

    val allCart: LiveData<List<Cart>> = repository.allCarts.asLiveData()


    fun getCartFromDist(retailer_code: String, listener: CartDataChangeListener<LiveData<List<Cart>>>) {
        viewModelScope.launch {
            listener.CartDataChange(repository.getCartFromDist(retailer_code).asLiveData())
        }
    }


    fun insertCart(cart: Cart) = viewModelScope.launch {

        repository.insertCart(cart)
    }

    fun updateCart(cart: Cart) = viewModelScope.launch {
        repository.updateCart(cart)
    }

    fun deleteAllCart() = viewModelScope.launch {
        repository.deleteAllCart()
    }

    fun deleteCart(cartId: Int) = viewModelScope.launch {
        repository.deleteCart(cartId)
    }

    //order

    val allOrders: LiveData<List<Orders>> = repository.allOrders.asLiveData()


    fun getOrdersFromDist(retailer_code: String, listener: OrderDataChangeListener<LiveData<List<Orders>>>) {
        viewModelScope.launch {
            listener.OrderDataChange(repository.getOrdersFromDist(retailer_code).asLiveData())
        }
    }

    fun getOrdersFromRetailer(retailerId: String, listener: OrderDataChangeListener<LiveData<List<Orders>>>) {
        viewModelScope.launch {
            listener.OrderDataChange(repository.getOrdersFromRetailer(retailerId).asLiveData())
        }
    }

    fun getOrdersFromTransaction(transactionNo: String, listener: OrderDataChangeListener<LiveData<List<Orders>>>) {
        viewModelScope.launch {
            listener.OrderDataChange(repository.getOrdersFromTransaction(transactionNo).asLiveData())
        }
    }

    fun insertOrders(cart: Orders) = viewModelScope.launch {
        repository.insertOrders(cart)
    }

    fun updateOrders(cart: Orders) = viewModelScope.launch {
        repository.updateOrders(cart)
    }

    fun deleteAllOrders() = viewModelScope.launch {
        repository.deleteAllOrders()
    }

    //transactions

    val allTransactions: LiveData<List<Transactions>> = repository.allTransactions.asLiveData()


    fun getTransactionsFromDist(retailer_code: String, listener: TransactionsDataChangeListener<LiveData<List<Transactions>>>) {
        viewModelScope.launch {
            listener.TransactionDataChange(repository.getTransactionsFromDist(retailer_code).asLiveData())
        }
    }

    fun getTransactionsFromRetailer(retailerId: String, listener: TransactionsDataChangeListener<LiveData<List<Transactions>>>) {
        viewModelScope.launch {
            listener.TransactionDataChange(repository.getTransactionsFromRetailer(retailerId).asLiveData())
        }
    }

    fun insertTransactions(transactions: Transactions) = viewModelScope.launch {
        repository.insertTransactions(transactions)
    }

    fun updateTransactions(transactions: Transactions) = viewModelScope.launch {
        repository.updateTransactions(transactions)
    }

    fun deleteAllTransactions() = viewModelScope.launch {
        repository.deleteAllTransactions()
    }

}

class WordViewModelFactory(private val repository: WordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}