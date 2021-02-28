package pathak.creations.sbl.data_classes

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import pathak.creations.sbl.dashboard.ui.cart.MyCart
import pathak.creations.sbl.interfaces.DataChangeListener

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

    fun deleteAllRetailer() = viewModelScope.launch {
        repository.deleteAllRetailer()
    }

    //cart

    val allCart: LiveData<List<Cart>> = repository.allCarts.asLiveData()


    fun getCartFromDist(distID: String, listener: MyCart) {
        viewModelScope.launch {
            listener.DataChange(repository.getCartFromDist(distID).asLiveData())
        }
    }


    fun insertCart(cart: Cart) = viewModelScope.launch {
        repository.insertCart(cart)
    }

    fun deleteAllCart() = viewModelScope.launch {
        repository.deleteAllCart()
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