package ui.compose.Manage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import util.compose.EasyToast
import util.compose.rememberToastState
import util.compose.toastBindNetworkResult
import util.network.CollectWithContentInBox
import util.network.getAvatarStatic
import util.regex.matchEmail

/** 审核管理员 */
const val AuditManage = 0
/** 主要管理员 */
const val MainManage = 1
/** 超级管理员 只允许给开发者 */
const val SuperManage = 2

/**
 * 管理管理员界面
 *
 * @constructor Create empty Manage administrator voyager
 */
object ManageAdministratorVoyager : Screen {
  @Composable
  override fun Content() {
    TabNavigator(FeatAdministratorVoyagerScreen) { tabNavigator ->
      Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
          val toastState = rememberToastState()
          val manageViewModel = koinInject<ManageViewModel>()
          toastState.toastBindNetworkResult(
            manageViewModel.adminAdd.collectAsState(),
            manageViewModel.adminLevelUpdate.collectAsState(),
          )
          CurrentTab()
          EasyToast(toastState)
        }
        BottomNavigation {
          BottomNavigationItem(
            label = { Text(FeatAdministratorVoyagerScreen.options.title) },
            icon = { Icon(imageVector = Icons.Filled.Add, contentDescription = null) },
            onClick = { tabNavigator.current = FeatAdministratorVoyagerScreen },
            selected = tabNavigator.current is FeatAdministratorVoyagerScreen,
          )
          BottomNavigationItem(
            label = { Text(ManageExistAdministratorVoyagerScreen.options.title) },
            icon = { Icon(imageVector = Icons.Filled.Person, contentDescription = null) },
            onClick = { tabNavigator.current = ManageExistAdministratorVoyagerScreen },
            selected = tabNavigator.current is ManageExistAdministratorVoyagerScreen,
          )
        }
      }
    }
  }
}

/**
 * 添加管理员界面
 *
 * @constructor Create empty Feat administrator voyager screen
 */
object FeatAdministratorVoyagerScreen : Tab {
  override val options: TabOptions
    @Composable
    get() {
      return TabOptions(index = 1u, icon = null, title = "添加新的管理员")
    }

  @Composable
  override fun Content() {
    Column(
      verticalArrangement = Arrangement.spacedBy(20.dp),
      modifier = Modifier.fillMaxSize().padding(10.dp),
    ) {
      val manageViewModel = koinInject<ManageViewModel>()
      val userEmail = remember { mutableStateOf("") }
      TextField(
        value = userEmail.value,
        onValueChange = { userEmail.value = it },
        trailingIcon = {
          Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            modifier = Modifier.clickable { manageViewModel.getUserDataByEmail(userEmail.value) },
          )
        },
        modifier = Modifier.fillMaxWidth(1f),
        placeholder = { Text("输入邮箱") },
        isError = !matchEmail(userEmail.value),
      )
      Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
        manageViewModel.userByEmail
          .collectAsState()
          .CollectWithContentInBox(
            success = { FeatAdministratorShowUser(it) },
            error = {
              Text(text = it.message.toString(), modifier = Modifier.align(Alignment.Center))
            },
            loading = { CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) },
            unSend = { Text("用户未加载", modifier = Modifier.align(Alignment.Center)) },
            modifier = Modifier.fillMaxSize(),
          )
      }
    }
  }
}

/**
 * 管理现有管理员界面
 *
 * @constructor Create empty Manage exist administrator voyager screen
 */
object ManageExistAdministratorVoyagerScreen : Tab {
  override val options: TabOptions
    @Composable
    get() {
      return TabOptions(index = 2u, icon = null, title = "管理已有管理员")
    }

  @OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
  @Composable
  override fun Content() {
    val userForChangeLevel = remember { mutableStateOf<User?>(null) }
    val manageViewModel = koinInject<ManageViewModel>()
    LaunchedEffect(Unit) { manageViewModel.refreshAdminList() }
    val adminList = manageViewModel.adminList.collectAsState()
    BottomSheetNavigator(modifier = Modifier.fillMaxSize()) { bottomSheetNavigator ->
      adminList.CollectWithContentInBox(
        success = {
          LazyColumn(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
          ) {
            stickyHeader {
              TopAppBar(contentPadding = PaddingValues(start = 10.dp)) { Text("超级管理员") }
            }
            items(it.filter { it.Level == SuperManage }) {
              AdministratorShowUser(
                it.User,
                changeUserLevel = { bottomSheetNavigator.show(ChangeUserLevel(user = it)) },
              )
            }
            stickyHeader {
              TopAppBar(contentPadding = PaddingValues(start = 10.dp)) { Text("主要管理员") }
            }
            items(it.filter { it.Level == MainManage }) {
              AdministratorShowUser(
                it.User,
                changeUserLevel = { bottomSheetNavigator.show(ChangeUserLevel(user = it)) },
              )
            }
            stickyHeader {
              TopAppBar(contentPadding = PaddingValues(start = 10.dp)) { Text("审核管理员") }
            }
            items(it.filter { it.Level == AuditManage }) {
              AdministratorShowUser(
                it.User,
                changeUserLevel = { bottomSheetNavigator.show(ChangeUserLevel(user = it)) },
              )
            }
          }
        },
        error = { Text("加载失败", modifier = Modifier.align(Alignment.Center)) },
        loading = { CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) },
      )
    }
  }
}

