package di

import createDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

val database = createDatabase()

val job = Job()
val globalScope = CoroutineScope(job)