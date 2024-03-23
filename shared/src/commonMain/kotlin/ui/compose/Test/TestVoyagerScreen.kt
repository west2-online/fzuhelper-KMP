package ui.compose.Test

import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen

class TestVoyagerScreen :Screen{
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val isSelected = remember {
            mutableStateOf(false)
        }
        ElevatedFilterChip(
            selected = isSelected.value,
            label = {
                Text("text")
            },
            onClick = {
                isSelected.value = !isSelected.value
            }
        )
    }
}