package ui.route

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import main.MainScreen
import ui.NewsDetail

@Composable
fun RouteHost(
    back:()->Unit = {},
    modifier: Modifier = Modifier
){
    val route = remember {
        mutableStateListOf<Route>(Route.Main(  "1",""))
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
    data class RouteNewsDetail(
        val id: String,
        override val route: String,
        override val content : @Composable (
            SnapshotStateList<Route>
        ) -> Unit = {
            NewsDetail()
        }
    ): Route

    data class Main(
        val id: String,
        override val route: String,
        override val content: @Composable ( SnapshotStateList<Route> ) -> Unit = {
            MainScreen(it)
        }
    ) : Route

    data class Person(
        val isSelf : Boolean = false,
        override val route: String,
        override val content: @Composable ( SnapshotStateList<Route> ) -> Unit = {
            MainScreen(it)
        }
    ) : Route
}






