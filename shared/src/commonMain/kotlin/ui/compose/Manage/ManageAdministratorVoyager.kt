package ui.compose.Manage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import data.share.User
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import util.network.getAvatarStatic

object ManageAdministratorVoyager : Screen{
    @Composable
    override fun Content() {
        TabNavigator(FeatAdministratorVoyagerScreen){ tabNavigator ->
            Scaffold(
                content = {
                    CurrentScreen()
                },
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            label = {
                                Text(FeatAdministratorVoyagerScreen.options.title)
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                tabNavigator.current = FeatAdministratorVoyagerScreen
                            },
                            selected = tabNavigator.current is FeatAdministratorVoyagerScreen
                        )
                        NavigationBarItem(
                            label = {
                                Text(ManageExistAdministratorVoyagerScreen.options.title)
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                tabNavigator.current = ManageExistAdministratorVoyagerScreen
                            },
                            selected = tabNavigator.current is ManageExistAdministratorVoyagerScreen
                        )
                    }
                }

            )
        }
    }
}

object FeatAdministratorVoyagerScreen:Tab{
    override val options: TabOptions
        @Composable
        get(){
            return TabOptions(
                index = 1u,
                icon = null,
                title = "添加新的管理员"
            )
        }
    @Composable
    override fun Content() {
        Column (
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .padding(10.dp)
        ){
            val userEmail = remember {
                mutableStateOf("")
            }
            val currentUser = remember {
                mutableStateOf<User?>(null)
            }
            TextField(
                value = userEmail.value,
                onValueChange = {
                    userEmail.value = it
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        modifier = Modifier
                            .clickable {

                            }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(1f),
                placeholder = {
                    Text("输入邮箱")
                }
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ){
                Crossfade(
                    modifier = Modifier
                        .fillMaxSize(),
                    targetState = currentUser.value
                ){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ){
                        if(it == null){
                            Text(
                                "用户未加载",
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        } else {
                            FeatAdministratorShowUser(
                                user = it
                            )
                        }
                    }
                }
            }
        }
    }
}

object ManageExistAdministratorVoyagerScreen : Tab{
    override val options: TabOptions
        @Composable
        get(){
            return TabOptions(
                index = 2u,
                icon = null,
                title = "管理已有管理员"
            )
        }
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val userForChangeLevel = remember {
            mutableStateOf<User?>(null)
        }

        BottomSheetNavigator{ bottomSheetNavigator ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        bottomSheetNavigator.show(ChangeUserLevel(user = User(
                            Id = 0,
                            Identify = 0,
                            age = 0,
                            email = "",
                            gender = "",
                            location = "",
                            username = "",
                            avatar = ""
                        )))
                    }
            ){

            }
        }
    }
}


class ChangeUserLevel(
    val user: User
):Screen{
    @Composable
    override fun Content() {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        Column(
            modifier = Modifier
                .fillMaxSize()
        ){
            val selectLevel = remember {
                mutableStateOf(AdministratorLevel.AuditAdministratorLevel)
            }
            Row (
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .wrapContentHeight()
                    .padding(10.dp)
                    .fillMaxWidth()
            ){
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .clickable {
                            bottomSheetNavigator.hide()
                        }
                        .wrapContentSize(Alignment.Center)
                        .fillMaxSize(0.8f)
                )
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ){
                AdministratorLevel.values().forEach { administratorLevel ->
                    item {
                        Card(
                            shape = RoundedCornerShape(10),
                            border = BorderStroke(
                                1.dp,
                                color = animateColorAsState(if (selectLevel.value == administratorLevel) Color.Cyan else Color.Transparent).value
                            ),
                            modifier = Modifier.clickable {
                                selectLevel.value = administratorLevel
                            },
                            backgroundColor = animateColorAsState(if (selectLevel.value == administratorLevel) Color.Cyan else Color.Transparent).value
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = administratorLevel.levelName,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = administratorLevel.describe
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class AdministratorLevel(val describe:String,val levelName:String){
    SuperAdministratorLevel("几乎所有权限","超级管理员"),
    MainAdministratorLevel("了修改管理员等级的其他权限","主要管理员"),
    AuditAdministratorLevel("负责审核的管理员权限","审核管理员")
}

@Composable
fun AdministratorShowUser(
    user: User
){
    val showDetail = remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .animateContentSize()
            .fillMaxWidth()
    ) {
        PersonalInformationAreaInManage(
            userName = user.username,
            url = getAvatarStatic(user.avatar)
        )
        AnimatedVisibility(showDetail.value){
            Column {
                Text("邮箱:${user.username}")
                Text("所在地:${user.location}")
                Text("年级:${user.gender}")
                Text("年龄:${user.age}")
                Row {
                    Button(
                        onClick = {

                        },
                        content = {
                            Text("删除")
                            Text("修改等级")
                        }
                    )
                    Button(
                        onClick = {

                        },
                        content = {
                            Text("删除")
                            Text("修改等级")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FeatAdministratorShowUser(
    user: User
){
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .animateContentSize()
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        PersonalInformationAreaInManage(
            userName = user.username,
            url = getAvatarStatic(user.avatar)
        )
        Text("邮箱:${user.username}")
        Text("所在地:${user.location}")
        Text("年级:${user.gender}")
        Text("年龄:${user.age}")
    }
}

@Composable
fun PersonalInformationAreaInManage(
    url : String = "https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg",
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(50.dp),
    userName : String = "theonenull",
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KamelImage(
            resource = asyncPainterResource(url),
            null,
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .aspectRatio(1f)
                .clip(CircleShape),
            contentScale = ContentScale.FillBounds
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f)
                .wrapContentHeight(),
            text = userName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}