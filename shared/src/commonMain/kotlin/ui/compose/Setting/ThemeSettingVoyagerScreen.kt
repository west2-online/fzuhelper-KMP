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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import dao.ThemeKValueAction
import di.globalScope
import org.koin.compose.koinInject
import util.compose.ParentPaddingControl
import util.compose.ThemeStyle
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.compose.toComposeTheme
import util.compose.toTheme
import util.flow.launchInDefault
import kotlin.jvm.Transient

class ThemeSettingVoyagerScreen(
    @Transient
    private val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl(),
):Screen{
    @Composable
    override fun Content() {
        val setting = koinInject<ThemeKValueAction>()
        val theme = setting.themeToken.currentValue.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .parentSystemControl(parentPaddingControl)
        ) {
            TopAppBar {
                Text("主题设置")
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(ThemeStyle.entries.toTypedArray()) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(200.dp)
                            .height(120.dp)
                            .padding(horizontal = 10.dp)
                            .border(
                                width = 2.dp,
                                color = animateColorAsState(if (theme.value.toTheme() == it) Color.Blue else Color.Transparent).value
                            )
                            .padding(all = 10.dp)
                            .clickable {
                                globalScope.launchInDefault {
                                    setting.themeToken.setValue(it.serializable)
                                }
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
        }
    }
}