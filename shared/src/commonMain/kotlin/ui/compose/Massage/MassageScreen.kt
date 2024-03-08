package ui.compose.Massage

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize

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
                )
            }
        }
    }

}

sealed class MassageNav:Parcelable{

    @Parcelize
    data object MassageListItem:MassageNav()

    @Parcelize
    data object MassageDetailItem:MassageNav()

}

class MassageRouteNode(
    buildContext: BuildContext,
    private val backStack: BackStack<MassageNav> = BackStack(
        model = BackStackModel(
            initialTarget = MassageNav.MassageListItem,
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = { BackStackFader(it) }
    )
): ParentNode<MassageNav>(
    appyxComponent = backStack,
    buildContext = buildContext
) {
    override fun resolve(interactionTarget: MassageNav, buildContext: BuildContext): Node =
        when(interactionTarget){
            MassageNav.MassageDetailItem -> MassageDetailNode(buildContext , back = { backStack.pop() })
            MassageNav.MassageListItem -> MassageListNode(buildContext, navigateToMassageDetail = { backStack.push(MassageNav.MassageDetailItem) })
        }

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(
            appyxComponent = backStack,
            modifier = modifier
                .fillMaxSize()
        )
    }
}

class MassageDetailNode(
    buildContext: BuildContext,
    private val back :(()->Unit)? = null
): Node(
    buildContext = buildContext
) {
    @Composable
    override fun View(modifier: Modifier) {
        MassageDetail(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        )
    }
}


class MassageListNode(
    buildContext: BuildContext,
    private val navigateToMassageDetail :(String) -> Unit = {}
): Node(
    buildContext = buildContext
) {
    @Composable
    override fun View(modifier: Modifier) {
        MassageList(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            navigateToMassageDetail = navigateToMassageDetail
        )
    }
}




interface MassageItem{
    class MassageListItem:MassageItem
    class MassageDetailItem:MassageItem
}


object MassageVoyagerScreen: Tab {
    override val options: TabOptions
        @Composable
        get(){
            return remember {
                TabOptions(
                    index = 0u,
                    title = ""
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(
            MassageVoyagerList()
        )
    }
}