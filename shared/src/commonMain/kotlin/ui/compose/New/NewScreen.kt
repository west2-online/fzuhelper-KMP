package ui.compose.New

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun NewScreen(
    modifier: Modifier = Modifier,
    viewModel:NewViewModel = koinInject()
){
    val currentItem = remember {
        mutableStateOf<NewItem>(NewItem.NewList())
    }
    LaunchedEffect(Unit){
        viewModel.getPostByPage("1")
    }

    val state = rememberLazyListState()
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
                    },
                    postListState = viewModel.postList.collectAsState(),
                    state = state
                )
            }
            is NewItem.NewDetail ->{
                NewsDetail(
                    id = it.id,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    back = {
                        currentItem.value = NewItem.NewList()
                    },
                    postState = viewModel.currentPostDetail.collectAsState(),
                    getPostById = {
                        viewModel.getPostById(it)
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