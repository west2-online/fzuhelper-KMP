package ui.compose.Authentication

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.painterResource
import kotlin.jvm.Transient
import org.example.library.MR
import org.koin.compose.koinInject
import repository.TokenData
import util.compose.EasyToast
import util.compose.ParentPaddingControl
import util.compose.Toast
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.network.CollectWithContent
import util.network.NetworkResult
import util.network.logicWithTypeWithLimit

/**
 * 注册的ui
 *
 * @param modifier Modifier
 * @param captchaState State<NetworkResult<String>> 获取futalk邮箱验证码
 * @param registerState State<NetworkResult<String>> 注册的结果
 * @param getCaptcha Function1<[@kotlin.ParameterName] String, Unit> 获取futalk验证码
 * @param register Function5<[@kotlin.ParameterName] String, [@kotlin.ParameterName] String,
 *   [@kotlin.ParameterName] String, [@kotlin.ParameterName] String, [@kotlin.ParameterName] String,
 *   Unit> 注册的逻辑
 * @param navigateToLogin Function0<Unit> 转到登录页
 * @param verifyStudentID Function3<[@kotlin.ParameterName] String, [@kotlin.ParameterName] String,
 *   [@kotlin.ParameterName] String, Unit> 验证用户的教务处账号密码
 * @param studentCaptchaState State<NetworkResult<ImageBitmap>> 教务处验证码的结果
 * @param getStudentCaptcha Function0<Unit> 获取教务处验证码
 * @param verifyStudentIDState State<NetworkResult<TokenData>> 验证学生的身份信息
 */
