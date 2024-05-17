package dao

import di.database
import ui.compose.ClassSchedule.ExamBean

/**
 * Exam dao
 * 考试的数据库相关操作
 * @constructor Create empty Exam dao
 */
class ExamDao {
    /**
     * Clear all exam
     * 删除所有的考试
     *
     */
    private fun clearAllExam(){
        database.examQueries.clearAllExams()
    }

    /**
     * 批量插入考试
     * @param list List<ExamBean>
     */
    fun insertExam(list: List<ExamBean>){
        clearAllExam()
        database.examQueries.transaction {
            list.forEach {
                it.apply {
                    database.examQueries.insertNewExams(
                        name = name,
                        xuefen = xuefen,
                        teacher = teacher,
                        address = address,
                        zuohao = zuohao,
                    )
                }
            }
        }

    }
}