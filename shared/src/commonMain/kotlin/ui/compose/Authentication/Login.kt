package ui.compose.Authentication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.coroutines.delay
import org.example.library.MR

@Composable
fun Login(
    modifier: Modifier,
    navigateToRegister:()->Unit
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
    var toast by remember {
        mutableStateOf<String?>(null)
    }
    var isLogin by remember {
        mutableStateOf(false)
    }
    var registerAble = remember {
        derivedStateOf {
            !isLogin && userEmail != "" && userPassword!="" && captcha!=""
        }
    }
    LaunchedEffect(toast){
        delay(2000)
        if (toast!=null){
            toast = null
        }
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
                        .background(Color.Green)
                        .animateContentSize()
                        .clickable {
                            if(!editAble) {
                                editAble = !editAble
                            }else{

                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = if (!editAble) painterResource(MR.images.cloud_key) else rememberVectorPainter(Icons.Filled.Refresh),
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
                    enabled = !isLogin
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
                    onClick = {},
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