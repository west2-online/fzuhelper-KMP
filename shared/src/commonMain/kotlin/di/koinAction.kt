package di

import cafe.adriel.voyager.navigator.Navigator
import com.liftric.kvault.KVault
import config.BaseUrlConfig
import configureForPlatform
import dao.ClassScheduleDao
import dao.Dao
import dao.ExamDao
import dao.ThemeKValueAction
import dao.TokenKValueAction
import dao.UndergraduateKValueAction
import dao.YearOpensDao
import initStore
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.readBytes
import io.ktor.client.statement.request
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.encodeBase64
import io.ktor.util.pipeline.PipelinePhase
import kotlin.random.nextInt
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module
import repository.ClassScheduleRepository
import repository.EmptyHouseRepository
import repository.FeedbackRepository
import repository.LoginRepository
import repository.ManageRepository
import repository.ModifierInformationRepository
import repository.NewRepository
import repository.PersonRepository
import repository.PostRepository
import repository.ReportRepository
import repository.RibbonRepository
import repository.SplashRepository
import repository.WeatherRepository
import ui.compose.Action.ActionViewModel
import ui.compose.Authentication.AuthenticationViewModel
import ui.compose.ClassSchedule.ClassScheduleViewModel
import ui.compose.EmptyHouse.EmptyHouseVoyagerViewModel
import ui.compose.Feedback.FeedBackViewModel
import ui.compose.Log.LogViewModel
import ui.compose.Manage.ManageViewModel
import ui.compose.ModifierInformation.ModifierInformationViewModel
import ui.compose.Person.PersonViewModel
import ui.compose.Post.PostDetailViewModel
import ui.compose.Post.PostListViewModel
import ui.compose.Release.ReleasePageViewModel
import ui.compose.Report.ReportViewModel
import ui.compose.Setting.SettingViewModel
import ui.compose.SplashPage.SplashPageViewModel
import ui.compose.UndergraduateWebView.UndergraduateWebViewViewModel
import ui.compose.Weather.WeatherViewModel
import ui.compose.Webview.WebviewViewModel
import ui.root.RootAction
import util.compose.Toast
import util.encode.encode
import viewModelDefinition

/**
 * 对教务处client的相关处理
 *
 * @property classScheduleRepository ClassScheduleRepository 有关
 * @property kVaultAction UndergraduateKValueAction
 * @property toast Toast 用于提示的toast
 * @property client HttpClient? 用于储存可用的教务处client
 * @property userSchoolId String? 储存可用的学生id
 * @property upDataTime Instant client更新时间
 * @constructor
 */
class ClassSchedule(
  private val classScheduleRepository: ClassScheduleRepository,
  private val kVaultAction: UndergraduateKValueAction,
  val toast: Toast,
) {
  private var client: HttpClient? = null
  private var userSchoolId: String? = null
  private var upDataTime = Clock.System.now().plus(-50, DateTimeUnit.MINUTE)

  @OptIn(ExperimentalCoroutinesApi::class)
  /** 获取可用于教务处请求的 client和学生id 如果失败则会返回Pair(null,null) */
  suspend fun getClassScheduleClient(): Pair<HttpClient?, String?> {
    if (client == null || Clock.System.now() - upDataTime > 20.toDuration(DurationUnit.MINUTES)) {
      val newClient =
        HttpClient() {
          install(ContentNegotiation) { json() }
          install(HttpCookies) {}
          install(HttpRedirect) { checkHttpMethod = false }
          configureForPlatform()
        }
      classScheduleRepository.apply {
        val userName = kVaultAction.schoolUserName.currentValue.value
        val password = kVaultAction.schoolPassword.currentValue.value
        if (userName == null || password == null) {
          toast.addWarnToast("未登录,请到设置中登录")
          return@apply
        }
        var id = ""
        var num = ""
        newClient.apply {
          getVerifyCode()
            .map { it.encodeBase64() }
            .flatMapConcat { verifyCodeForParse -> parseVerifyCodeFormWest2(verifyCodeForParse) }
            .flatMapConcat { verifyCode ->
              loginStudent(user = userName, pass = password, verifyCode = verifyCode)
            }
            .flatMapConcat {
              val url = it.call.request.url.toString()
              id = url.split("id=")[1].split("&")[0]
              num = url.split("num=")[1].split("&")[0]

              val context = it.readBytes().decodeToString()
              val token = context.split("var token = \"")[1].split("\";")[0]
              loginByToken(token)
            }
            .flatMapConcat { loginCheckXs(id = id, num = num) }
            .retry(3)
            .map {
              val url = it.call.request.url.toString()
              client = newClient
              userSchoolId = url.split("id=")[1].split("&")[0]
              upDateClientTime()
            }
            .catch {
              client = null
              userSchoolId = null
            }
            .collect {}
        }
      }
      return Pair(newClient, userSchoolId)
    }
    return Pair(client, userSchoolId)
  }

  /** Up date client time 更新client更新时间 */
  private fun upDateClientTime() {
    upDataTime = Clock.System.now()
  }

  /**
   * 用于验证账号密码是否正确
   *
   * @param userName String
   * @param password String
   * @param failAction SuspendFunction1<VerifyYourAccountError, Unit> 错误调用的逻辑
   * @param success SuspendFunction0<Unit> 正确调用的逻辑
   */
  @OptIn(ExperimentalCoroutinesApi::class)
  suspend fun verifyYourAccount(
    userName: String,
    password: String,
    failAction: suspend (VerifyYourAccountError) -> Unit,
    success: suspend () -> Unit,
  ) {
    val newClient =
      HttpClient() {
        install(ContentNegotiation) { json() }
        install(HttpCookies) {}
        install(HttpRedirect) { checkHttpMethod = false }
        configureForPlatform()
      }
    classScheduleRepository.apply {
      var id = ""
      var num = ""
      newClient.apply {
        getVerifyCode()
          .map { it.encodeBase64() }
          .catch { failAction.invoke(VerifyYourAccountError.ValidationFailed) }
          .flatMapConcat { verifyCodeForParse -> parseVerifyCodeFormWest2(verifyCodeForParse) }
          .flatMapConcat { verifyCode ->
            loginStudent(user = userName, pass = password, verifyCode = verifyCode)
          }
          .flatMapConcat {
            val url = it.call.request.url.toString()
            id = url.split("id=")[1].split("&")[0]
            num = url.split("num=")[1].split("&")[0]

            val context = it.readBytes().decodeToString()
            val token = context.split("var token = \"")[1].split("\";")[0]
            loginByToken(token)
          }
          .flatMapConcat { loginCheckXs(id = id, num = num) }
          .retry(3)
          .catch { failAction.invoke(VerifyYourAccountError.LoginFailed) }
          .collect { success.invoke() }
      }
    }
  }

  /**
   * Verify your account error
   *
   * @constructor Create empty Verify your account error
   */
  enum class VerifyYourAccountError {
    ValidationFailed,
    LoginFailed,
  }
}

