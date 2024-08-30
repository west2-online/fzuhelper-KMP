package di

import createDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/** Database 获取底层数据库 */
val database = createDatabase()

internal val job = Job()

/** Global scope 全局可用的协程 */
val globalScope = CoroutineScope(job)
