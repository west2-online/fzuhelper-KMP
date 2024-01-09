package ui.compose.Main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.replace
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.coroutines.launch
import org.example.library.MR
import ui.compose.Massage.MassageRouteNode
import ui.compose.Person.PersonRouteNode
import ui.compose.Post.PostRouteTarget
import ui.compose.Ribbon.RibbonRouteNode


enum class MainItems(
    val tag : String,
    val unSelectImageVector: ImageVector,
    val selectImageVector: ImageVector,
    val correspondingNav:MainNav
){
    POST(
        "主页",
        unSelectImageVector = Icons.Outlined.Home,
        selectImageVector = Icons.Filled.Home,
        correspondingNav = MainNav.Post
    ),
    ACTION(
        "功能",
        unSelectImageVector = Icons.Outlined.Share,
        selectImageVector = Icons.Filled.Share,
        correspondingNav = MainNav.Action
    ),
    MASSAGE(
        "消息",
        unSelectImageVector = Icons.Outlined.Email,
        selectImageVector = Icons.Filled.Email,
        correspondingNav = MainNav.Massage
    ),

    PERSON(
        "个人",
        unSelectImageVector = Icons.Outlined.Person,
        selectImageVector = Icons.Filled.Person,
        correspondingNav = MainNav.Person
    ),
}

sealed class MainNav : Parcelable{

    @Parcelize
    data object Person : MainNav()

    @Parcelize
    data object Massage : MainNav()

    @Parcelize
    data object Action : MainNav()

    @Parcelize
    data object Post : MainNav()

}

class MainRouteNode(
    buildContext: BuildContext,
    private val backStack: BackStack<MainNav> = BackStack(
        model = BackStackModel(
            initialTarget = MainNav.Post,
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = { BackStackFader(it) }
    )
): ParentNode<MainNav>(
    buildContext = buildContext,
    appyxComponent = backStack
) {
    override fun resolve(interactionTarget: MainNav, buildContext: BuildContext): Node = when(interactionTarget){
        MainNav.Post -> PostRouteTarget(buildContext)
        MainNav.Massage -> MassageRouteNode(buildContext)
        MainNav.Action -> RibbonRouteNode(buildContext)
        MainNav.Person -> PersonRouteNode(buildContext)
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun View(modifier: Modifier) {
        val scaffoldState = rememberScaffoldState()
        val scope = rememberCoroutineScope()
        Scaffold(
            scaffoldState = scaffoldState,
            drawerContent = {
                MainDrawer(
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
                    var selectedItem by rememberSaveable { mutableStateOf(0) }
                    val pagerState = rememberPagerState(
                        initialPage = 0,
                        initialPageOffsetFraction = 0f
                    ) {
                        MainItems.values().size
                    }
                    LaunchedEffect(selectedItem){
                        pagerState.animateScrollToPage(selectedItem)
                    }
                    AppyxComponent(
                        appyxComponent = backStack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    BottomNavigation(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable{
                                    scope.launch {
                                        scaffoldState.drawerState.open()
                                    }
                                }
                        ){
                            Image(
                                painter = painterResource(MR.images.FuTalk),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .align(Alignment.Center)
                            )
                        }

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
                                        )
                                    }
                                },
                                label = { Text(item.tag) },
                                selected = selectedItem == index,
                                onClick = {
                                    selectedItem = index
                                    backStack.replace(item.correspondingNav)
                                },
                                selectedContentColor = MaterialTheme.colors.primaryVariant,
                                modifier = Modifier
                            )
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
        )
    }
}
