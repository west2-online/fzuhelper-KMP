package dao

/**
 * 对数据库操作的封装
 * @property yearOpensDao YearOpensDao
 * @property classScheduleDao ClassScheduleDao
 * @property examDao ExamDao
 * @constructor
 */
class Dao(
    val yearOpensDao : YearOpensDao,
    val classScheduleDao: ClassScheduleDao,
    val examDao: ExamDao
)