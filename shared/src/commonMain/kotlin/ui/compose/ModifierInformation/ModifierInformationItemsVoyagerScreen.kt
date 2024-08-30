package ui.compose.ModifierInformation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.person.UserData.Data
import kotlin.jvm.Transient
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl

/**
 * 选择要设置的内容
 *
 * @property userData Data
 * @constructor
 */
class ModifierInformationItemsVoyagerScreen(
  @Transient private val userData: Data,
  @Transient val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl(),
) : Screen {
  @Composable
  override fun Content() {
    Column(
      modifier =
        Modifier.fillMaxSize().parentSystemControl(parentPaddingControl).padding(horizontal = 10.dp)
    ) {
      ModifierUserDataItem(userData)
    }
  }
}

@Composable
fun ModifierUserDataItem(userData: Data) {
  val navigator = LocalNavigator.currentOrThrow
  Column(
    modifier =
      Modifier.wrapContentSize()
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .border(width = 2.dp, color = Color.Gray, RoundedCornerShape(10.dp))
        .padding(10.dp)
  ) {
    Text("用户信息设置", fontSize = 10.sp)
    Divider()
    Text(
      "用户信息修改",
      modifier =
        Modifier.clickable { navigator.push(ModifierUserDateVoyager(userData = userData)) }
          .padding(vertical = 10.dp)
          .fillMaxWidth(),
    )
    Divider()
    Text(
      "用户头像修改",
      modifier =
        Modifier.clickable { navigator.push(ModifierUserAvatarVoyagerScreen()) }
          .padding(vertical = 10.dp)
          .fillMaxWidth(),
    )
    Divider()
  }
}
