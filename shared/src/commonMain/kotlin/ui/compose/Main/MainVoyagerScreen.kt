package ui.compose.Main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.coroutines.launch
import org.example.library.MR
import ui.compose.Action.ActionVoyagerScreen
import ui.compose.ClassSchedule.ClassScheduleVoyagerScreen
import ui.compose.Person.PersonVoyagerScreen
import ui.compose.Post.PostVoyagerScreen
import util.compose.BottomNavigationWithBottomPadding
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import kotlin.jvm.Transient

/**
 * 底部栏的四个导航
 * @property tag String
 * @property unSelectImageVector ImageVector
 * @property selectImageVector ImageVector
 * @constructor
 */
enum class MainItems(
    val tag : String,
    val unSelectImageVector: ImageVector,
    val selectImageVector: ImageVector,
){
    CLASS(
        "课表",
        unSelectImageVector = Icons.Outlined.DateRange,
        selectImageVector = Icons.Filled.DateRange,
    ),
    POST(
        "讨论",
        unSelectImageVector = Icons.Outlined.Home,
        selectImageVector = Icons.Filled.Home,
    ),
    ACTION(
        "功能",
        unSelectImageVector = Icons.Outlined.Share,
        selectImageVector = Icons.Filled.Share,
    ),
//    MASSAGE(
//        "消息",
//        unSelectImageVector = Icons.Outlined.Email,
//        selectImageVector = Icons.Filled.Email,
//    ),

    PERSON(
        "个人",
        unSelectImageVector = Icons.Outlined.Person,
        selectImageVector = Icons.Filled.Person,
    ),
}

/**
 * 主要界面
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class MainVoyagerScreen(
    @Transient
    val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) : Screen {
    @Composable
    override fun Content() {
        TabNavigator(ClassScheduleVoyagerScreen(
            parentPaddingControl = ParentPaddingControl(
                parentNavigatorControl = true
            )
        )) {
            val scope = rememberCoroutineScope()
            val scaffoldState = rememberScaffoldState()
            Scaffold(
                drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            CurrentTab()
                        }
                        BottomNavigationWithBottomPadding(
                            modifier = Modifier
                                .fillMaxWidth(),
                            isNavigationBarsPadding = !parentPaddingControl.parentNavigatorControl
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
                                    .padding(10.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        scope.launch {
                                            scaffoldState.drawerState.open()
                                        }
                                    }
                            ) {
                                Image(
                                    painter = painterResource(MR.images.FuTalk),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .align(Alignment.Center)
                                )
                            }
                            BottomClassTab()
                            BottomPostTab()
                            BottomActionTab()
                            BottomPersonTab()
                        }
                    }
                },
                drawerContent = {
                    MainDrawer(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 10.dp),
                    )
                },
                scaffoldState = scaffoldState
            )
        }
    }
}

/**
 * 帖子的跳转
 * @receiver RowScope
 */
