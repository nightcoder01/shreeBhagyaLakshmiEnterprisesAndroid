package pathak.creations.sbl

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import pathak.creations.sbl.data_classes.WordRepository
import pathak.creations.sbl.data_classes.WordRoomDatabase

class AppController : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts

    val applicationScope = CoroutineScope(SupervisorJob())


    val database by lazy { WordRoomDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { WordRepository(database.wordDao()) }
}