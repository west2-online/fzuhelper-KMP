package ui.compose.Authentication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.coroutines.delay
import org.example.library.MR
import ui.util.network.CollectWithContent
import ui.util.network.NetworkResult

@Composable
fun Register(
    modifier: Modifier,
    captchaState: State<NetworkResult<String>>,
    registerState: State<NetworkResult<Int>>,
    getCaptcha: (email: String) -> Unit,
    register: (email: String, password: String, captccha: String) -> Unit,
    navigateToLogin: () -> Unit,
    verifyStudentID: (studentCode: String, studentPassword: String, studentCaptcha: String) -> Unit,
    studentCaptchaState: State<NetworkResult<ImageBitmap>>,
    getStudentCaptcha : () -> Unit,
) {
    var studentCode by remember {
        mutableStateOf("")
    }
    var studentPassword by remember {
        mutableStateOf("")
    }
    var studentCaptcha by remember {
        mutableStateOf("")
    }
    var talkerEmail by remember {
        mutableStateOf("")
    }
    var talkerPassword by remember {
        mutableStateOf("")
    }
    var talkerPasswordAgain by remember {
        mutableStateOf("")
    }
    var captcha by remember {
        mutableStateOf("")
    }
    var editAble by remember {
        mutableStateOf(false)
    }
    val weight by animateFloatAsState(
        if(editAble) 1f else 0f
    )
    val isRegister by remember {
        derivedStateOf {
            registerState.value is NetworkResult.Loading || captchaState.value is NetworkResult.Loading
        }
    }
    val registerAble = remember {
        derivedStateOf {
            !isRegister && studentCode != "" && studentPassword!="" && talkerPassword != "" && talkerPasswordAgain!="" && talkerEmail!="" && captcha!=""
        }
    }
    var toast by remember {
        mutableStateOf<String?>(null)
    }
    LaunchedEffect(toast){
        delay(2000)
        toast?.let {
            toast = null
        }
    }
    LaunchedEffect(Unit){
        getStudentCaptcha()
    }
    LazyColumn (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        item {
            Image(
                painter = painterResource(MR.images.FuTalk),
                "",
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f)
                    .padding(vertical = 30.dp)
            )
        }
        item {
            TextField(
                value = studentCode,
                onValueChange = {
                    studentCode = it
                },
                label = {
                    Text("学号")
                },
                maxLines = 1,
                singleLine = true,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
        item {
            TextField(
                value = studentPassword,
                onValueChange = {
                    studentPassword = it
                },
                label = {
                    Text("教务处密码")
                },
                maxLines = 1,
                singleLine = true,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                visualTransformation = PasswordVisualTransformation()
            )
        }
        item {
            Row{

                studentCaptchaState.CollectWithContent(
                    content = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable {
                                    getStudentCaptcha.invoke()
                                }
                        )
                    },
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ){
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Red)
                                .clickable {
                                    getStudentCaptcha.invoke()
                                }
                        ){
                            Text(
                                "获取失败",
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }

                    },
                    success = {
                        Image(
                            bitmap = it, null,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color = Color.Red)
                                .clickable {
                                    getStudentCaptcha.invoke()
                                },
                            contentScale = ContentScale.FillBounds
                        )
                    },
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .weight(1f)
                        .height(56.dp)
                        .padding(end = 5.dp)
                )
                TextField(
                    value = studentCaptcha,
                    onValueChange = {
                        studentCaptcha = it
                    },
                    label = {
                        Text("验证码")
                    },
                    maxLines = 1,
                    singleLine = true,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .weight(1f)
                        .height(56.dp),
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        }
        item {
            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    verifyStudentID.invoke(studentCode,studentPassword,studentCaptcha)
                }
            ){
                Text("验证")
            }
        }
        item {
            TextField(
                value = talkerEmail,
                onValueChange = {
                    talkerEmail = it
                },
                label = {
                    Text("邮箱")
                },
                maxLines = 1,
                singleLine = true,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
        item {
            TextField(
                value = talkerPassword,
                onValueChange = {
                    talkerPassword = it
                },
                label = {
                    Text("密码")
                },
                maxLines = 1,
                singleLine = true,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                visualTransformation = PasswordVisualTransformation()
            )
        }
        item {
            TextField(
                value = talkerPasswordAgain,
                onValueChange = {
                    talkerPasswordAgain = it
                },
                label = {
                    Text("确认密码")
                },
                maxLines = 1,
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                visualTransformation = PasswordVisualTransformation()
            )
        }
        item {
            Row (
                modifier = Modifier
                    .padding(vertical = 10.dp)
            ){
                TextField(
                    value = captcha,
                    onValueChange = {
                        captcha = it
                    },
                    label = {
                        Text("验证码")
                    },
                    maxLines = 1,
                    singleLine = true,
                    modifier = Modifier
                        .then(if(editAble) Modifier.weight(1f).padding(end = 10.dp) else Modifier.width(0.dp))
                        .height(56.dp)
                        .animateContentSize()
                )
                Row(
                    modifier = Modifier
                        .then(
                            if (!editAble) Modifier.weight(1f).height(56.dp) else Modifier.size(56.dp)
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Green)
                        .animateContentSize()
                        .clickable {
                            if(!editAble) {
                                editAble = !editAble
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (!editAble) Icons.Filled.Share else Icons.Filled.Refresh,
                        "",
                        modifier = Modifier
                            .size(56.dp)
                            .wrapContentSize(Alignment.Center)
                            .fillMaxSize(0.5f)
                    )
                    Text(
                        "申请验证码",
                        modifier = Modifier
                            .weight(1f)
                    )
                }
            }
        }
        item {
            Text(
                modifier = Modifier
                    .padding( top = 20.dp )
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                text = "FuTalk 开发团队确保不会在未经您允许情况下故意泄露您的信息，教务处账号仅用作您的福大身份确认",
                textAlign = TextAlign.Center
            )
        }
        item{
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ){
                Button(
                    onClick = {
                        navigateToLogin.invoke()
                    },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        vertical = 10.dp,
                        horizontal = 20.dp
                    ),
                    enabled = !isRegister
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        "",
                        modifier = Modifier
                            .padding(end = 10.dp)
                    )
                    Text("我已注册")
                }
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        vertical = 10.dp,
                        horizontal = 20.dp
                    ),
                    modifier = Modifier
                        .padding( start = 20.dp )
                        .weight(1f),
                    enabled = registerAble.value
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        "",
                        modifier = Modifier
                            .padding(end = 10.dp)
                    )
                    Text("注册")
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
    AnimatedVisibility(
        toast!=null,
        exit =  slideOutVertically() { 0 } + shrinkVertically() + fadeOut(tween(5000)),
        enter = slideInVertically{0}
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Cyan)
                .padding(10.dp)
        ){
            Text(
                if( toast != null ) toast!! else "",
                modifier = Modifier
                    .align(Alignment.Center),
                textAlign = TextAlign.Center
            )
        }
    }
}