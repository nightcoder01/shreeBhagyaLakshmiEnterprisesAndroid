package pathak.creations.sbl.data_classes

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class WordViewModel(private val repository: WordRepository) : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allWords: LiveData<List<Word>> = repository.allWords.asLiveData()
    val allDistributor: LiveData<List<Distributor>> = repository.allDistributors.asLiveData()
    val allCart: LiveData<List<Cart>> = repository.allCarts.asLiveData()

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */


    fun insert(word: Word) = viewModelScope.launch {
        repository.insert(word)
    }

    fun insertDist(dist: Distributor) = viewModelScope.launch {
        repository.insertDist(dist)
    }

    fun insertCart(cart: Cart) = viewModelScope.launch {
        repository.insertCart(cart)
    }


    fun delete() = viewModelScope.launch {
        repository.delete()
    }

    fun deleteAllDist() = viewModelScope.launch {
        repository.deleteAllDist()
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