package dao

import com.futalk.kmm.YearOptions
import di.database

/**
 * Year opens dao
 * 对可选学年的数据库操作
 * @constructor Create empty Year opens dao
 */
class YearOpensDao(

) {
    /**
     * Clear all year opens
     * 清除可选学年
     */
    private fun clearAllYearOpens(){
        database.yearOptionsQueries.clearAllYearOptions()
    }

    /**
     * 批量插入可选学年
     * @param list List<String>
     */
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

    /**
     * 获取可选学年
     * @return List<YearOptions>
     */
    fun getAllYearOpens(): List<YearOptions> {
        return database.yearOptionsQueries.getAllYearOptions().executeAsList()
    }
}