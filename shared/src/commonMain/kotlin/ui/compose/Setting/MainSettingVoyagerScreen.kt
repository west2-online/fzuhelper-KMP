package ui.compose.Setting

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dao.UndergraduateKValueAction
import org.koin.compose.koinInject
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import kotlin.jvm.Transient

/**
 * 设置主界面 一级界面
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class MainSettingVoyagerScreen(
    @Transient
    private val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl(),
): Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        LazyColumn (
            modifier = Modifier.parentSystemControl(parentPaddingControl)
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ){
            item {
                AccountSettings(navigator)
            }
            item {
                ThemeSettingAssembly(navigator)
            }
        }
    }
}

@Composable
fun AccountSettings(
    navigator: Navigator
){
    val setting = koinInject<UndergraduateKValueAction>()
    val settingViewModel = koinInject<SettingViewModel>()
    Column(
        modifier = Modifier
            .wrapContentSize()
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(width = 2.dp, color = Color.Gray,RoundedCornerShape(10.dp))
            .padding(10.dp)
    ){
        Text("账号设置", fontSize = 10.sp)
        Divider()
        setting.schoolUserName.currentValue.collectAsState().value.let {
            if( it != null ){
                val undergraduate = true
                Row(
                    modifier = Modifier.clickable {
                        navigator.push(AccountSettingsVoyagerScreen())
                    }.padding(vertical = 10.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text( it , modifier = Modifier.weight(1f).padding(horizontal = 4.dp), overflow = TextOverflow.Ellipsis )
                    Button(
                        onClick ={
                            settingViewModel.clearAccount()
                        },
                        content = {
                            Text("退出登录")
                        }
                    )
                    Text( if (undergraduate) "本科生" else "研究生" , modifier = Modifier, fontSize = 10.sp)
                }
            }else{
                Text("未登录，点击登录", modifier = Modifier.clickable {
                    navigator.push(AccountSettingsVoyagerScreen())
                }.padding(vertical = 10.dp).fillMaxWidth())
            }
        }

    }
}


@Composable
fun ThemeSettingAssembly(
    navigator: Navigator,
){
    Column(
        modifier = Modifier
            .wrapContentSize()
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(width = 2.dp, color = Color.Gray,RoundedCornerShape(10.dp))
            .padding(10.dp)
    ){
        Text("主题设置", fontSize = 10.sp)
        Divider()
        Text("字体设置", modifier = Modifier.clickable {
            navigator.push(FontSettingVoyagerScreen())
        }.padding(vertical = 10.dp).fillMaxWidth())
        Divider()
        Text("界面切换动画", modifier = Modifier.clickable {
            navigator.push(TransitionsSettingVoyagerScreen())
        }.padding(vertical = 10.dp).fillMaxWidth())
        Divider()
        Text("主题颜色设置", modifier = Modifier.clickable {
            navigator.push(ThemeSettingVoyagerScreen())
        }.padding(vertical = 10.dp).fillMaxWidth())
    }
}