/**
 * @property user User
 * @constructor
 */
class ChangeUserLevel(val user: User) : Screen {
  @Composable
  override fun Content() {
    val bottomSheetNavigator = LocalBottomSheetNavigator.current
    Column(modifier = Modifier.fillMaxSize()) {
      val manageViewModel = koinInject<ManageViewModel>()
      val selectLevel = remember { mutableStateOf(AdministratorLevel.AuditAdministratorLevel) }
      Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.wrapContentHeight().padding(10.dp).fillMaxWidth(),
      ) {
        Icon(
          imageVector = Icons.Filled.Close,
          contentDescription = null,
          modifier =
            Modifier.size(30.dp)
              .clip(CircleShape)
              .clickable { bottomSheetNavigator.hide() }
              .wrapContentSize(Alignment.Center)
              .fillMaxSize(0.8f),
        )
      }

      LazyColumn(
        modifier = Modifier.weight(1f).fillMaxWidth().padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        AdministratorLevel.values().forEach { administratorLevel ->
          item {
            Card(
              shape = RoundedCornerShape(10),
              border =
                BorderStroke(
                  1.dp,
                  color =
                    animateColorAsState(
                        if (selectLevel.value == administratorLevel) Color.Cyan
                        else Color.Transparent
                      )
                      .value,
                ),
              modifier = Modifier.clickable { selectLevel.value = administratorLevel },
              backgroundColor =
                animateColorAsState(
                    if (selectLevel.value == administratorLevel) Color.Cyan else Color.Transparent
                  )
                  .value,
            ) {
              Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
              ) {
                Text(text = administratorLevel.levelName, fontSize = 20.sp)
                Text(text = administratorLevel.describe)
              }
            }
          }
        }
      }
      Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.wrapContentHeight().fillMaxWidth().padding(10.dp),
      ) {
        Button(
          onClick = {
            manageViewModel.adminLevelUpdate(userId = user.Id, level = selectLevel.value.level)
          },
          modifier = Modifier,
        ) {
          Text("修改等级", modifier = Modifier.wrapContentHeight().padding(10.dp), color = Color.Black)
        }
      }
    }
  }
}

enum class AdministratorLevel(val describe: String, val levelName: String, val level: Int) {
  SuperAdministratorLevel("几乎所有权限", "超级管理员", SuperManage),
  MainAdministratorLevel("了修改管理员等级的其他权限", "主要管理员", MainManage),
  AuditAdministratorLevel("负责审核的管理员权限", "审核管理员", AuditManage),
}

/**
 * 管理员显示用户
 *
 * @param user User
 * @param changeUserLevel Function1<User, Unit>
 */
@Composable
fun AdministratorShowUser(user: User, changeUserLevel: (User) -> Unit) {
  val showDetail = remember { mutableStateOf(false) }
  Column(modifier = Modifier.wrapContentHeight().animateContentSize().fillMaxWidth()) {
    PersonalInformationAreaInManage(
      userName = user.username,
      url = getAvatarStatic(user.avatar),
      clickShowDetail = { showDetail.value = !showDetail.value },
    )
    AnimatedVisibility(showDetail.value) {
      Column {
        Text("邮箱:${user.email}")
        Text("所在地:${user.location}")
        Text("年级:${user.gender}")
        Text("年龄:${user.age}")
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
          Button(onClick = { changeUserLevel.invoke(user) }, content = { Text("修改等级") })
          Button(onClick = {}, content = { Text("删除") })
        }
      }
    }
  }
}

/**
 * 要添加的管理员信息预览
 *
 * @param user User
 */
@Composable
fun FeatAdministratorShowUser(user: User) {
  val manageViewModel = koinInject<ManageViewModel>()
  Column(
    modifier =
      Modifier.fillMaxHeight().fillMaxWidth().verticalScroll(rememberScrollState()).padding(10.dp),
    verticalArrangement = Arrangement.spacedBy(20.dp),
  ) {
    PersonalInformationAreaInManage(url = getAvatarStatic(user.avatar), userName = user.username) {}

    Text("邮箱:${user.email}")
    Text("所在地:${user.location}")
    Text("年级:${user.gender}")
    Text("年龄:${user.age}")
    Button(onClick = { manageViewModel.addManager(user.email) }) { Text("加入审核管理员") }
    Button(onClick = {}) { Text("加入主要管理员") }
  }
}

/**
 * 显示用户信息
 *
 * @param url String
 * @param modifier Modifier
 * @param userName String
 * @param clickShowDetail Function0<Unit>
 */
@Composable
fun PersonalInformationAreaInManage(
  url: String,
  modifier: Modifier = Modifier.fillMaxWidth().height(50.dp),
  userName: String,
  clickShowDetail: () -> Unit,
) {
  Row(
    modifier = modifier.clickable { clickShowDetail.invoke() },
    verticalAlignment = Alignment.CenterVertically,
  ) {
    KamelImage(
      resource = asyncPainterResource(url),
      null,
      modifier = Modifier.fillMaxHeight(0.7f).aspectRatio(1f).clip(CircleShape),
      contentScale = ContentScale.FillBounds,
    )
    Text(
      modifier = Modifier.padding(horizontal = 10.dp).weight(1f).wrapContentHeight(),
      text = userName,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}