/**
 * 用于登录futalk的客户端
 *
 * @property client HttpClient
 * @constructor
 */
class LoginClient(
  val client: HttpClient =
    HttpClient {
        install(ContentNegotiation) { json() }
        install(DefaultRequest) { url(BaseUrlConfig.BaseUrl) }
        install(Logging)
        install(HttpCookies) {}
        install(HttpRedirect) { checkHttpMethod = false }
        configure()
      }
      .encodeAction()
)

class SchoolClient(
  val client: HttpClient =
    HttpClient {
        install(ContentNegotiation) { json() }
        install(DefaultRequest) { url(BaseUrlConfig.BaseUrl) }
        install(Logging)
        install(HttpCookies) {}
        install(HttpRedirect) { checkHttpMethod = false }
        configure()
      }
      .encodeAction()
)

/**
 * 用于共享的客户端
 *
 * @property client HttpClient
 * @constructor
 */
class ShareClient(
  val client: HttpClient =
    HttpClient {
        install(ContentNegotiation) {
          json(
            Json {
              ignoreUnknownKeys = true
              encodeDefaults = true
              isLenient = true
              allowSpecialFloatingPointValues = true
              allowStructuredMapKeys = true
              prettyPrint = false
              useArrayPolymorphism = false
            }
          )
        }
        install(Logging)
        install(HttpRedirect) { checkHttpMethod = false }
        configure()
      }
      .encodeAction()
)

/**
 * 用于web程序的客户端
 *
 * @property client HttpClient
 * @constructor
 */
class WebClient(
  val client: HttpClient =
    HttpClient {
        install(ContentNegotiation) {
          json(
            Json {
              ignoreUnknownKeys = true
              encodeDefaults = true
              isLenient = true
              allowSpecialFloatingPointValues = true
              allowStructuredMapKeys = true
              prettyPrint = false
              useArrayPolymorphism = false
            }
          )
        }
        install(Logging)
        install(HttpRedirect) { checkHttpMethod = false }
        configure()
      }
      .encodeAction()
)

class SystemAction(val onBack: () -> Unit, val onFinish: () -> Unit)

/**
 * 用于全局di
 *
 * @param rootAction RootAction 对主页面路由的控制
 * @param systemAction SystemAction 系统逻辑，如退出软件
 * @param navigator Navigator 主页面路由
 * @return Module
 */
