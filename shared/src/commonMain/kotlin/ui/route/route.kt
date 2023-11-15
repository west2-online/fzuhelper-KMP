package ui.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ui.compose.Authentication.Assembly
import ui.compose.Main.MainScreen
import ui.compose.Massage.MassageScreen
import ui.compose.New.NewsDetail
import ui.compose.Release.ReleasePageScreen
import ui.compose.SplashPage.SplashPage

@Composable
fun RouteHost(
    modifier: Modifier = Modifier,
    route:RouteState
){

    Box(modifier = modifier) {
        route.currentPage.value?.content?.invoke(route.route)
    }
}

interface Route{
    val route: String
    val content : @Composable ( SnapshotStateList<Route> ) -> Unit

    class RouteNewsDetail private constructor(
        val id: String,
        override val route: String,
        override val content : @Composable (
            SnapshotStateList<Route>
        ) -> Unit = {
            NewsDetail()
        }
    ): Route{
        class Builder {
            private var route: String? = null
            private var id : String? = null
            fun setRoute(route: String): Builder {
                this.route = route
                return this
            }
            fun setId(id:String) : Builder {
                this.id = id
                return this
            }
            fun build(): RouteNewsDetail {
                return RouteNewsDetail(
                    route!!,
                    id!!
                )
            }
        }
    }

    class Main (
        val id: String ,
        override val route: String = "Main",
        override val content: @Composable ( SnapshotStateList<Route> ) -> Unit = {
            MainScreen()
        }
    ) : Route


    class ReleasePage private constructor(
        val id: String,
        override val route: String,
        override val content: @Composable ( SnapshotStateList<Route> ) -> Unit = {
            ReleasePageScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
            )
        }
    ) : Route{
        class Builder {
            private var id: String? = null
            private var route: String? = null
            fun setId(id: String): Builder {
                this.id = id
                return this
            }
            fun setRoute(route: String): Builder {
                this.route = route
                return this
            }
            fun build(): ReleasePage {
                return ReleasePage(
                    id!!,
                    route!!
                )
            }
        }
    }

    class Person (
        val isSelf : Boolean = false,
        override val route: String,
        override val content: @Composable ( SnapshotStateList<Route> ) -> Unit = {
//            MainScreen(it)
        }
    ) : Route

    class Massage private constructor(
        override val route: String,
        override val content: @Composable ( SnapshotStateList<Route> ) -> Unit = {
            MassageScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding( all = 10.dp )
            ) {

            }
        }
    ):Route{
        class Builder {
            private var route: String? = null
            fun setRoute(firstName: String): Builder {
                this.route = firstName
                return this
            }
            fun build(): Massage {
                return Massage(
                    route!!,
                )
            }
        }
    }

    class LoginWithRegister(
        override val route: String = "loginWithRegister",
        override val content: @Composable (SnapshotStateList<Route>) -> Unit = {
            Assembly(Modifier.fillMaxSize())
        }
    ):Route

    class Splash(
        override val route: String = "",
        override val content: @Composable (SnapshotStateList<Route>) -> Unit = {
            SplashPage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            )
        }
    ):Route

}

class RouteState(start:Route){
    val route = mutableStateListOf<Route>(start)
    val currentPage = derivedStateOf {
        if(route.isEmpty()){
            return@derivedStateOf null
        }
        route.last()
    }
    private val scope = CoroutineScope(Job())
    private val mutex = Mutex()
    fun back(){
        if(!mutex.isLocked){
            scope.launch(Dispatchers.Default) {
                mutex.withLock {
                    route.removeLast()
                }
            }
        }
    }
    fun navigateWithPop(newRoute: Route){
        route.removeLastOrNull()
        route.add(newRoute)
    }
    fun navigateWithoutPop(newRoute: Route){
        route.add(newRoute)
    }
}








