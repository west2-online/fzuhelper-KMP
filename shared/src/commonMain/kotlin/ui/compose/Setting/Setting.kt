package ui.compose.Setting

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import dev.icerock.moko.resources.compose.fontFamilyResource
import org.koin.compose.koinInject
import ui.setting.Font
import ui.setting.PageTransitions
import ui.setting.Setting
import ui.setting.ThemeStyle
import ui.setting.toComposeTheme


class SettingVoyagerScreen : Screen {

    @Composable
    override fun Content() {

        val setting = koinInject<Setting>()
        val theme = setting.theme.collectAsState()
        val font = setting.font.collectAsState()
        val transitions = setting.transitions.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ){
            TopAppBar {
                Text("主题设置")
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                items(ThemeStyle.values()) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(200.dp)
                            .height(120.dp)
                            .padding(horizontal = 10.dp)
                            .border(
                                width = 2.dp,
                                color = animateColorAsState(if (theme.value == it) Color.Blue else Color.Transparent).value
                            )
                            .padding(all = 10.dp)
                            .clickable {
                                setting.changeTheme(it)
                            }

                    ) {
                        Column {
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().primaryInLightTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().onPrimaryInLightTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().primaryVariantInLightTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().secondaryInLightTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().onSecondaryInLightTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().secondaryVariantInLightTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().surfaceInLightTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().onSurfaceInLightTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().errorInLightTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().onErrorInLightTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().backgroundInLightTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().onBackgroundInLightTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().primaryInDarkTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().onPrimaryInDarkTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().primaryVariantInDarkTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().secondaryInDarkTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().onSecondaryInDarkTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().secondaryVariantInDarkTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().surfaceInDarkTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().onSurfaceInDarkTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().errorInDarkTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().onErrorInDarkTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().backgroundInDarkTheme)
                            )
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth()
                                    .background(it.toComposeTheme().onBackgroundInDarkTheme)
                            )
                        }
                    }
                }
            }
            TopAppBar {
                Text("字体设置")
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp)
            ) {
                items(Font.values()) {
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .wrapContentHeight()
                            .wrapContentSize()
                            .border(
                                width = 2.dp,
                                color = animateColorAsState(if (font.value == it) Color.Blue else Color.Transparent).value,
                                shape = RoundedCornerShape(10 )
                            )
                            .padding(10.dp)
                            .clickable {
                                setting.changeFont(it)
                            }

                    ) {
                        Text(
                            "This is a test",
                            fontFamily = fontFamilyResource(it.fontResource)
                        )
                    }
                }
            }
            TopAppBar {
                Text("界面切换动画")
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                items(PageTransitions.values()) {
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .wrapContentHeight()
                            .wrapContentSize()
                            .border(
                                width = 2.dp,
                                color = animateColorAsState(if (transitions.value == it) Color.Blue else Color.Transparent).value,
                                shape = RoundedCornerShape(10 )
                            )
                            .padding(10.dp)
                            .clickable {
                                setting.changeTransitions(it)
                            }
                    ) {
                        Text(
                            it.describe,
                        )
                    }
                }
            }

        }
    }

}