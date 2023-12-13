package ui.compose.Person

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.liftric.kvault.KVault
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.example.library.MR
import org.koin.compose.koinInject
import ui.route.Route
import ui.route.RouteState

@Composable
fun PersonalDrawer(
    modifier: Modifier = Modifier,
    kVault: KVault = koinInject()
){
    Column(
        modifier = modifier
    ){
        Functions(
            modifier = Modifier
                .padding(top = 20.dp)
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
        )
    }
}

@Composable
fun PersonalInformation(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ) {
        KamelImage(
            resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
            null,
            modifier = Modifier
                .height(50.dp)
                .aspectRatio(1f)
                .clip(CircleShape),
            contentScale = ContentScale.FillBounds
        )
        Text(
            text = "theonenull",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding( top = 10.dp )
        )
        Text(
            text =  "sqt18750016193@163.com",
            fontSize = 10.sp
        )
    }
}

@Composable
fun Functions(
    modifier: Modifier = Modifier,
    routeState: RouteState = koinInject(),
    kVault: KVault = koinInject()
){
    LazyColumn(
        modifier = modifier
    ){
        item {
            FunctionsItem(
                painterResource(MR.images.feedback),
                {
                    routeState.navigateWithoutPop(Route.Feedback())
                },
                "反馈"
            )
        }
        item {
            FunctionsItem(
                painterResource(MR.images.qrcode),
                {
                    routeState.navigateWithoutPop(Route.QRCode())
                },
                "二维码生成"
            )

        }
        item {
            FunctionsItem(
                painterResource(MR.images.eye),
                {
                    routeState.navigateWithoutPop(Route.Person())
                },
                "个人资料"
            )
        }
        item {
            FunctionsItem(
                painterResource(MR.images.eye),
                {
//                    routeState.navigateWithoutPop(Route.Test())
                },
                "测试"
            )
        }
        item {
            FunctionsItem(
                painterResource(MR.images.loginOut),
                onclick = {
                    kVault.clear()
                    routeState.reLogin()
                },
                "退出登录",
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .height(50.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(231, 64, 50))
                    .clickable {
                        kVault.clear()
                        routeState.reLogin()
                    }
                    .padding( start = 10.dp )
            )
        }
    }
}

@Composable
fun LazyItemScope.FunctionsItem(
    painter: Painter,
    onclick :()->Unit = { },
    text : String,
    modifier: Modifier = Modifier
        .padding(bottom = 10.dp)
        .height(50.dp)
        .fillMaxWidth()
        .clip(RoundedCornerShape(10.dp))
        .clickable { onclick.invoke() }
        .padding( start = 10.dp )
){
    Row(
        modifier = modifier
            .clickable {
                       onclick.invoke()
            },
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painter,
            "",
            modifier = Modifier
                .fillMaxHeight(0.4f)
                .aspectRatio(1f)
        )
        Text(
            text,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false
        )
    }
}

