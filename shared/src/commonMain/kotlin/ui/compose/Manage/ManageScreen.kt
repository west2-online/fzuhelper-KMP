package ui.compose.Manage


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.paging.compose.collectAsLazyPagingItems
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.replace
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import config.BaseUrlConfig
import data.post.PostById.FileData
import data.post.PostById.PostContent
import data.post.PostById.ValueData
import di.SelectItem
import di.TopBarState
import org.koin.compose.koinInject
import ui.compose.Post.ImageContent
import ui.compose.Post.PersonalInformationAreaInDetail
import ui.compose.Post.Time
import ui.util.network.NetworkResult

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


sealed class ManageScreenNav:Parcelable{
    @Parcelize
    data object ManageComment:ManageScreenNav()

    @Parcelize
    data object ManagePost:ManageScreenNav()
}

class ManageRouteNode(
    buildContext: BuildContext,
    private val backStack: BackStack<ManageScreenNav> = BackStack<ManageScreenNav>(
        model = BackStackModel(
            initialTarget = ManageScreenNav.ManagePost ,
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = { BackStackSlider(it) }
    )
):ParentNode<ManageScreenNav>(
    buildContext = buildContext,
    appyxComponent = backStack
){
    override fun resolve(interactionTarget: ManageScreenNav, buildContext: BuildContext): Node {
        return when(interactionTarget){
            is ManageScreenNav.ManageComment -> ManageCommentReport(buildContext)
            is ManageScreenNav.ManagePost -> ManagePostReport(buildContext)
        }
    }

    @Composable
    override fun View(modifier: Modifier){
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

                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .padding(10.dp)
                ){
                    var expanded by remember {
                        mutableStateOf(false)
                    }
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        DropdownMenuItem(onClick = {
                            backStack.replace(ManageScreenNav.ManagePost)
                        }) {
                            Text("管理帖子")
                        }
                        DropdownMenuItem(onClick = {
                            backStack.replace(ManageScreenNav.ManageComment)
                        }) {
                            Text("管理评论")
                        }
                    }
                }
            }
            AppyxComponent(
                backStack,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

class ManagePostReport(
    buildContext: BuildContext
):Node(
    buildContext = buildContext
){
    @Composable
    override fun View(modifier: Modifier) {
        val viewModel = koinInject<ManageViewModel>()
        val postReportPageList = viewModel.postReportPageList.collectAsLazyPagingItems()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(1f)
        ){
            postReportPageList.itemSnapshotList.items.forEach { postReportData ->
                if(postReportData.state.value is NetworkResult.Success){
                    item {
                        postReportData.let {
                            it.postData.let {  postById ->
                                Surface (
                                    modifier = Modifier
                                        .padding(10.dp)
                                ){
                                    Column(
                                        modifier = Modifier
                                            .wrapContentHeight()
                                            .padding(10.dp)
                                            .animateContentSize()
                                    ) {
                                        PersonalInformationAreaInDetail(
                                            userName = postById.Post.User.username,
                                            url = "${BaseUrlConfig.UserAvatar}/${postById.Post.User.avatar}"
                                        )
                                        Time(postById.Post.Time)
                                        Text(
                                            text = postById.Post.Title,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        listOf<PostContent>().plus(postById.valueData ?: listOf())
                                            .plus(postById.fileData ?: listOf()).sortedBy {
                                                it.order
                                            }.forEach {
                                                when (it) {
                                                    is FileData -> {
                                                        ImageContent(it.fileName)
                                                    }

                                                    is ValueData -> {
                                                        Text(it.value)
                                                    }
                                                }
                                            }
                                        Row (
                                            modifier = Modifier
                                                .wrapContentHeight()
                                                .fillMaxWidth(1f)
                                                .padding(vertical = 10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ){
                                            Button(
                                                modifier = Modifier,
                                                onClick = {

                                                }
                                            ){
                                                Text("")
                                            }
                                            Spacer(modifier = Modifier.width(100.dp))
                                            Button(
                                                modifier = Modifier,
                                                onClick = {

                                                }
                                            ){
                                                Text("")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

class ManageCommentReport(
    buildContext: BuildContext
):Node(
    buildContext = buildContext
){
    @Composable
    override fun View(modifier: Modifier) {
        Text("Comment")
    }
}