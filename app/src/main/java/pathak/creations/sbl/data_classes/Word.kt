package pathak.creations.sbl.data_classes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey






@Entity(tableName = "distributor_table")
 class Distributor(@PrimaryKey @ColumnInfo(name = "dist") val distID : String, @ColumnInfo(name = "distName")val distName : String)


@Entity(tableName = "word_table")
class Word(@PrimaryKey @ColumnInfo(name = "word") val word : String)
