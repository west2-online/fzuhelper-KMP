package ui.compose.Massage

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MassageScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
){
    val currentItem = remember {
        mutableStateOf<MassageItem>(MassageItem.MassageListItem())
    }
    Crossfade(currentItem.value){
        when(it){
            is MassageItem.MassageListItem->{
                MassageList(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    navigateToMassageDetail = {
                        currentItem.value = MassageItem.MassageDetailItem()
                    }
                )
            }
            is MassageItem.MassageDetailItem->{
                MassageDetail(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    back = {
                        currentItem.value = MassageItem.MassageListItem()
                    }
                )
            }
        }
    }

}

interface MassageItem{
    class MassageListItem:MassageItem
    class MassageDetailItem:MassageItem

}