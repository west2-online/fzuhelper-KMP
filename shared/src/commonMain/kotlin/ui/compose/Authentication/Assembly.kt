package ui.compose.Authentication

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.replace
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.utils.multiplatform.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import org.koin.compose.koinInject

sealed class LoginTarget:Parcelable{

    @Parcelize
    data object Login:LoginTarget()

    @Parcelize
    data object Register:LoginTarget()

}

data class AuthenticationRouteNode(
    val buildContext:BuildContext,
    private val backStack: BackStack<LoginTarget> = BackStack(
        model = BackStackModel(
            initialTarget = LoginTarget.Login,
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = { BackStackFader(it) }
    )
):ParentNode<LoginTarget>(
    buildContext = buildContext,
    appyxComponent = backStack
){
    override fun resolve(interactionTarget: LoginTarget, buildContext: BuildContext): Node =
        when(interactionTarget){
            LoginTarget.Login -> LoginNode(
                buildContext = buildContext,
                navigateToRegister = {
                    backStack.replace(LoginTarget.Register)
                }
            )
            LoginTarget.Register -> RegisterNode(
                buildContext = buildContext,
                navigateToLogin = {
                    backStack.replace(LoginTarget.Login)
                }
            )
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

data class LoginNode(
    val buildContext:BuildContext,
    val navigateToRegister:()->Unit,
):Node(
    buildContext = buildContext
){
    @Composable
    override fun View(modifier: Modifier) {
        val viewModel = koinInject<AuthenticationViewModel>()
        Login(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            navigateToRegister = navigateToRegister,
            login = { userEmail,userPassword,captcha ->
                viewModel.login(userEmail,userPassword,captcha)
            },
            loginState = viewModel.loginState.collectAsState(),
            getCaptcha = { userEmail ->
                viewModel.getLoginCaptcha(userEmail)
            },
            loginCaptcha = viewModel.loginCaptcha.collectAsState(),
            cleanRegisterData = {
                viewModel.cleanRegisterData()
            }
        )
    }
}

data class RegisterNode(
    val buildContext:BuildContext,
    val navigateToLogin:()->Unit,
):Node(
    buildContext = buildContext
){
    @Composable
    override fun View(modifier: Modifier) {
        val viewModel = koinInject<AuthenticationViewModel>()
        Register(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            register = { email, password, captcha,studentCode,studentPassword->
                viewModel.register(email,password,captcha,studentCode,studentPassword)
            },
            getCaptcha = { email ->
                viewModel.getRegisterCaptcha(email)
            },
            captchaState = viewModel.captcha.collectAsState(),
            registerState = viewModel.registerState.collectAsState(),
            verifyStudentID = { studentCode, studentPassword,studentCaptcha ->
                viewModel.verifyStudentID(studentCode,studentPassword, captcha = studentCaptcha)
            },
            navigateToLogin = navigateToLogin,
            studentCaptchaState = viewModel.studentCaptcha.collectAsState(),
            getStudentCaptcha = {
                viewModel.refreshStudentCaptcha()
            },
            verifyStudentIDState = viewModel.verifyStudentIDState.collectAsState(),
            cleanRegisterData = {
                viewModel.cleanRegisterData()
            }
        )
    }
}


object LoginAndRegisterVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        Navigator(LoginVoyagerScreen())
    }
}

