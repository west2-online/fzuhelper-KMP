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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import asImageBitmap
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import data.Person.UserData.Data
import dev.icerock.moko.resources.compose.painterResource
import getPlatformContext
import org.example.library.MR
import org.koin.compose.koinInject
import ui.util.compose.EasyToast
import ui.util.compose.rememberToastState
import ui.util.network.toast

@Composable
fun ModifierInformationScreen(
    userData: Data,
    modifier: Modifier = Modifier,
    viewModel :ModifierInformationViewModel = koinInject()
){
    val toast = rememberToastState()
    val modifierUserdataState = viewModel.modifierUserdataState.collectAsState()
    val modifierUserAvatarState = viewModel.modifierAvatarState.collectAsState()
    LaunchedEffect(modifierUserdataState.value.key.value){
        modifierUserdataState.value.toast(
            success = {
                toast.addToast(it)
            },
            error = {
                toast.addWarnToast(it.message.toString())
            }
        )
    }
    LaunchedEffect(modifierUserAvatarState.value.key.value){
        modifierUserAvatarState.value.toast(
            success = {
                toast.addToast(it)
            },
            error = {
                toast.addWarnToast(it.message.toString())
            }
        )
    }
    var username by remember { mutableStateOf(userData.username) }
    var grade by remember { mutableStateOf(userData.gender) }
    var age by remember { mutableStateOf( userData.age.toString() ) }
    var location by remember { mutableStateOf(userData.location) }
    val imageByteArray = remember {
        mutableStateOf<ByteArray?>(null)
    }
    val imagePicker = ImagePickerFactory(context = getPlatformContext()).createPicker()
    imagePicker.registerPicker(
        onImagePicked = {
            imageByteArray.value = it
        }
    )
    Box{
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState())
                .padding(vertical = 10.dp)
                .padding(horizontal = 10.dp)
        ) {
            TextField(
                value = username,
                onValueChange = {
                    username = it
                },
                label = {
                    Text("用户名")
                },
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()
            )

            TextField(
                value = grade,
                onValueChange = {
                    grade = it
                },
                label = {
                    Text("年级")
                },
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()
            )

            TextField(
                value = age,
                onValueChange = {
                    age = it
                },
                label = {
                    Text("年龄")
                },
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()
            )

            TextField(
                value = location,
                onValueChange = {
                    location = it
                },
                label = {
                    Text("所在地")
                },
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .wrapContentHeight()
                    .fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.modifierUserdata(username,age,grade,location)
                    }
                ) {
                    Text("修改信息")
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .animateContentSize()

            ) {
                imageByteArray.value?.let {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .wrapContentSize(Alignment.Center)
                            .fillMaxSize(0.7f)
                            .clip(RoundedCornerShape(5.dp))
                            .border(1.dp, Color.Gray, shape = RoundedCornerShape(5.dp)),
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(bottom = 10.dp)
            ) {
                Crossfade(
                    imageByteArray.value
                ) {
                    if (it == null) {
                        Icon(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .clickable {
                                    imagePicker.pickImage()
                                }
                                .padding(7.dp),
                            painter = painterResource(MR.images.image),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    } else {
                        Icon(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .clickable {
                                    imageByteArray.value = null
                                }
                                .padding(7.dp),
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Button(
                    onClick = {
                        imageByteArray.value?:let {
                            toast.addWarnToast("图片不得为空")
                            return@let
                        }
                        imageByteArray.value?.let { viewModel.modifierUserAvatar(it) }
                    }
                ) {
                    Text("修改头像")
                }
            }
        }
        EasyToast(toast)
    }
}

class ModifierInformationRouteNode(
    private val userData: Data,
    buildContext: BuildContext
): Node(
    buildContext
){
    @Composable
    override fun View(modifier: Modifier) {
        ModifierInformationScreen(
            userData
        )
    }
}

