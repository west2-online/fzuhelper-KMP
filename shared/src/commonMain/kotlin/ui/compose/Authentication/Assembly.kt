package ui.compose.Authentication

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Assembly(
    modifier: Modifier,
    viewModel: AuthenticationViewModel = koinInject()
){

    val pageState = rememberPagerState { 2 }
    val scope = rememberCoroutineScope()
    HorizontalPager(
        state = pageState,
        modifier = modifier,
        userScrollEnabled = false
    ){
        Surface (
            modifier = Modifier
                .fillMaxSize()
        ){
            when(it){
                0->{
                    Login(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        navigateToRegister = {
                            scope.launch {
                                pageState.animateScrollToPage(1)
                            }
                        },
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
                1->{
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
                        navigateToLogin = {
                            scope.launch {
                                pageState.animateScrollToPage(0)
                            }
                        },
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
        }

    }
}