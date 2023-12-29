package ui.compose.Main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.zip
import ui.compose.Massage.MassageScreen
import ui.compose.Person.PersonScreen
import ui.compose.Person.PersonalDrawer
import ui.compose.Post.NewScreen


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(){
    val visibility = remember {
        mutableStateOf(true)
    }
    Scaffold(
        drawerContent = {
            PersonalDrawer(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 10.dp),
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
                Box (
                    modifier = Modifier.fillMaxWidth().weight(1f).animateContentSize(),
                ){
                    HorizontalPager(
                        state = pagerState,
                        userScrollEnabled= false
                    ){
                        val scope = this
                       Box(
                           modifier = Modifier
                               .fillMaxSize()
                       ){
                           MainItems.values()[it].content(scope,visibility)
                       }
                    }
                }

                BottomNavigation(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                ) {
                    MainItems.values().forEachIndexed { index, item ->
                        BottomNavigationItem(
                            icon = {
                                val imageVector = remember(selectedItem ) {
                                    mutableStateOf(
                                        if(selectedItem == index){
                                             item.selectImageVector
                                        } else{
                                             item.unSelectImageVector
                                        }
                                    )
                                }
                                Crossfade (
                                    imageVector.value
                                ){
                                    Icon(
                                        it,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .drawWithContent {
                                                drawContent()
                                            }
                                    )
                                }

                            },
                            label = { Text(item.tag) },
                            selected = selectedItem == index,
                            onClick = {
                                selectedItem = index
                                visibility.value = true
                            },
                            selectedContentColor = MaterialTheme.colors.primaryVariant,
                            modifier = Modifier
                                .animateContentSize()
                                .then(
                                    if(selectedItem == index){
                                        Modifier.weight(1f)
                                    }
                                    else{
                                        Modifier.width(50.dp)
                                    }
                                )
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
    val unSelectImageVector: ImageVector,
    val selectImageVector: ImageVector,
    val content : @Composable PagerScope.(MutableState<Boolean>) -> Unit = {}
){
    @OptIn(ExperimentalFoundationApi::class)
    NEWS(
        "主页",
        unSelectImageVector = Icons.Outlined.Home,
        selectImageVector = Icons.Filled.Home,
        content = {showState ->
            Box{
                val lazyListState = androidx.compose.foundation.lazy.rememberLazyListState()
                val first = androidx.compose.runtime.mutableStateOf(
                    Pair(
                        lazyListState.firstVisibleItemIndex,
                        lazyListState.firstVisibleItemScrollOffset
                    )
                )

                DisposableEffect(Unit) {
                    first.value =  Pair(lazyListState.firstVisibleItemIndex,lazyListState.firstVisibleItemScrollOffset)
                    onDispose {
                        showState.value = true
                    }
                }

                NewScreen(
                    Modifier
                        .fillMaxSize()
                        .padding(vertical = 10.dp),
                    state = lazyListState
                )

                LaunchedEffect(lazyListState) {
                    snapshotFlow { lazyListState.firstVisibleItemIndex }
                        .zip(snapshotFlow { lazyListState.firstVisibleItemScrollOffset }) { index, offset ->
                            Pair(index, offset)
                        }
                        .collect {
                            val used = first.value
                            if (used != it) {
                                first.value = it
                            }
                            showState.value =
                                used.first > it.first || (used.first == it.first && used.second > it.second)
                        }
                }
            }
        }
    ),
    @OptIn(ExperimentalFoundationApi::class)
    ACTION(
        "功能",
        unSelectImageVector = Icons.Outlined.Share,
        selectImageVector = Icons.Filled.Share,
        content = {showState ->
            Box {
                DisposableEffect(Unit) {
                    showState.value = true
                    onDispose {
                        showState.value = true
                    }
                }
                ui.compose.Ribbon.Ribbon(
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )
            }
        }
    ),
    @OptIn(ExperimentalFoundationApi::class)
    MASSAGE(
        "消息",
        unSelectImageVector = Icons.Outlined.Email,
        selectImageVector = Icons.Filled.Email,
        content = {showState ->
            Box {
                DisposableEffect(Unit) {
                    showState.value = true
                    onDispose {
                        showState.value = true
                    }
                }
                MassageScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp)
                )
            }
        }
    ),
    @OptIn(ExperimentalFoundationApi::class)
    PERSON(
        "个人",
        unSelectImageVector = Icons.Outlined.Person,
        selectImageVector = Icons.Filled.Person,
        content = {
            PersonScreen()
        }
    ),
}

