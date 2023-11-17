package ui.compose.New

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun NewScreen(
    modifier: Modifier = Modifier
){
    val currentItem = remember {
        mutableStateOf<NewItem>(NewItem.NewList())
    }
    Crossfade(
        currentItem.value
    ){
        when(it){
            is NewItem.NewList ->{
                NewsList(
                    modifier = Modifier
                        .fillMaxSize(),
                    navigateToNewsDetail = {
                        currentItem.value = NewItem.NewDetail(it)
                    }
                )
            }
            is NewItem.NewDetail ->{
                NewsDetail(
                    modifier = Modifier
                        .fillMaxSize(),
                    back = {
                        currentItem.value = NewItem.NewList()
                    }
                )
            }
        }
    }
}


interface NewItem{
    class NewList():NewItem
    class NewDetail(var id:String):NewItem
}