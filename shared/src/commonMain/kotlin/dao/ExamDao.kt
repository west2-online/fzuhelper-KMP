package dao

import di.database
import ui.compose.ClassSchedule.ExamBean

class ExamDao {
    private fun clearAllExam(){
        database.examQueries.clearAllExams()
    }

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