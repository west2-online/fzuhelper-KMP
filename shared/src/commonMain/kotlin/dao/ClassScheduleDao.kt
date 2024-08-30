package dao

import di.database
import repository.CourseBeanForTemp

/**
 * Class schedule dao 设置与学校课程相关的数据库信息
 *
 * @constructor Create empty Class schedule dao
 */
class ClassScheduleDao {
  //    private fun clearClassSchedule(){
  //        database.classScheduleQueries.clearCourse()
  //    }
  /**
   * 根据学年和学期来删除课程
   *
   * @param kcXuenian Int
   * @param kcYear Int
   */
  private fun clearClassScheduleByXueNian(kcXuenian: Int, kcYear: Int) {
    database.classScheduleQueries.deleteCourseByXq(
      kcXuenian = kcXuenian.toLong(),
      kcYear = kcYear.toLong(),
    )
  }

  /**
   * 插入课程
   *
   * @param list List<CourseBeanForTemp>
   * @param kcXuenian Int
   * @param kcYear Int
   */
  fun insertClassScheduleByXueNian(list: List<CourseBeanForTemp>, kcXuenian: Int, kcYear: Int) {
    database.transaction {
      clearClassScheduleByXueNian(kcXuenian = kcXuenian, kcYear = kcYear)
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
            kcIsSingle = if (kcIsSingle) 1 else 0,
            kcWeekend = kcWeekend.toLong(),
            kcYear = kcYear.toLong(),
            kcXuenian = kcXuenian.toLong(),
            kcNote = kcNote,
            kcBackgroundId = kcBackgroundId.toLong(),
            shoukeJihua = shoukeJihua,
            jiaoxueDagang = jiaoxueDagang,
            teacher = teacher,
            priority = priority,
            type = type.toLong(),
          )
        }
      }
    }
  }
}
