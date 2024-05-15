package dao

import com.futalk.kmm.YearOptions
import di.database

class YearOpensDao(

) {
    private fun clearAllYearOpens(){
        database.yearOptionsQueries.clearAllYearOptions()
    }
    fun insertYearOpens(
        list: List<String>
    ){
        database.yearOptionsQueries.transaction {
            clearAllYearOpens()
            list.forEach {
                database.yearOptionsQueries.insertYearOptions(it)
            }
        }
    }
    fun getAllYearOpens(): List<YearOptions> {
        return database.yearOptionsQueries.getAllYearOptions().executeAsList()
    }
}