package ui.compose.Main


import androidVersion.AndroidVersion
import androidVersion.Version
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.liftric.kvault.KVault
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import di.SystemAction
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlinx.serialization.json.Json
import org.example.library.MR
import org.koin.compose.koinInject
import ui.root.getRootAction
import kotlin.time.Duration.Companion.days

fun getPassedRange(): Float {
    // 获取当前日期
    val currentDate = Clock.System.todayIn(TimeZone.UTC)

    // 获取今年的开始日期
    val startOfYear = LocalDate(currentDate.year, 1, 1)

    // 计算已经过去的天数
    val daysPassed = currentDate.minus(startOfYear).days

    // 获取今年总天数
    val daysInYear = 365.days // 注意：这里假设每年都是 365 天

    // 计算已经过去的天数比例
    return (daysPassed.toFloat() / daysInYear.inWholeDays)
}
@Composable
fun MainDrawer(
    modifier: Modifier = Modifier,
){

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        item{
            val angle = Animatable(270f)
            LaunchedEffect(Unit){
                angle.animateTo(getPassedRange()*360f*(-1.0f))
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colors.primary)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Canvas(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                ){
                    drawArc(
                        brush = Brush.linearGradient(colors = listOf(Color.Green, Color.Blue, Color.Red)),
                        startAngle = 270f,
                        sweepAngle = angle.value,
                        useCenter = false,
                        size = Size(size.height - size.height/3,size.height - size.height/3),
                        style = Stroke(30f, cap = StrokeCap.Round),
                        topLeft = Offset(size.height/6,size.height/6)
                    )
                }
                Text("今年已过 ${(getPassedRange()*100).toInt()}%，继续加油！\uD83E\uDD17")
            }
        }
        item{
            val client = koinInject<HttpClient>()
            val latest = remember {
                mutableStateOf<Version?>(null)
            }
            LaunchedEffect(Unit){
                try {
                    val result = client.get("/static/config/androidVersion.json").bodyAsText()
                    val data:AndroidVersion = Json.decodeFromString(result)
                    latest.value = data.version
                        .filter {
                            it.canUse
                        }
                        .filter {
                            it.version.split("-")[1]=="Release"
                        }.sortedBy {
                            val list = it.version.split("-")[0].split(".")
                            return@sortedBy list[0].toInt() *100 + list[1].toInt() *10 + list[2].toInt() *1
                        }.lastOrNull()
                } catch (e:Exception){
                    println(e.message.toString())
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colors.primary)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){
                Text( "当前版本:"+stringResource(MR.strings.version))
                Divider( modifier = Modifier.fillMaxWidth().height(1.dp))
                latest.value?.let {
                    Text( "最新版本:"+ latest.value?.version)
                }
            }
        }
        item{
            Functions(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
            )
        }
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
    kVault: KVault = koinInject()
){
    val rootAction = getRootAction()
    Column(
        modifier = modifier
    ){
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )
        FunctionsItem(
            painterResource(MR.images.feedback),
            {
                rootAction.navigateFromActionToFeedback()
            },
            "反馈"
        )
        FunctionsItem(
            painterResource(MR.images.qrcode),
            {
                rootAction.navigateFromActionToQRCodeScreen()
            },
            "二维码生成"
        )
//        FunctionsItem(
//            painterResource(MR.images.eye),
//            {
//                rootAction.navigateToNewTarget(rootTarget = RootTarget.Person(null))
//            },
//            "个人资料"
//        )
//        FunctionsItem(
//            painterResource(MR.images.eye),
//            {
////                    routeState.navigateWithoutPop(Route.Test())
//                rootAction.navigateToNewTarget(rootTarget = RootTarget.Person(null))
//            },
//            "测试"
//        )
        val systemAction = koinInject<SystemAction>()
        FunctionsItem(
            Icons.Filled.ExitToApp,
            {
                systemAction.onFinish.invoke()
            },
            "退出程序"
        )
        FunctionsItem(
            painterResource(MR.images.loginOut),
            onclick = {
                kVault.clear()
                rootAction.reLogin()
            },
            "退出登录",
            modifier = Modifier
                .padding(bottom = 10.dp)
                .height(50.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(231, 64, 50))
                .padding( start = 10.dp )
        )
    }
}

@Composable
fun FunctionsItem(
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
        modifier = modifier,
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

@Composable
fun FunctionsItem(
    imageVector: ImageVector,
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
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            imageVector = imageVector,
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

