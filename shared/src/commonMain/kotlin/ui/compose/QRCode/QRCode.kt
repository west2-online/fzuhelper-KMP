package ui.compose.QRCode

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrLogoPadding
import io.github.alexzhirkevich.qrose.options.QrLogoShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.brush
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import kotlin.jvm.Transient


@Composable
fun QRCodeScreen(
    modifier: Modifier
){
    val icon = remember {
        mutableStateOf(Icons.Filled.Done)
    }
    val logoPainter = rememberVectorPainter(
        icon.value,
    )
    val data = remember {
        mutableStateOf("https://example.com")
    }
    val angleColor = remember {
        mutableStateOf(Color.Black)
    }
    val contentColor = remember {
        mutableStateOf(Color.Black)
    }
    val qrcodePainter = rememberQrCodePainter(data.value) {
        logo {
            painter = logoPainter
            padding = QrLogoPadding.Natural(.1f)
            shape = QrLogoShape.circle()
            size = 0.2f
        }
        shapes {
            ball = QrBallShape.circle()
            darkPixel = QrPixelShape.roundCorners()
            frame = QrFrameShape.roundCorners(.25f)
        }
        colors  {
            dark = QrBrush.brush {
                Brush.linearGradient(
                    listOf(contentColor.value,contentColor.value),
                    end = Offset(it, it)
                )
            }
            frame = QrBrush.solid(angleColor.value)
        }
    }

    Column( modifier = modifier ) {
        Image(
            painter = qrcodePainter,
            null,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
                .wrapContentSize(Alignment.Center)
                .fillMaxSize(0.7f)
        )
        val color = listOf(
            Color.Black,
            Color.Red,
            Color.Green,
            Color.Gray,
            Color.White,
            Color.Yellow,
            Color.Blue,
            Color.Magenta
        )
        val iconList = listOf(
            Icons.Filled.Done,
            Icons.Filled.AccountBox,
            Icons.Filled.AccountCircle,
            Icons.Filled.AddCircle,
            Icons.Filled.Add,
            Icons.Filled.Call,
            Icons.Filled.DateRange,
            Icons.Filled.Delete,
            Icons.Filled.Email,
            Icons.Filled.Face,
            Icons.Filled.Info

        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(10.dp)
        ) {
            item {
                TextField(
                    value = data.value,
                    onValueChange = {
                        data.value = it
                    },
                    label = {
                        Text("二维码信息")
                    },
                    modifier = Modifier
                        .padding( bottom = 10.dp )
                        .fillMaxWidth()
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .padding( bottom = 10.dp )
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(MaterialTheme.colors.surface)
                        .padding(10.dp)
                ) {
                    Text(
                        "中心图标选择",
                        modifier = Modifier
                            .padding(vertical = 3.dp)
                    )
                    LazyRow(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .height( 50.dp )
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        iconList.forEach {
                            item {
                                Icon(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .clickable {
                                            icon.value = it
                                        },
                                    imageVector = it,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .padding( bottom = 10.dp )
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(MaterialTheme.colors.surface)
                        .padding(10.dp)
                ) {
                    Text(
                        "主体颜色",
                        modifier = Modifier
                            .padding(vertical = 3.dp)
                    )
                    LazyRow(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .height( 50.dp )
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        color.forEach {
                            item {
                                Box(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(it)
                                        .clickable {
                                            contentColor.value = it
                                        }
                                )
                            }
                        }
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .padding( bottom = 10.dp )
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(MaterialTheme.colors.surface)
                        .padding(10.dp)
                ) {
                    Text(
                        "四角颜色选择",
                        modifier = Modifier
                            .padding(vertical = 3.dp)
                    )
                    LazyRow(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .height( 50.dp )
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        color.forEach {
                            item {
                                Box(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(it)
                                        .clickable {
                                            angleColor.value = it
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


class QRCodeVoyagerScreen(
    @Transient
    val parentPaddingControl :ParentPaddingControl = defaultSelfPaddingControl()
):Screen{
    @Composable
    override fun Content() {
        QRCodeScreen(
            modifier = Modifier
                .parentSystemControl(parentPaddingControl)
                .fillMaxSize()
        )
    }
}