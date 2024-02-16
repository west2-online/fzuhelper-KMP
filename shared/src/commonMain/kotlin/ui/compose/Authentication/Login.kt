package ui.compose.Authentication

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import org.example.library.MR
import util.compose.EasyToast
import util.compose.rememberToastState
import util.network.NetworkResult

@Composable
fun Login(
    modifier: Modifier,
    navigateToRegister:()->Unit,
    login:(userEmail:String,userPassword:String,captcha:String)->Unit,
    getCaptcha:(userEmail:String)->Unit,
    loginState: State<NetworkResult<String>>,
    loginCaptcha:State<NetworkResult<String>>,
    cleanRegisterData:()->Unit
){
    var userEmail by remember {
        mutableStateOf("")
    }
    var userPassword by remember {
        mutableStateOf("")
    }
    var captcha by remember {
        mutableStateOf("")
    }
    var editAble by remember {
        mutableStateOf(false)
    }
    val toast = rememberToastState()
    val registerAble = remember {
        derivedStateOf {
            loginState.value !is NetworkResult.Loading && userEmail != "" && userPassword!="" && captcha!=""
        }
    }
    DisposableEffect(Unit){
        onDispose {
            cleanRegisterData()
        }
    }
    LaunchedEffect(loginState.value,loginState.value.key.value){
        loginState.value.let {
            when( it ){
                is NetworkResult.Success<String>->{
                    toast.addToast( it.data )
                }
                is NetworkResult.Error -> {
                    toast.addToast( it.error.message.toString() , Color.Red )
                }

            }
        }
    }
    LaunchedEffect(loginCaptcha.value){
        loginCaptcha.value.let {
            when( it ){
                is NetworkResult.Success<String>->{
                    toast.addToast( it.data )
                }
                is NetworkResult.Error -> {
                    toast.addToast( it.error.message.toString() , Color.Red )
                }

            }
        }
    }
    Box( modifier = Modifier.fillMaxSize() ){
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
                    value = userEmail,
                    onValueChange = {
                        userEmail = it
                    },
                    label = {
                        Text("邮箱")
                    },
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    singleLine = true
                )
            }
            item {
                TextField(
                    value = userPassword,
                    onValueChange = {
                        userPassword = it
                    },
                    label = {
                        Text("密码")
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    singleLine = true
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
                            .background(
                                if (isSystemInDarkTheme()) Color.Gray else Color.Green
                            )
                            .animateContentSize()
                            .clickable {
                                if(!editAble) {
                                    editAble = !editAble
                                }
                                getCaptcha(userEmail)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = if (!editAble) painterResource(MR.images.cloud_key) else rememberVectorPainter(
                                Icons.Filled.Refresh),
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
                            navigateToRegister.invoke()
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(
                            vertical = 10.dp,
                            horizontal = 20.dp
                        ),
                        enabled = loginState.value !is NetworkResult.Loading
                    ) {
                        Icon(
                            painter = painterResource(MR.images.register),
                            "",
                            modifier = Modifier
                                .size(40.dp)
                                .wrapContentSize(Alignment.Center)
                                .fillMaxSize(0.6f)
                        )
                        Text("未有账号")
                    }
                    Button(
                        onClick = {
                            login.invoke(userEmail,userPassword,captcha)
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(
                            vertical = 10.dp,
                            horizontal = 20.dp
                        ),
                        modifier = Modifier
                            .padding( start = 20.dp )
                            .weight(1f),
                        enabled = registerAble.value
                    ) {
                        Icon(
                            painter = painterResource(MR.images.login),
                            "",
                            modifier = Modifier
                                .size(40.dp)
                                .wrapContentSize(Alignment.Center)
                                .fillMaxSize(0.6f)
                        )
                        Text("登录")
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
        EasyToast(toast)
    }
}