fun appModule(rootAction: RootAction, systemAction: SystemAction, navigator: Navigator) = module {
  single { rootAction }
  single { ThemeKValueAction(get()) }
  single { UndergraduateKValueAction(get()) }
  single { ClassSchedule(get(), get(), get()) }
  single { systemAction }
  single { navigator }
  single {
    val client = HttpClient {
      install(ContentNegotiation) {
        json(
          Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            isLenient = true
            allowSpecialFloatingPointValues = true
            allowStructuredMapKeys = true
            prettyPrint = false
            useArrayPolymorphism = false
          }
        )
      }
      install(DefaultRequest) {
        val tokenKValueAction = get<TokenKValueAction>()
        val token = tokenKValueAction.token.currentValue
        token.value?.let { headers.append("Authorization", it) }
        url(BaseUrlConfig.BaseUrl)
      }
      install(Logging) { level = LogLevel.BODY }
      install(HttpCookies) {}
      install(HttpRedirect) { checkHttpMethod = false }
      configure()
    }
    val authPhase = PipelinePhase("Auth")
    client.receivePipeline.insertPhaseBefore(HttpReceivePipeline.Before, authPhase)
    client.receivePipeline.intercept(authPhase) {
      if (it.status.value == 555) {
        val kVault = get<KVault>()
        kVault.clear()
        get<RootAction>().reLogin()
      }
      if (it.status.value == 556) {
        val toast = get<Toast>()
        toast.addWarnToast("网络延迟过大")
      }
      if (it.status.value == 557) {
        get<RootAction>().popManage()
      }
    }
    if (false) {
      val LogRequest = PipelinePhase("LogRequest")
      client.receivePipeline.insertPhaseAfter(HttpReceivePipeline.After, LogRequest)
      client.receivePipeline.intercept(LogRequest) {
        println("----------------------request---------------------------")
        println("request ${it.request.url} ${it.request.method}")
        it.request.headers.forEach { s, strings -> println("header --> $s -> ${strings}") }
      }

      val LogResponse = PipelinePhase("LogResponse")
      client.receivePipeline.insertPhaseAfter(HttpReceivePipeline.After, LogResponse)
      client.receivePipeline.intercept(LogResponse) {
        println("----------------------response---------------------------")
        println("request ${it.request.url} ${it.request.method}")
        it.headers.forEach { s, strings -> println("header --> $s -> $strings") }
      }
    }
    return@single client.encodeAction()
  }
  single { TokenKValueAction(get()) }
  repositoryList()
  viewModel()
  single { initStore() }
  single { LoginClient() }
  single { ShareClient() }
  single { WebClient() }
  single { Toast(globalScope) }
  single { Dao(get(), get(), get()) }
  single { ClassScheduleDao() }
  single { ExamDao() }
  single { YearOpensDao() }
}

/**
 * 对仓库层的注入
 *
 * @receiver Module
 */
fun Module.repositoryList() {
  single { SplashRepository(get()) }
  single { LoginRepository(get()) }
  single { NewRepository(get()) }
  single { PersonRepository(get()) }
  single { PostRepository(get()) }
  single { FeedbackRepository(get(), get()) }
  single { ModifierInformationRepository(get()) }
  single { WeatherRepository(get()) }
  single { ReportRepository(get()) }
  single { ManageRepository(get()) }
  single { RibbonRepository(get()) }
  single { EmptyHouseRepository(get()) }
  single { ClassScheduleRepository() }
}

/**
 * 对viewModel的注入
 *
 * @receiver Module
 */
fun Module.viewModel() {
  viewModelDefinition { AuthenticationViewModel(get(), get(), get()) }
  single { ActionViewModel(get(), get()) }
  single { SplashPageViewModel(get(), get()) }
  single { PostListViewModel(get(), get(), get(), get()) }
  single { FeedBackViewModel(get()) }
  single { ReportViewModel(get()) }
  single { PostDetailViewModel(get(), get(), get()) }
  single { ClassScheduleViewModel(get(), get(), get(), get(), get()) }
  single { SettingViewModel(get(), get()) }
  viewModelDefinition { ManageViewModel(get(), get()) }
  single { PersonViewModel(get(), get()) }
  viewModelDefinition { WeatherViewModel(get()) }
  viewModelDefinition { ReleasePageViewModel(get()) }
  viewModelDefinition { ModifierInformationViewModel(get()) }
  viewModelDefinition { LogViewModel() }
  viewModelDefinition { EmptyHouseVoyagerViewModel(get()) }
  single { WebviewViewModel() }
  single { UndergraduateWebViewViewModel(get()) }
}

/**
 * 对client的特定平台设置，主要是为了兼容https
 *
 * @receiver HttpClientConfig<*>
 */
fun HttpClientConfig<*>.configure() {
  configureForPlatform()
}

/**
 * 添加特定的header，防止爬虫
 *
 * @return HttpClient
 * @receiver HttpClient
 */
fun HttpClient.encodeAction(): HttpClient {
  return this.apply {
    val encodePhase = PipelinePhase("Encode")
    this.requestPipeline.insertPhaseBefore(
      io.ktor.client.request.HttpRequestPipeline.Send,
      encodePhase,
    )
    this.requestPipeline.intercept(encodePhase) {
      val time = kotlinx.datetime.Clock.System.now().toEpochMilliseconds() / 1000
      val randomNumber1 = kotlin.random.Random.nextInt(10..99)
      val randomNumber2 = kotlin.random.Random.nextInt(1..9)
      this.context.headers.append(
        "Encode",
        "${randomNumber1}${randomNumber2}_${encode(randomNumber1,randomNumber2,time)}",
      )
    }
  }
}
