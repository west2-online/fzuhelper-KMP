package dao

import di.database
import repository.CourseBeanForTemp

class ClassScheduleDao {
    fun clearClassSchedule(){
        database.classScheduleQueries.clearCourse()
    }

    fun insertClassSchedule( list : List<CourseBeanForTemp> ){
        database.transaction {
            clearClassSchedule()
            list.forEach {
                it.apply {
                    database.classScheduleQueries.insertCourse(
                        kcName = kcName,
                        kcLocation = kcLocation,
                        kcStartTime = kcStartTime.toLong(),
                        kcEndTime = kcEndTime.toLong(),
                        kcStartWeek = kcStartWeek.toLong(),
                        kcEndWeek = kcEndWeek.toLong(),
                        kcIsDouble = if (kcIsDouble) 1 else 0,
                        kcIsSingle =  if (kcIsSingle) 1 else 0,
                        kcWeekend = kcWeekend.toLong(),
                        kcYear = kcYear.toLong(),
                        kcXuenian = kcXuenian.toLong(),
                        kcNote = kcNote,
                        kcBackgroundId = kcBackgroundId.toLong(),
                        shoukeJihua = shoukeJihua,
                        jiaoxueDagang = jiaoxueDagang,
                        teacher = teacher,
                        priority = priority,
                        type = type.toLong()
                    )
                }

            }
        }

    }

    fun deleteClassScheduleByXq(xq: String, xn: String) {
        database.classScheduleQueries.deleteCourseByXq(xq.toLong(),xn.toLong())
    }
}