package ui.compose.Manage

import SelectItem
import TopBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject

@Composable
fun ManageScreen(
    modifier: Modifier = Modifier
){
    val topBarState = koinInject<TopBarState>()
    LaunchedEffect(Unit){
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
    }
}