@Composable
fun Register(
  modifier: Modifier,
  captchaState: State<NetworkResult<String>>,
  registerState: State<NetworkResult<String>>,
  getCaptcha: (email: String) -> Unit,
  register:
    (
      email: String, password: String, captcha: String, studentCode: String, studentPassword: String,
    ) -> Unit,
  navigateToLogin: () -> Unit,
  verifyStudentID: (studentCode: String, studentPassword: String, studentCaptcha: String) -> Unit,
  studentCaptchaState: State<NetworkResult<ImageBitmap>>,
  getStudentCaptcha: () -> Unit,
  verifyStudentIDState: State<NetworkResult<TokenData>>,
) {
  var studentCode by remember { mutableStateOf("") }
  var studentPassword by remember { mutableStateOf("") }
  var studentCaptcha by remember { mutableStateOf("") }
  var talkerEmail by remember { mutableStateOf("") }
  var talkerPassword by remember { mutableStateOf("") }
  var talkerPasswordAgain by remember { mutableStateOf("") }
  var captcha by remember { mutableStateOf("") }
  var editAble by remember { mutableStateOf(false) }
  val weight by animateFloatAsState(if (editAble) 1f else 0f)
  val isRegister by remember {
    derivedStateOf {
      registerState.value is NetworkResult.LoadingWithAction ||
        captchaState.value is NetworkResult.LoadingWithAction
    }
  }
  val registerAble = remember {
    derivedStateOf {
      !isRegister &&
        talkerPassword != "" &&
        talkerPasswordAgain != "" &&
        talkerEmail != "" &&
        captcha != "" &&
        verifyStudentIDState.value is NetworkResult.Success
    }
  }

  //    val toast = rememberToastState()
  val scope = rememberCoroutineScope()
  val toast = remember { Toast(scope) }

  LaunchedEffect(verifyStudentIDState.value) {
    if (verifyStudentIDState.value is NetworkResult.Error<*>) {
      toast.addToast("验证失败，检查你的信息")
      getStudentCaptcha.invoke()
    }
  }

  LaunchedEffect(registerState.value) {
    registerState.value.let {
      when (it) {
        is NetworkResult.Success -> {
          //                    toast = Toast(it.data)
          toast.addToast(it.dataForShow)
        }
        is NetworkResult.Error -> {
          //                    toast = Toast( it.error.message.toString(), Red )
          toast.addToast(it.errorForShow.message.toString())
        }
        else -> {}
      }
    }
  }
  LaunchedEffect(captchaState.value) {
    captchaState.value.let {
      when (it) {
        is NetworkResult.Success -> {
          toast.addToast(it.dataForShow)
        }
        is NetworkResult.Error -> {
          toast.addToast(it.errorForShow.message.toString())
        }
        else -> {}
      }
    }
  }
  LaunchedEffect(Unit) { getStudentCaptcha() }
  Box(modifier = modifier) {
    LazyColumn(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
      item {
        Image(
          painter = painterResource(MR.images.FuTalk),
          "",
          modifier = Modifier.fillMaxWidth(0.7f).aspectRatio(1f).padding(vertical = 30.dp),
        )
      }
      item {
        verifyStudentIDState.CollectWithContent(
          success = {
            Box(modifier = Modifier.fillMaxWidth().height(56.dp).background(Color.Green)) {
              Text(modifier = Modifier.align(Alignment.Center), text = "已通过验证")
            }
          },
          content = {
            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
              TextField(
                value = studentCode,
                onValueChange = { studentCode = it },
                label = { Text("学号") },
                maxLines = 1,
                singleLine = true,
                modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth().height(56.dp),
              )
              TextField(
                value = studentPassword,
                onValueChange = { studentPassword = it },
                label = { Text("教务处密码") },
                maxLines = 1,
                singleLine = true,
                modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth().height(56.dp),
                visualTransformation = PasswordVisualTransformation(),
              )
              Row {
                studentCaptchaState.CollectWithContent(
                  content = {
                    Box(modifier = Modifier.fillMaxSize().clickable { getStudentCaptcha.invoke() })
                  },
                  loading = {
                    Box(modifier = Modifier.fillMaxSize()) {
                      CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                  },
                  error = {
                    Box(
                      modifier =
                        Modifier.fillMaxSize().background(Color.Red).clickable {
                          getStudentCaptcha.invoke()
                        }
                    ) {
                      Text("获取失败", modifier = Modifier.align(Alignment.Center))
                    }
                  },
                  success = {
                    Image(
                      bitmap = it,
                      null,
                      modifier =
                        Modifier.fillMaxSize().background(color = Color.Red).clickable {
                          getStudentCaptcha.invoke()
                        },
                      contentScale = ContentScale.FillBounds,
                    )
                  },
                  modifier =
                    Modifier.padding(vertical = 10.dp).weight(1f).height(56.dp).padding(end = 5.dp),
                )
                TextField(
                  value = studentCaptcha,
                  onValueChange = { studentCaptcha = it },
                  label = { Text("验证码") },
                  maxLines = 1,
                  singleLine = true,
                  modifier = Modifier.padding(vertical = 10.dp).weight(1f).height(56.dp),
                )
              }
              Button(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                onClick = { verifyStudentID.invoke(studentCode, studentPassword, studentCaptcha) },
              ) {
                Text("验证")
              }
            }
          },
          loading = {
            Row(
              modifier = Modifier.fillMaxWidth().height(56.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
            ) {
              Box(modifier = Modifier.fillMaxHeight(0.7f).aspectRatio(1f)) {
                CircularProgressIndicator()
              }
              Text("正在验证")
            }
          },
        )
      }
      item {
        TextField(
          value = talkerEmail,
          onValueChange = { talkerEmail = it },
          label = { Text("邮箱") },
          maxLines = 1,
          singleLine = true,
          modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth().height(56.dp),
        )
      }
      item {
        TextField(
          value = talkerPassword,
          onValueChange = { talkerPassword = it },
          label = { Text("密码") },
          maxLines = 1,
          singleLine = true,
          modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth().height(56.dp),
          visualTransformation = PasswordVisualTransformation(),
        )
      }
      item {
        TextField(
          value = talkerPasswordAgain,
          onValueChange = { talkerPasswordAgain = it },
          label = { Text("确认密码") },
          maxLines = 1,
          singleLine = true,
          modifier = Modifier.fillMaxWidth().height(56.dp),
          visualTransformation = PasswordVisualTransformation(),
        )
      }
      item {
        Row(modifier = Modifier.padding(vertical = 10.dp)) {
          TextField(
            value = captcha,
            onValueChange = { captcha = it },
            label = { Text("验证码") },
            maxLines = 1,
            singleLine = true,
            modifier =
              Modifier.then(
                  if (editAble) Modifier.weight(1f).padding(end = 10.dp) else Modifier.width(0.dp)
                )
                .height(56.dp)
                .animateContentSize(),
          )
          Row(
            modifier =
              Modifier.then(
                  if (!editAble) Modifier.weight(1f).height(56.dp) else Modifier.size(56.dp)
                )
                .clip(RoundedCornerShape(10.dp))
                .background(if (isSystemInDarkTheme()) Color.Gray else Color.Green)
                .animateContentSize()
                .clickable {
                  if (!editAble) {
                    editAble = !editAble
                  }
                  getCaptcha.invoke(talkerEmail)
                },
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              painter =
                if (!editAble) painterResource(MR.images.cloud_key)
                else rememberVectorPainter(Icons.Filled.Refresh),
              "",
              modifier = Modifier.size(56.dp).wrapContentSize(Alignment.Center).fillMaxSize(0.5f),
            )
            Text("申请验证码", modifier = Modifier.weight(1f))
          }
        }
      }
      item {
        Text(
          modifier =
            Modifier.padding(top = 20.dp).fillMaxWidth().wrapContentHeight().padding(10.dp),
          text = "FuTalk 开发团队确保不会在未经您允许情况下故意泄露您的信息，教务处账号仅用作您的福大身份确认",
          textAlign = TextAlign.Center,
        )
      }
      item {
        Row(
          modifier = Modifier.fillMaxWidth().wrapContentHeight(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center,
        ) {
          Button(
            onClick = { navigateToLogin.invoke() },
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 20.dp),
            enabled = !isRegister,
          ) {
            Icon(
              painter = painterResource(MR.images.login),
              "",
              modifier = Modifier.size(40.dp).wrapContentSize(Alignment.Center).fillMaxSize(0.6f),
            )
            Text("我已注册")
          }
          Button(
            onClick = {
              if (registerAble.value) {
                register(talkerEmail, talkerPassword, captcha, studentCode, studentPassword)
              } else {
                toast.addToast("信息不足或未通过学生身份验证")
              }
            },
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(vertical = 10.dp, horizontal = 20.dp),
            modifier = Modifier.padding(start = 20.dp).weight(1f),
          ) {
            Icon(
              painter = painterResource(MR.images.register),
              "",
              modifier = Modifier.size(40.dp).wrapContentSize(Alignment.Center).fillMaxSize(0.6f),
            )

            Text("注册")
          }
        }
      }
      item { Spacer(modifier = Modifier.height(50.dp)) }
    }
    EasyToast(toast)
  }
}

