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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.coroutines.launch
import org.example.library.MR
import ui.compose.Action.ActionVoyagerScreen
import ui.compose.Massage.MassageVoyagerScreen
import ui.compose.Person.PersonVoyagerScreen
import ui.compose.Post.PostVoyagerScreen


enum class MainItems(
    val tag : String,
    val unSelectImageVector: ImageVector,
    val selectImageVector: ImageVector,
){
    POST(
        "主页",
        unSelectImageVector = Icons.Outlined.Home,
        selectImageVector = Icons.Filled.Home,
    ),
    ACTION(
        "功能",
        unSelectImageVector = Icons.Outlined.Share,
        selectImageVector = Icons.Filled.Share,
    ),
    MASSAGE(
        "消息",
        unSelectImageVector = Icons.Outlined.Email,
        selectImageVector = Icons.Filled.Email,
    ),

    PERSON(
        "个人",
        unSelectImageVector = Icons.Outlined.Person,
        selectImageVector = Icons.Filled.Person,
    ),
}





object Main : Screen{
    @Composable
    override fun Content() {
        TabNavigator(PostVoyagerScreen) { tabNavigator ->
            val scope = rememberCoroutineScope()
            val scaffoldState = rememberScaffoldState()
            Scaffold(
                content = {
                    Column (
                        modifier = Modifier
                            .fillMaxSize()
                    ){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ){
                            CurrentTab()
                        }
                        BottomNavigation {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(1f)
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
                            BottomPostTab()
                            BottomActionTab()
                            BottomMassageTab()
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


@Composable
fun RowScope.BottomPostTab(){
    val currentTabNavigator = LocalTabNavigator.current
    val item = MainItems.POST
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
        label = { Text(item.tag) },
        selected = currentTabNavigator.current is PostVoyagerScreen,
        onClick = {
            currentTabNavigator.current = PostVoyagerScreen
        },
        selectedContentColor = MaterialTheme.colors.primaryVariant,
        modifier = Modifier
    )
}

@Composable
fun RowScope.BottomActionTab(){
    val currentTabNavigator = LocalTabNavigator.current
    val item = MainItems.ACTION
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
        label = { Text(item.tag) },
        selected = currentTabNavigator.current is ActionVoyagerScreen,
        onClick = {
            currentTabNavigator.current = ActionVoyagerScreen
        },
        selectedContentColor = MaterialTheme.colors.primaryVariant,
        modifier = Modifier
    )
}

@Composable
fun RowScope.BottomMassageTab(){
    val currentTabNavigator = LocalTabNavigator.current
    val item = MainItems.MASSAGE
    this.BottomNavigationItem(
        icon = {
            val imageVector = remember(currentTabNavigator.current) {
                mutableStateOf(
                    if( currentTabNavigator.current is MassageVoyagerScreen){
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
        selected = currentTabNavigator.current is PostVoyagerScreen,
        onClick = {
            currentTabNavigator.current = MassageVoyagerScreen
        },
        selectedContentColor = MaterialTheme.colors.primaryVariant,
        modifier = Modifier
    )
}

@Composable
fun RowScope.BottomPersonTab(){
    val currentTabNavigator = LocalTabNavigator.current
    val item = MainItems.PERSON
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
        label = { Text(item.tag) },
        selected = currentTabNavigator.current is PersonVoyagerScreen,
        onClick = {
            currentTabNavigator.current = PersonVoyagerScreen( modifier = Modifier.fillMaxSize(),null)
        },
        selectedContentColor = MaterialTheme.colors.primaryVariant,
        modifier = Modifier
    )
}




