package dao

import di.database
import ui.compose.ClassSchedule.ClassScheduleViewModel

class ExamDao {
    private fun clearAllExam(){
        database.examQueries.clearAllExams()
    }

    fun insertExam(list: List<ClassScheduleViewModel.ExamBean>){
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