/**
 * 注册的二级界面
 *
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class RegisterVoyagerScreen(
  @Transient val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) : Screen {
  @Composable
  override fun Content() {
    val navigate = LocalNavigator.currentOrThrow
    val viewModel = koinInject<AuthenticationViewModel>()
    viewModel.registerState.collectAsState().value.logicWithTypeWithLimit {
      navigate.replaceAll(LoginVoyagerScreen())
    }
    Register(
      modifier = Modifier.fillMaxSize().parentSystemControl(parentPaddingControl).padding(10.dp),
      register = { email, password, captcha, studentCode, studentPassword ->
        viewModel.register(email, password, captcha, studentCode, studentPassword)
      },
      getCaptcha = { email -> viewModel.getRegisterCaptcha(email) },
      captchaState = viewModel.registerCaptcha.collectAsState(),
      registerState = viewModel.registerState.collectAsState(),
      verifyStudentID = { studentCode, studentPassword, studentCaptcha ->
        viewModel.verifyStudentID(studentCode, studentPassword, captcha = studentCaptcha)
      },
      navigateToLogin = { navigate.replaceAll(LoginVoyagerScreen()) },
      studentCaptchaState = viewModel.studentCaptcha.collectAsState(),
      getStudentCaptcha = { viewModel.refreshStudentCaptcha() },
      verifyStudentIDState = viewModel.verifyStudentIDState.collectAsState(),
    )
  }
}
