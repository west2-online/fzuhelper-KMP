package ui.route

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun Route(
    back:()->Unit
){
    val route = rememberSaveable {
        mutableStateListOf<Route>()
    }
    val currentPage = derivedStateOf {
        route.last()
    }
 
}

interface Route{
    val route: String
    data class RouteNewsDetail(
        val name: String,
        override val route: String
    ) : Route
}






