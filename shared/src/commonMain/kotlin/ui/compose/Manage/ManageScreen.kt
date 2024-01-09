package ui.compose.Manage


import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import di.SelectItem
import di.TopBarState
import org.koin.compose.koinInject

@Composable
fun ManageScreen(
    modifier: Modifier = Modifier
){
    val topBarState = koinInject<TopBarState>()
    DisposableEffect(Unit){
        topBarState.registerItemForSelect(
            listOf(
                SelectItem(
                    text = "this is a test",
                    click = {
                        topBarState.expanded.value = false
                        topBarState.title.value = "this is "
                    }
                )
            )
        )
        onDispose {
            topBarState.registerItemForSelect(null)
        }
    }
}

