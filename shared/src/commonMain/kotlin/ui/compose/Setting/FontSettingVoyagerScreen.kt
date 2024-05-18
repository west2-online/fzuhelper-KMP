package ui.compose.Setting

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import dao.ThemeKValueAction
import dev.icerock.moko.resources.compose.fontFamilyResource
import di.globalScope
import org.koin.compose.koinInject
import util.compose.FontStyle
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.compose.toFont
import util.flow.launchInDefault
import kotlin.jvm.Transient

/**
 * 字体设置界面 二级界面
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class FontSettingVoyagerScreen(
    @Transient
    val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
):Screen {
    @Composable
    override fun Content() {
        val setting = koinInject<ThemeKValueAction>()
        val font = setting.fontToken.currentValue.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .parentSystemControl(parentPaddingControl)
        ){
            TopAppBar {
                Text("字体设置")
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(10.dp)
            ) {
                items(FontStyle.entries.toTypedArray()) {
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .wrapContentHeight()
                            .wrapContentSize()
                            .border(
                                width = 2.dp,
                                color = animateColorAsState(if (font.value.toFont() == it) Color.Blue else Color.Transparent).value,
                                shape = RoundedCornerShape(10)
                            )
                            .padding(10.dp)
                            .clickable {
                                globalScope.launchInDefault {
                                    setting.fontToken.setValue(it.serializable)
                                }
                            }

                    ) {
                        Text(
                            "This is a test",
                            fontFamily = fontFamilyResource(it.fontResource)
                        )
                    }
                }
            }
        }
    }
}