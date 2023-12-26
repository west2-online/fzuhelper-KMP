package ui.compose.Manage

import MainViewState
import SelectItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import org.koin.compose.koinInject

@Composable
fun ManageScreen(
    modifier: Modifier = Modifier
){
    val mainViewState = koinInject<MainViewState>()
    LaunchedEffect(Unit){
        mainViewState.itemForSelect.value = listOf(
            SelectItem(
                text = "this is a test",
                click = {
                    mainViewState.expanded.value = false
                    mainViewState.title.value = "this is "
                }
            )
        )
    }
}

