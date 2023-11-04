package ui.compose.Main

import BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ui.compose.Massage.MassageScreen
import ui.compose.New.NewsScreen
import ui.compose.PERSON.PersonalDrawer
import ui.route.Route


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Main(
    route : SnapshotStateList<Route>
){
    BackHandler(true){
        route.remove(route.last())
    }
    Scaffold(
        drawerContent = {
            PersonalDrawer(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 10.dp)
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                var selectedItem by remember { mutableStateOf(0) }
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    initialPageOffsetFraction = 0f
                ) {
                    MainItems.values().size
                }
                LaunchedEffect(selectedItem){
                    pagerState.animateScrollToPage(selectedItem)
                }
                Surface (
                    modifier = Modifier.fillMaxWidth().weight(1f),
                ){
                    HorizontalPager(
                        state = pagerState,
                        userScrollEnabled= false
                    ){
                        MainItems.values()[it].content(this)
                    }
                }
                BottomNavigation{
                    MainItems.values().forEachIndexed { index, item ->
                        BottomNavigationItem(
                            icon = { Icon(item.imageVector, contentDescription = null) },
                            label = { Text(item.tag) },
                            selected = selectedItem == index,
                            onClick = { selectedItem = index }
                        )
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
    )
}

enum class MainItems @OptIn(ExperimentalFoundationApi::class) constructor(
    val tag : String,
    val imageVector: ImageVector,
    val content : @Composable PagerScope.() -> Unit = {}
){
    @OptIn(ExperimentalFoundationApi::class)
    NEWS(
        "主页",
        Icons.Filled.Home,
        content = {
            NewsScreen(
                Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp)
            )
        }
    ),
    @OptIn(ExperimentalFoundationApi::class)
    ACTION(
        "功能",
        Icons.Filled.Share,
        content = {
            NewsScreen(
                Modifier
                    .fillMaxSize()
                    .padding(all = 10.dp)
            )
        }
    ),
    @OptIn(ExperimentalFoundationApi::class)
    MASSAGE(
        "消息",
        Icons.Filled.Email,
        content = {
            MassageScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            )
        }
    ),
    @OptIn(ExperimentalFoundationApi::class)
    PERSON("个人",Icons.Filled.Person),
}

