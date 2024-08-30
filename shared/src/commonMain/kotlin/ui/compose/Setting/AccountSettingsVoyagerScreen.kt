package ui.compose.Setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import dao.TYPE_POSTGRADUATE
import dao.TYPE_UNDERGRADUATE
import kotlin.jvm.Transient
import org.koin.compose.koinInject
import util.compose.EasyToast
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.rememberToastState
import util.compose.toastBindNetworkResult
import util.network.CollectWithContentInBox

/**
 * 账号设置界面 二级界面
 *
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class AccountSettingsVoyagerScreen(
  @Transient val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) : Screen {
  @Composable
  override fun Content() {
    val settingViewModel = koinInject<SettingViewModel>()
    val signInStatus = settingViewModel.signInStatus.collectAsState()
    val username = remember {
      mutableStateOf(settingViewModel.kValueAction.schoolUserName.currentValue.value ?: "")
    }
    val account = remember {
      mutableStateOf(settingViewModel.kValueAction.schoolPassword.currentValue.value ?: "")
    }
    val isUndergraduate = remember {
      val loginType = settingViewModel.kValueAction.loginType.currentValue.value
      mutableStateOf(if (loginType == null) true else loginType == TYPE_UNDERGRADUATE)
    }
    val toastState = rememberToastState()
    toastState.toastBindNetworkResult(signInStatus)
    Box(modifier = Modifier.fillMaxSize()) {
      signInStatus.CollectWithContentInBox(
        modifier = Modifier.fillMaxSize(),
        success = { Text("登录成功", modifier = Modifier.align(Alignment.Center)) },
        loading = { CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) },
        content = {
          Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(horizontal = 20.dp).align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.selectableGroup(),
              horizontalArrangement = Arrangement.Center,
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { isUndergraduate.value = true },
              ) {
                RadioButton(
                  selected = isUndergraduate.value,
                  onClick = { isUndergraduate.value = true },
                )
                Text("本科生")
              }
              Spacer(modifier = Modifier.width(20.dp))
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { isUndergraduate.value = false },
              ) {
                RadioButton(
                  selected = !isUndergraduate.value,
                  onClick = { isUndergraduate.value = false },
                )
                Text("研究生")
              }
            }
            TextField(
              value = username.value,
              onValueChange = { username.value = it },
              label = { Text("账号") },
              modifier = Modifier.fillMaxWidth(),
            )
            TextField(
              value = account.value,
              onValueChange = { account.value = it },
              label = { Text("密码") },
              modifier = Modifier.fillMaxWidth(),
              visualTransformation = PasswordVisualTransformation('*'),
            )
            Button(
              onClick = {
                settingViewModel.verifyTheAccount(
                  username.value,
                  account.value,
                  if (isUndergraduate.value) TYPE_UNDERGRADUATE else TYPE_POSTGRADUATE,
                )
              }
            ) {
              Text("登录")
            }
          }
        },
      )
      EasyToast(toastState)
    }
  }
}
