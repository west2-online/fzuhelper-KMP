package ui.root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.newRoot
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.operation.replace
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue
import data.Person.UserData.Data
import di.SystemAction
import di.appModule
import initStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import ui.compose.AboutUs.AboutUsRouteNode
import ui.compose.Authentication.AuthenticationRouteNode
import ui.compose.Feedback.FeedbackAssemblyNode
import ui.compose.Main.MainRouteNode
import ui.compose.Massage.MassageRouteNode
import ui.compose.ModifierInformation.ModifierInformationRouteNode
import ui.compose.Person.PersonRouteNode
import ui.compose.QRCode.QRCodeRouteNode
import ui.compose.Release.ReleaseRouteNode
import ui.compose.Report.ReportRouteNode
import ui.compose.Report.ReportType
import ui.compose.Ribbon.RibbonRouteNode
import ui.compose.SplashPage.SplashPageRouteNode
import ui.compose.Weather.WeatherRouteNode



sealed class RootTarget : Parcelable {
    @Parcelize
    data object Main : RootTarget()

    @Parcelize
    data object AboutUs : RootTarget()

    @Parcelize
    data object Weather : RootTarget()

    @Parcelize
    data object Massage : RootTarget()

    @Parcelize
    data object Authentication : RootTarget()

    @Parcelize
    data object SplashPage : RootTarget()

    @Parcelize
    class ModifierInformation(val userData: @RawValue Data) : RootTarget()

    @Parcelize
    class Person(
        val userId:String?
    ) : RootTarget()

    @Parcelize
    data object QRCode : RootTarget()

    @Parcelize
    data object Release : RootTarget()

    @Parcelize
    class Report(
        val type : @RawValue ReportType,
    ) : RootTarget()

    @Parcelize
    data object Ribbon : RootTarget()

    @Parcelize
    data object Feedback : RootTarget()
}

class RootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<RootTarget> = BackStack(
        model = BackStackModel(
            initialTarget = RootTarget.SplashPage,
            savedStateMap = mutableMapOf(),
        ),
        visualisation = { BackStackFader(it) }
    ),
    private val systemAction: SystemAction
) : ParentNode<RootTarget>(
    buildContext = buildContext,
    appyxComponent = backStack
) {
    override fun resolve(interactionTarget: RootTarget, buildContext: BuildContext): Node =
        when (interactionTarget) {
            is RootTarget.AboutUs -> AboutUsRouteNode(buildContext)
            is RootTarget.Authentication -> AuthenticationRouteNode(buildContext)
            is RootTarget.Feedback -> FeedbackAssemblyNode(buildContext)
            is RootTarget.Main -> MainRouteNode(buildContext)

            is RootTarget.Massage -> MassageRouteNode(buildContext)
            is RootTarget.ModifierInformation -> ModifierInformationRouteNode(interactionTarget.userData,buildContext)
            is RootTarget.Person -> PersonRouteNode(buildContext,userId = interactionTarget.userId)
            is RootTarget.QRCode -> QRCodeRouteNode(buildContext)
            is RootTarget.Release -> ReleaseRouteNode(buildContext)
            is RootTarget.Report -> ReportRouteNode(buildContext,interactionTarget.type)
            is RootTarget.Ribbon -> RibbonRouteNode(buildContext)

            is RootTarget.SplashPage -> SplashPageRouteNode(buildContext)
            is RootTarget.Weather -> WeatherRouteNode(buildContext)

        }

    @Composable
    override fun View(modifier: Modifier) {
        val scope = rememberCoroutineScope()
        KoinApplication(application = {
            modules(appModule(
                object : RootAction{
                    override fun navigateToNewTarget(rootTarget: RootTarget) {
                        scope.launch {
                            backStack.push(rootTarget)
                        }
                    }

                    override fun replaceNewTarget(rootTarget: RootTarget) {
                        scope.launch {
                            backStack.replace(target = rootTarget)
                        }
                    }

                    override fun canBack() = backStack.canHandeBackPress()

                    override fun reLogin() {
                        initStore().clear()
                        backStack.newRoot(RootTarget.Authentication)
                    }
                },
                systemAction = systemAction
            ))
        }) {
            AppyxComponent(
                appyxComponent = backStack,
                modifier = Modifier.fillMaxSize(),
                clipToBounds = false
            )
        }
    }

}


interface RootAction{
    fun navigateToNewTarget(rootTarget : RootTarget)
    fun replaceNewTarget(rootTarget : RootTarget)
    fun canBack():StateFlow<Boolean>
    fun reLogin()
}

@Composable
fun getRootAction(): RootAction {
    return koinInject<RootAction>()
}