@Composable
fun RowScope.BottomPostTab(){
    val currentTabNavigator = LocalTabNavigator.current
    val item = MainItems.POST
    val textDecoration = remember(currentTabNavigator.current) {
        mutableStateOf(
            if( currentTabNavigator.current is PostVoyagerScreen){
                TextDecoration.Underline
            } else{
                TextDecoration.None
            }
        )
    }
    this.BottomNavigationItem(
        icon = {
            val imageVector = remember(currentTabNavigator.current) {
                mutableStateOf(
                    if( currentTabNavigator.current is PostVoyagerScreen ){
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
        label = { Text(item.tag,textDecoration = textDecoration.value) },
        selected = currentTabNavigator.current is PostVoyagerScreen,
        onClick = {
            currentTabNavigator.current = PostVoyagerScreen(
                parentPaddingControl = ParentPaddingControl(
                    parentNavigatorControl = true
                )
            )
        },
        selectedContentColor = MaterialTheme.colors.primaryVariant,
        modifier = Modifier
    )
}

/**
 * 功能页跳转
 * @receiver RowScope
 */
@Composable
fun RowScope.BottomActionTab(){
    val currentTabNavigator = LocalTabNavigator.current
    val item = MainItems.ACTION
    val textDecoration = remember(currentTabNavigator.current) {
        mutableStateOf(
            if( currentTabNavigator.current is ActionVoyagerScreen){
                TextDecoration.Underline
            } else{
                TextDecoration.None
            }
        )
    }
    this.BottomNavigationItem(
        icon = {
            val imageVector = remember(currentTabNavigator.current) {
                mutableStateOf(
                    if( currentTabNavigator.current is ActionVoyagerScreen ){
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
        label = { Text(item.tag,textDecoration = textDecoration.value) },
        selected = currentTabNavigator.current is ActionVoyagerScreen,
        onClick = {
            currentTabNavigator.current = ActionVoyagerScreen(
                ParentPaddingControl(false,true)
            )
        },
        selectedContentColor = MaterialTheme.colors.primaryVariant,
        modifier = Modifier
    )
}

//@Composable
//fun RowScope.BottomMassageTab(){
//    val currentTabNavigator = LocalTabNavigator.current
//    val item = MainItems.Class
//    this.BottomNavigationItem(
//        icon = {
//            val imageVector = remember(currentTabNavigator.current) {
//                mutableStateOf(
//                    if( currentTabNavigator.current is MassageVoyagerScreen){
//                        item.selectImageVector
//                    } else{
//                        item.unSelectImageVector
//                    }
//                )
//            }
//            Crossfade (
//                imageVector.value
//            ){
//                Icon(
//                    it,
//                    contentDescription = null,
//                    modifier = Modifier
//                )
//            }
//        },
//        label = { Text(item.tag) },
//        selected = currentTabNavigator.current is PostVoyagerScreen,
//        onClick = {
//            currentTabNavigator.current = MassageVoyagerScreen(
//                ParentPaddingControl(false,true)
//            )
//        },
//        selectedContentColor = MaterialTheme.colors.primaryVariant,
//        modifier = Modifier
//    )
//}
//

/**
 * 课程跳转
 * @receiver RowScope
 */
@Composable
fun RowScope.BottomClassTab(){
    val currentTabNavigator = LocalTabNavigator.current
    val item = MainItems.CLASS
    val textDecoration = remember(currentTabNavigator.current) {
        mutableStateOf(
            if( currentTabNavigator.current is ClassScheduleVoyagerScreen){
                TextDecoration.Underline
            } else{
                TextDecoration.None
            }
        )
    }
    this.BottomNavigationItem(
        icon = {
            val imageVector = remember(currentTabNavigator.current) {
                mutableStateOf(
                    if( currentTabNavigator.current is ClassScheduleVoyagerScreen){
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
        label = {
            Text(
                item.tag,
                textDecoration = textDecoration.value
            )
        },
        selected = currentTabNavigator.current is ClassScheduleVoyagerScreen,
        onClick = {
            currentTabNavigator.current = ClassScheduleVoyagerScreen(
                parentPaddingControl = ParentPaddingControl(false,true)
            )
        },
        selectedContentColor = MaterialTheme.colors.primaryVariant,
        modifier = Modifier
    )
}

/**
 * 个人界面跳转
 * @receiver RowScope
 */
@Composable
fun RowScope.BottomPersonTab(){
    val currentTabNavigator = LocalTabNavigator.current
    val item = MainItems.PERSON
    val textDecoration = remember(currentTabNavigator.current) {
        mutableStateOf(
            if( currentTabNavigator.current is PersonVoyagerScreen){
                TextDecoration.Underline
            } else{
                TextDecoration.None
            }
        )
    }
    this.BottomNavigationItem(
        icon = {
            val imageVector = remember(currentTabNavigator.current) {
                mutableStateOf(
                    if( currentTabNavigator.current is PersonVoyagerScreen){
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
        label = { Text(item.tag,textDecoration = textDecoration.value) },
        selected = currentTabNavigator.current is PersonVoyagerScreen,
        onClick = {
            currentTabNavigator.current = PersonVoyagerScreen(
                modifier = Modifier.fillMaxSize(),null,
                parentPaddingControl = ParentPaddingControl(false,true)
            )
        },
        selectedContentColor = MaterialTheme.colors.primaryVariant,
        modifier = Modifier
    )
}




