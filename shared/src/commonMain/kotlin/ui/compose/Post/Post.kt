package ui.compose.Post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.koin.compose.koinInject
import ui.compose.Main.MainItems
import ui.compose.Report.ReportType
import ui.root.RootAction
import ui.setting.SettingTransitions
import util.compose.EasyToast
import util.compose.rememberToastState


object PostVoyagerScreen : Tab {


    override val options: TabOptions
        @Composable
        get() {
            val title = MainItems.POST.tag
            val icon = rememberVectorPainter(MainItems.POST.selectImageVector)
            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val toastState = rememberToastState()
        val rootAction = koinInject<RootAction>()
        Box(
            modifier = Modifier
                .fillMaxSize()
        ){
            Navigator(PostListVoyagerScreen(
                modifier = Modifier.fillMaxSize(),
                state = rememberLazyListState(),
                postListFlow = koinInject<PostListViewModel>().postListFlow.collectAsLazyPagingItems(),
                navigateToRelease = {
                    rootAction.navigateFormAnywhereToRelease()
                },
                navigateToReport = {
                    rootAction.navigateFormPostToReport(ReportType.PostReportType(id = it.Id.toString(),it))
                },
            )){ navigator ->
                SettingTransitions(navigator)
            }
            EasyToast(toastState)
        }
    }
}