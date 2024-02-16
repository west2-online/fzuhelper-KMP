package ui.compose.AboutUs

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.mikepenz.markdown.compose.Markdown
import util.compose.loadAction

@Composable
fun AboutUsScreen(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ){
                Box(modifier = Modifier.fillMaxSize(0.5f).align(Alignment.Center).loadAction())
        }
        Markdown(
            content = markdown,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
        )
    }
}

val markdown = """
### 欢迎使用FuTalk🤗

**👏初创成员**
 
- 沈轻腾
- 沈轻腾
- 沈轻腾
 
**👻FuTalk与学校关系**
 
 **FuTalk** 属于私人开发，未得到 **福州大学** 的任何资金支持，并没有密切关系，您在 **FuTalk** 上的信息并不会共享给学校，大家可以大胆发言哦 😉😉
 
**🤠关于软件**

该软件仍在开发阶段，我们仍在积极和各个社团展开合作，并完善软件，敬请期待
 
**👀反馈**
 
您的反馈对我们非常重要，任何关于软件的反馈都可以在软件的 **反馈模块** 添加反馈 或 在 **GitHub** 上向我们提出issue 🧐🧐

 
**🌏Github地址**

https://github.com/Futalker/FuTalk_Apk

""".trimIndent()

class AboutUsRouteNode(
    buildContext:BuildContext
):Node(
    buildContext = buildContext
){
    @Composable
    override fun View(modifier: Modifier) {
        AboutUsScreen(modifier)
    }
}