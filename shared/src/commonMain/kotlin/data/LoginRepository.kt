package data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import io.ktor.util.InternalAPI
import io.ktor.utils.io.charsets.Charset
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import repository.register.bean.AuthenticationResponse

class LoginRepository(private val client : HttpClient) {
    fun getCaptcha(email:String):Flow<AuthenticationResponse>{
        return flow{
            val response:AuthenticationResponse = client.submitForm(
                url = "http://localhost:8080/signup",
                formParameters = parameters {
                    append("email", email)
                }
            ).body()
            emit(response)
        }
    }
    fun register(email:String,password:String,captcha:String):Flow<AuthenticationResponse>{
        return flow{
            val response:AuthenticationResponse = client.submitForm(
                url = "http://localhost:8080/signup",
                formParameters = parameters {
                    append("email", email)
                    append("password",password)
                    append("captcha",captcha)
                }
            ).body()
            emit(response)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun verifyStudentIdentity(userName:String, password: String, captcha:String): Flow<TokenData> {
        return loginStudent(
            pass = password,
            user = userName,
            captcha = captcha,
        )
//            .flatMapConcat {
//                loginByTokenForIdInUrl(
//                    result = it,
//                    failedToGetAccount = {
//
//                    },
//                    elseMistake = {
//
//                    }
//                )
////                .retryWhen{ error,tryTime ->
////                    error.message == "获取account失败" && tryTime <= 3
////                }
////                .catch {
//////                    if(it.message == "获取account失败"){
//////                        println("获取account失败")
//////                    }
//////                    else{
//////
//////                    }
////                    println("ssss ${it.message}")
////                }
//            }
//            .catch {
//                println("ssss1 ${it.message}")
//            }
//            .flatMapConcat {
//                loadCookieData(
//                    queryMap = it,
//                    user = userName
//                )
////                    .catchWithMassage {
////                        loginButtonState.value = true
////                        getVerificationCodeFromNetwork()
////                    }
//            }
//            .catch {
//                println("ssss2 ${it.message}")
//            }
//            .flatMapConcat {
//                checkTheUserInformation(
//                    user = userName,
//                    serialNumberHandling = {
//
//                    },
//                    id = it
//                )
////               .catchWithMassage {
////                    loginButtonState.value = true
////                    getVerificationCodeFromNetwork()
////                }
//
//            }
//            .catch {
//                println("ssss3 ${it.message}")
//            }
//            .collectWithError{ loginResult ->
//                when(loginResult){
//                    LoginResult.LoginError->{
//                        easyToast("登录失败,请重新登录")
//                        loginButtonState.value = true
//                        getVerificationCodeFromNetwork()
//                        loginFailed.invoke()
//                    }
//                    LoginResult.LoginSuccess->{
//                        easyToast("登录成功")
//                        withContext(Dispatchers.Main){
//                            loginSuccessful.invoke()
//                        }
//                    }
//                }
//            }
    }

    //登录 step1
    fun loginStudent(
        user: String,
        pass: String,
        captcha: String = "",
        tryTimes: Int = 0,
        theUserDoesNotExistAction: () -> Unit = {},
        captchaVerificationFailedAction: () -> Unit = {},
        thePasswordIsIncorrectAction: () -> Unit = {},
        errorNotExplainedAction: () -> Unit = {},
        theOriginalPasswordIsWeakAction: () -> Unit = {},
        networkErrorAction: () -> Unit = {},
        elseErrorAction:()->Unit = {},
        everyErrorAction:(LoginError)->Unit = {}
    ): Flow<TokenData> {
        val exceptionActions = listOf(
            theUserDoesNotExistAction,
            captchaVerificationFailedAction,
            thePasswordIsIncorrectAction,
            errorNotExplainedAction,
            theOriginalPasswordIsWeakAction,
            networkErrorAction,
        )
        return flow {

            val response = client.submitForm (
                url = "https://jwcjwxt1.fzu.edu.cn/logincheck.asp",
                formParameters = Parameters.build {
                    append("muser", "102101624")
                    append("passwd", "351172abc2015@")
                    append("VerifyCode", captcha)
                }
            ){
                contentType(ContentType.Application.FormUrlEncoded)
            }

            if( !response.status.isSuccess() ){
                println(response.status)
                throw LoginError.NetworkError.throwable
            }
//            val data = response.bodyAsText(fallbackCharset = Charset.forName("GB2312"))
            val data = response.bodyAsText(Charset.forName("GBK"))
            println(data)
            LoginError.values().forEach {
                if(data.contains(it.throwable.message.toString())){
                    throw it.throwable
                }
            }
            val token = data.split("var token = \"")[1].split("\";")[0]
            val url = response.request.url.toString()
            emit(
                TokenData( token = token , url = url )
            )
        }
    }

    //step2 中转，匹配Token
    @OptIn(InternalAPI::class)
    fun loginByTokenForIdInUrl(
        result : TokenData,
        failedToGetAccount : (Throwable)->Unit = {},
        elseMistake : (Throwable)->Unit = {}
    ):Flow<HashMap<String,String>>{
        return flow {
//            val body = result.body()?.string()  ?: ""
//            val token = body.split("var token = \"")[1].split("\";")[0]
            val token = result.token
            val url = result.url
//            val response: JwchTokenLoginResponseDto = client.post("https://jwcjwxt2.fzu.edu.cn/Sfrz/SSOLogin") {
//                headers {
//                    append("X-Requested-With", "XMLHttpRequest")
//                }
//                contentType(ContentType.Application.FormUrlEncoded)
//                body = Parameters.build {
//                    append("token", result.token)
//                }
//            }.body()
//            val response: JwchTokenLoginResponseDto = client.submitForm(
//                url = "https://jwcjwxt2.fzu.edu.cn/Sfrz/SSOLogin",
//                formParameters = parameters {
//                    append("token", result.token)
//                }
//            ).body()
            val response = client.submitForm(
                url = "https://jwcjwxt2.fzu.edu.cn/Sfrz/SSOLogin",
                formParameters = Parameters.build {
                    append("token", result.token)

                },
                encodeInQuery = true
            ).body<JwchTokenLoginResponseDto>()

            if(response.code == 200){
                //Step3 获取Url中的id信息
//                val url = result.raw().request.url.toString()
                val id = url.split("id=")[1].split("&")[0]
                val num = url.split("num=")[1].split("&")[0]
                val queryMap = hashMapOf(
                    "id" to id,
                    "num" to num,
                    "ssourl" to "https://jwcjwxt2.fzu.edu.cn",
                    "hosturl" to "https://jwcjwxt2.fzu.edu.cn:81"
                )
                emit(queryMap)
            }
            else if (response.code == 400 && response.info.contains("获取account失败")) {
                //400 重新登录一次试试看
                throw Throwable("获取account失败")
            }
        }
    }

    //Step4 用loginCheckXs接口登录
    private fun loadCookieData(queryMap:HashMap<String,String>,user: String) : Flow<String>{
        return flow {
            //Step4 用loginCheckXs接口登录
            val response = client.get("https://jwcjwxt2.fzu.edu.cn:81/loginchk_xs.aspx") {
                parameters{
                    queryMap.forEach {
                        append(it.key,it.value)
                    }
                }
                contentType(ContentType.Application.FormUrlEncoded)
            }
            if (response.status == HttpStatusCode.OK) {
                val body3 = response.bodyAsText()
                if (body3.contains("福州大学教务处本科教学管理系统")) {
                    val url3 = response.request.url.toString()
                    val id = url3.split("id=")[1].split("&")[0]
                    emit(id)
                }
            }
        }
    }

    //Step5 检查用户信息 防止串号
    private fun checkTheUserInformation(
        user: String,
        serialNumberHandling:()->Unit = {},
        id:String
    ): Flow<LoginResult> {
        return flow {
            //Step5 检查用户信息 防止串号
            val response = client.get("https://yourbaseurl.com/jcxx/xsxx/StudentInformation.aspx") {
                parameter("id", id)
            }.bodyAsText()
            val check = response.contains(user)
            if (check) {
                emit(LoginResult.LoginSuccess)
            } else {
                serialNumberHandling.invoke()
                emit(LoginResult.LoginError)
            }
        }
    }

    fun getVerifyCode(): Flow<ByteArray> {
        return flow {
            val response  = client.get("https://jwcjwxt1.fzu.edu.cn/plus/verifycode.asp").readBytes()
            emit(
                response
            )
        }
    }
}

enum class RegistrationStatus(val value: Int, val description: String) {
    RegisterSuccess(0, "没有问题"),
    TheEmailIsMissingWhenAskFoCaptcha(1, "申请验证码时缺失邮箱"),
    TheEmailIsFormatErrorWhenAskFoCaptcha(2, "申请验证码时，邮箱格式错误"),
    TheVerificationCodeWasNotGenerated(3, "验证码生成失败"),
    EmailFailedToSend(4, "验证码邮件发送失败"),
    TheInformationIsEmpty(5, "注册时信息为空"),
    StorageSystemRegistrationError(6, "因为mysql或者redis的问题导致的无法注册"),
    ThisEmailAddressIsAlreadyRegistered(7, "该电子邮件地址已经注册"),
    TheVerificationCodeIsIncorrect(8, "验证码不正确"),
    CaptchaVerificationFailed(9, "验证码验证失败")
}

data class TokenData(
    val token : String,
    val url : String
)

enum class LoginError(val throwable: Throwable){
    TheUserDoesNotExist(Throwable("不存在该用户")),
    CaptchaVerificationFailed(Throwable("验证码验证失败")),
    ThePasswordIsIncorrect(Throwable("密码错误")),
    ErrorNotExplained(Throwable("未知错误,但不是网络错误")),
    TheOriginalPasswordIsWeak(Throwable("原密码较弱")),
    NetworkError(Throwable("网络错误"))
}

fun Throwable.compareWith(t:LoginError):Boolean{
    return this.message == t.throwable.message
}


enum class LoginResult{
    LoginSuccess,
    LoginError
}

@Serializable
data class JwchTokenLoginResponseDto(
    val code: Int,
    val info: String
)




