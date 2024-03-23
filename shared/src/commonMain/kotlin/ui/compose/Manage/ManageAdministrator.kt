package ui.compose.Manage

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import data.share.User
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.koinInject
import util.network.CollectWithContentInBox
import util.network.getAvatarStatic
import util.regex.matchEmail

object ManageAdministratorVoyager : Screen{
    @Composable
    override fun Content() {
        TabNavigator(FeatAdministratorVoyagerScreen){ tabNavigator ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ){
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ){
                    CurrentTab()
                }
                BottomNavigation {
                    BottomNavigationItem(
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
                    BottomNavigationItem(
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
                .fillMaxSize()
                .padding(10.dp)
        ){
            val manageViewModel = koinInject<ManageViewModel>()
            val userEmail = remember {
                mutableStateOf("")
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
                                manageViewModel.getUserDataByEmail(userEmail.value)
                            }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(1f),
                placeholder = {
                    Text("输入邮箱")
                },
                isError = !matchEmail(userEmail.value)
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ){
                manageViewModel.userByEmail.collectAsState().CollectWithContentInBox(
                    success = {
                        FeatAdministratorShowUser(it)
                    },
                    error = {
                        Text(
                            text = it.message.toString(),
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    },
                    loading = {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    },
                    unSend = {
                        Text(
                            "用户未加载",
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    },
                    modifier = Modifier
                        .fillMaxSize()
                )
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
        BottomSheetNavigator(
            modifier = Modifier
                .fillMaxSize()
        ){ bottomSheetNavigator ->
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
            Row (
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(10.dp)
            ){
                Button(
                    onClick = {

                    },
                    modifier = Modifier
                ){
                    Text(
                        "修改等级",
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(10.dp),
                        color = Color.Black
                    )
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
            .fillMaxHeight()
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        PersonalInformationAreaInManage(
            userName = user.username,
            url = getAvatarStatic(user.avatar)
        )
        Text("邮箱:${user.username}")
        Text("所在地:${user.location}")
        Text("年级:${user.gender}")
        Text("年龄:${user.age}")
        Button(
            onClick = {

            }
        ){
            Text("加入审核管理员")
        }
        Button(
            onClick = {

            }
        ){
            Text("加入主要管理员")
        }
    }
}

@Composable
fun PersonalInformationAreaInManage(
    url : String ,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(50.dp),
    userName : String,
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