package ui.route

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.compose.Authentication.Register
import ui.compose.Massage.MassageScreen
import ui.compose.NEW.NewsDetail
import ui.compose.Release.ReleasePageScreen
import ui.compose.main.MainScreen

@Composable
fun RouteHost(
    back:()->Unit = {},
    modifier: Modifier = Modifier,
    start: Route = Route.Main.Builder()
        .setRoute("massage")
        .setId("massage")
        .build()
){
    val route = remember {
        mutableStateListOf<Route>(
            start
        )
    }
    val currentPage = remember(route){
        derivedStateOf {
            if(route.isEmpty()){
                return@derivedStateOf null
            }
            route.last()
        }
    }
    Box(modifier = modifier) {
        currentPage.value?.content?.invoke(route)
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

    class Main private constructor(
        val id: String,
        override val route: String,
        override val content: @Composable ( SnapshotStateList<Route> ) -> Unit = {
//            MainScreen(it)
//            Ribbon(
//                modifier = Modifier
//                    .padding(10.dp),
//            )
//            Box(
//                modifier = Modifier.fillMaxSize()
//                    .loadAction()
//            ){
//
//            }
//            SplashPage(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(10.dp)
//            )
            Register(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            )
        }
    ) : Route{
        class Builder {
            private var id:String? = null
            private var route : String? = null
            fun setId(id: String): Builder {
                this.id = id
                return this
            }
            fun setRoute(route: String): Builder {
                this.route = route
                return this
            }
            fun build(): Main {
                return Main(
                    id!!,
                    route!!
                )
            }
        }
    }

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

    class Person private constructor(
        val isSelf : Boolean = false,
        override val route: String,
        override val content: @Composable ( SnapshotStateList<Route> ) -> Unit = {
            MainScreen(it)
        }
    ) : Route{
        class Builder {
            private val isSelf : Boolean? = null
            private var route: String? = null
            fun setRoute(route: String): Builder {
                this.route = route
                return this
            }
            fun build(): Person {
                return Person(
                    isSelf!!,
                    route!!
                )
            }
        }
    }

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

}






