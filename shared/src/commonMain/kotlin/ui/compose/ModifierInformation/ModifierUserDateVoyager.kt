package ui.compose.ModifierInformation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import data.person.UserData.Data
import kotlin.jvm.Transient
import org.koin.compose.koinInject
import util.compose.EasyToast
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.compose.rememberToastState
import util.network.toast

/**
 * 修改用户信息的界面
 *
 * @property userData Data
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class ModifierUserDateVoyager(
  @Transient val userData: Data,
  @Transient val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl(),
) : Screen {
  @Composable
  override fun Content() {
    val toast = rememberToastState()
    val modifierInformationViewModel = koinInject<ModifierInformationViewModel>()
    val modifierUserdataState = modifierInformationViewModel.modifierUserdataState.collectAsState()
    LaunchedEffect(modifierUserdataState.value.key.value) {
      modifierUserdataState.value.toast(
        success = { toast.addToast(it) },
        error = { toast.addWarnToast(it.message.toString()) },
      )
    }

    var username by remember { mutableStateOf(userData.username) }
    var grade by remember { mutableStateOf(userData.gender) }
    var age by remember { mutableStateOf(userData.age.toString()) }
    var location by remember { mutableStateOf(userData.location) }

    Box(
      modifier =
        Modifier.fillMaxSize()
          .parentSystemControl(parentPaddingControl = parentPaddingControl)
          .padding(horizontal = 10.dp)
    ) {
      Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        TextField(
          value = username,
          onValueChange = { username = it },
          label = { Text("用户名") },
          modifier = Modifier.padding(bottom = 10.dp).wrapContentHeight().fillMaxWidth(),
        )

        TextField(
          value = grade,
          onValueChange = { grade = it },
          label = { Text("年级") },
          modifier = Modifier.padding(bottom = 10.dp).wrapContentHeight().fillMaxWidth(),
        )

        TextField(
          value = age,
          onValueChange = { age = it },
          label = { Text("年龄") },
          modifier = Modifier.padding(bottom = 10.dp).wrapContentHeight().fillMaxWidth(),
        )

        TextField(
          value = location,
          onValueChange = { location = it },
          label = { Text("所在地") },
          modifier = Modifier.padding(bottom = 10.dp).wrapContentHeight().fillMaxWidth(),
        )
        Row(
          horizontalArrangement = Arrangement.End,
          modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        ) {
          Button(
            onClick = {
              modifierInformationViewModel.modifierUserdata(username, age, grade, location)
            }
          ) {
            Text("修改信息")
          }
        }
      }
      EasyToast(toast)
    }
  }
}
