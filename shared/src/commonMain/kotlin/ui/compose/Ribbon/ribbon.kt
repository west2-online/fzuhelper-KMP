package ui.compose.Ribbon

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.library.MR
import org.koin.compose.koinInject
import ui.route.Route

@Composable
fun Ribbon(
    modifier: Modifier,
    viewModel: RibbonViewModel = koinInject()
){
    Column(
        modifier = modifier,
    ) {
        Carousel(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f)
                .clip(RoundedCornerShape(10.dp))
        )
        LazyVerticalGrid(
            modifier = Modifier.padding(top = 10.dp).weight(1f).fillMaxWidth().clip(RoundedCornerShape(10.dp)),
            columns = GridCells.Fixed(5)
        ){
            items(Functions.values().size){
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.7f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            viewModel.enterFunction(Functions.values()[it].route)
                        }
                        .padding(10.dp)
                ){
                    Image(
                      painter = painterResource(Functions.values()[it].painter),
                        null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .wrapContentSize(Alignment.Center)
                            .fillMaxSize(0.5f)
                            .clip(RoundedCornerShape(10)),
                        contentScale = ContentScale.FillBounds
                    )
                    Text(
                        Functions.values()[it].functionName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Carousel(
    modifier: Modifier = Modifier,
    refreshCarousel:()->Unit = {},
) {
    val data = listOf<String>(
        "https://www.jetbrains.com/lp/compose-multiplatform/static/hero-desktop-a91cbf05d6f13666a61aba073a2e71bf.jpg",
        "https://www.jetbrains.com/_assets/www/fleet/inc/overview-content/parts/heading-section/img/main1248.8e6d0b77d29fd84703f62206d15767ff.png",
        "https://i1.wp.com/ugtechmag.com/wp-content/uploads/2018/05/kotlin-featured.png?fit=1200%2C630&ssl=1",
        "https://tse2-mm.cn.bing.net/th/id/OIP-C.ndsvvkmLDtOMBtFXt20BzwHaD4?w=329&h=180&c=7&r=0&o=5&dpr=1.3&pid=1.7"
    )
    val pageState = rememberPagerState(
        initialPage = 0,
    ) {
        data.size
    }

    val coroutineScope= rememberCoroutineScope()

    LaunchedEffect(pageState.currentPage){
        while (true){
            delay(4000)
            coroutineScope.launch {
                pageState.animateScrollToPage((pageState.currentPage+1)%data.size)
            }
        }
    }
    HorizontalPager(
        state = pageState,
        modifier = modifier.background(Color.Blue)
    ){
        KamelImage(
            resource = asyncPainterResource(data[it]),
            null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
}

enum class Functions(val route:Route,val functionName: String,val painter: ImageResource){
    QRCODE(route = Route.QRCode(),functionName = "二维码生成", painter = MR.images.feedback),
    WebView(route = Route.OwnWebView("https://welcome.fzuhelper.w2fzu.com/#/"),functionName = "新生宝典", painter = MR.images.login),
    Weather(route = Route.Weather(),functionName = "天气", painter = MR.images.close),
    Map(route = Route.SchoolMap(),functionName = "天气", painter = MR.images.close)
}