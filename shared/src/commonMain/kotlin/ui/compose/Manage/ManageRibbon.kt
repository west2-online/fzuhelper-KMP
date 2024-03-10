package ui.compose.Manage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import config.BaseUrlConfig
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.koinInject
import ui.setting.SettingTransitions
import util.network.CollectWithContent


class MangeRibbonVoyagerScreen():Screen{
    @Composable
    override fun Content() {
        Navigator(WorkWithExistingCarouselsVoyagerScreen()){ navigator ->
            Column {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ){
                    SettingTransitions(navigator = navigator)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                ){

                }
            }
        }
    }
}

@Composable
fun RibbonImageShow(
    ribbonUrl: String,
    refresh:()->Unit,
    delete:()->Unit
){
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(1f)
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
        ){
            KamelImage(
                resource = asyncPainterResource("${BaseUrlConfig.RibbonImage}/${ribbonUrl}"),
                modifier = Modifier
                    .aspectRatio(2f)
                    .fillMaxHeight(),
                contentDescription = "",
                contentScale = ContentScale.FillBounds
            )
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding( top = 10.dp )
            ) {
                Button(
                    onClick = {
                        delete.invoke()
                    }
                ) {
                    Text("删除该开屏页")
                }
            }
        }
    }
}

class WorkWithExistingCarouselsVoyagerScreen():Screen{
    @Composable
    override fun Content() {
        val manageViewModel = koinInject<ManageViewModel>()
        LaunchedEffect(Unit){
            manageViewModel.getRibbonData()
        }
        Column (
            modifier = Modifier
                .fillMaxSize()
        ){
            manageViewModel.ribbonList.collectAsState().CollectWithContent(
                success = { ribbonData ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(ribbonData.size){ index ->
                            ribbonData[index].let { ribbonData ->
                                RibbonImageShow(ribbonUrl = ribbonData.Image, refresh = {}, delete = {
                                    TODO()
                                })
                            }
                        }
                    }
                },
                loading = {
                    Box(modifier = Modifier.fillMaxSize()){
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                },
                error = {
                    Box(modifier = Modifier.fillMaxSize()){
                        Text(modifier = Modifier.align(Alignment.Center).clickable {
                            manageViewModel.getRibbonData()
                        }, text = "加载失败")
                    }
                }
            )
        }
    }
}