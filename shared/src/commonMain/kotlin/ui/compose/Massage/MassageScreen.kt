package ui.compose.Massage

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MassageScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {}
){
    MassageList(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    )
}