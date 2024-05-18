package ui.compose.Log

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.koinInject
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import kotlin.jvm.Transient

/**
 * 日志显示
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class LogVoyagerScreen(
    @Transient
    val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) :Screen{
    @Composable
    override fun Content() {
        val loginViewModel = koinInject<LogViewModel>()
        val logData = loginViewModel.logs.collectAsState(listOf())
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .parentSystemControl(parentPaddingControl)
        ){
            items(logData.value) {
                Column {
                    Text(it.time.toString())
                    Text(it.error.toString())
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                    )
                }
            }
        }
    }
}