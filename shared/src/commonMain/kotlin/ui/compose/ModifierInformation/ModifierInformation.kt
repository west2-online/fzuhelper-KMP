package ui.compose.ModifierInformation

import ImagePickerFactory
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import asImageBitmap
import getPlatformContext

@Composable
fun ModifierInformation(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ){
        TextField(
            value = "",
            onValueChange = {

            },
            label = {
                Text("年级")
            }
        )
        TextField(
            value = "",
            onValueChange = {

            },
            label = {
                Text("年龄")
            }
        )
        TextField(
            value = "",
            onValueChange = {

            },
            label = {
                Text("所在地")
            }
        )

    }
}

@Composable
fun ReleasePageItemImage(
    modifier: Modifier,
){
    val image = remember {
        mutableStateOf<ByteArray?>(null)
    }
    val imagePicker = ImagePickerFactory(context = getPlatformContext()).createPicker()
    imagePicker.registerPicker(
        onImagePicked = {
            image.value = it
        }
    )
    Column( modifier ) {
        LazyRow(
            modifier = Modifier
                .height(60.dp)
                .padding(vertical = 5.dp)
        ) {
            item{
                Button(
                    {
                        imagePicker.pickImage()
                    }
                ){
                    Text("选择图片")
                }
            }
            item{
                Button(
                    {
                        imagePicker.pickImage()
                    }
                ){
                    Text("还原")
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            content = {
                Crossfade(image.value){
                    if(it != null){
                        Image(
                            bitmap = it.asImageBitmap(),
                            null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            contentScale = ContentScale.Crop
                        )
                    } else {

                    }
                }
            }
        )
    }
}