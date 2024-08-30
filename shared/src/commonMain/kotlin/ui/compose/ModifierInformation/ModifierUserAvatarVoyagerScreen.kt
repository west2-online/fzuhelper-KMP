package ui.compose.ModifierInformation

import ImagePickerFactory
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import asImageBitmap
import cafe.adriel.voyager.core.screen.Screen
import dev.icerock.moko.resources.compose.painterResource
import getPlatformContext
import kotlin.jvm.Transient
import org.example.library.MR
import org.koin.compose.koinInject
import util.compose.EasyToast
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.compose.rememberToastState
import util.compose.toastBindNetworkResult

/**
 * 修改头像页面
 *
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class ModifierUserAvatarVoyagerScreen(
  @Transient val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) : Screen {
  @Composable
  override fun Content() {
    val modifierViewModel = koinInject<ModifierInformationViewModel>()
    val imageByteArray = remember { mutableStateOf<ByteArray?>(null) }
    val toast = rememberToastState()
    val imagePicker = ImagePickerFactory(context = getPlatformContext()).createPicker()
    imagePicker.registerPicker(
      onImagePicked = {
        if ((it.size / 1024 / 8 / 5) > 0) {
          toast.addWarnToast("图片过大")
          return@registerPicker
        }
        imageByteArray.value = it
      }
    )
    toast.toastBindNetworkResult(modifierViewModel.modifierAvatarState.collectAsState())
    Box {
      Column(
        modifier =
          Modifier.fillMaxSize()
            .parentSystemControl(parentPaddingControl = parentPaddingControl)
            .padding(horizontal = 10.dp)
      ) {
        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).animateContentSize()) {
          imageByteArray.value?.let {
            Image(
              modifier =
                Modifier.fillMaxWidth()
                  .aspectRatio(1f)
                  .wrapContentSize(Alignment.Center)
                  .fillMaxSize(0.7f)
                  .clip(RoundedCornerShape(5.dp))
                  .border(1.dp, Color.Gray, shape = RoundedCornerShape(5.dp)),
              bitmap = it.asImageBitmap(),
              contentDescription = null,
              contentScale = ContentScale.FillBounds,
            )
          }
        }
        Row(modifier = Modifier.padding(bottom = 10.dp)) {
          Crossfade(imageByteArray.value) {
            if (it == null) {
              Icon(
                modifier =
                  Modifier.size(50.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .clickable { imagePicker.pickImage() }
                    .padding(7.dp),
                painter = painterResource(MR.images.image),
                contentDescription = null,
                tint = Color.Gray,
              )
            } else {
              Icon(
                modifier =
                  Modifier.size(50.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .clickable { imageByteArray.value = null }
                    .padding(7.dp),
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = Color.Gray,
              )
            }
          }
        }
        Row(
          horizontalArrangement = Arrangement.End,
          modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
        ) {
          Button(
            onClick = {
              imageByteArray.value
                ?: let {
                  toast.addWarnToast("图片不得为空")
                  return@let
                }
              imageByteArray.value?.let { modifierViewModel.modifierUserAvatar(it) }
            }
          ) {
            Text("修改头像")
          }
        }
      }
      EasyToast(toast)
    }
  }
}
