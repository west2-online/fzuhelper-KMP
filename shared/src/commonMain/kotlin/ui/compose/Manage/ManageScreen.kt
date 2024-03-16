package ui.compose.Manage


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import org.koin.compose.koinInject
import ui.setting.SettingTransitions


class ManageVoyagerScreen():Screen{
    @Composable
    override fun Content() {
        val viewModel = koinInject<ManageViewModel>()
        Navigator(ManagePostVoyagerScreen){ navigator ->
            Column {
                TopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Row (
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ){
                        val lazyPagingItemsForPost = koinInject<ManageViewModel>().postReportPageList.collectAsLazyPagingItems()
                        val lazyPagingItemsForComment = koinInject<ManageViewModel>().commentReportPageList.collectAsLazyPagingItems()
                        IconButton(onClick = {
                            lazyPagingItemsForComment.refresh()
                            lazyPagingItemsForPost.refresh()
                            viewModel.refresh()
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Localized description")
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                    ){
                        var expanded by remember {
                            mutableStateOf(false)
                        }
                        IconButton(onClick = {
                            expanded = true
                        }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            DropdownMenuItem(onClick = {
                                if(navigator.lastItem !is ManagePostVoyagerScreen ){
                                    navigator.replaceAll(ManagePostVoyagerScreen)
                                }
                                expanded = false
                            }) {
                                Text("管理帖子")
                            }
                            DropdownMenuItem(onClick = {
                                if(navigator.lastItem !is ManageCommentVoyagerScreen ){
                                    navigator.replaceAll(ManageCommentVoyagerScreen)
                                }
                                expanded = false
                            }) {
                                Text("管理评论")
                            }
                            DropdownMenuItem(onClick = {
                                if(navigator.lastItem !is ManageOpenImageVoyagerScreen ){
                                    navigator.replaceAll(ManageOpenImageVoyagerScreen)
                                }
                                expanded = false
                            }) {
                                Text("管理开屏页")
                            }
                            DropdownMenuItem(onClick = {
                                if(navigator.lastItem !is MangeRibbonVoyagerScreen ){
                                    navigator.replaceAll(MangeRibbonVoyagerScreen())
                                }
                                expanded = false
                            }) {
                                Text("管理轮播页")
                            }
                        }
                    }
                }
                SettingTransitions(navigator)
            }
        }
    }
}





