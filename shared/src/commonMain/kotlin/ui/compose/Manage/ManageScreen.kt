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
import org.koin.compose.koinInject


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
            is ManageScreenNav.ManagePost -> ManagePost(buildContext)
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
                    val lazyPagingItemsForPost = koinInject<ManageViewModel>().postReportPageList.collectAsLazyPagingItems()
                    val lazyPagingItemsForComment = koinInject<ManageViewModel>().commentReportPageList.collectAsLazyPagingItems()
                    IconButton(onClick = {
                        lazyPagingItemsForComment.refresh()
                        lazyPagingItemsForPost.refresh()
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
                            backStack.replace(ManageScreenNav.ManagePost)
                            expanded = false
                        }) {
                            Text("管理帖子")
                        }
                        DropdownMenuItem(onClick = {
                            backStack.replace(ManageScreenNav.ManageComment)
                            expanded = false
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





