package di

import createDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

val database = createDatabase()

internal val job = Job()
val globalScope = CoroutineScope(job)

object CookieUtil {
    var id: String = ""
    fun clear() {
        id = ""
    }
}