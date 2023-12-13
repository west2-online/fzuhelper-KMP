package ui.compose.Report

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.post.PostCommentTree.MainComment
import data.post.PostList.Data

@Composable
fun ReportScreen(
    modifier: Modifier = Modifier,
    type : ReportType
){
    val selectItem = remember {
        mutableStateOf(0)
    }
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ){
        LazyColumn(
            modifier = Modifier
                .weight(1f)
        ) {
            item {
                when(type){
                    is ReportType.PostReportType -> {
                        PostReport(modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(),type.data)
                    }
                    is ReportType.CommentReportType -> {
                        CommentRepost(
                            modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(),
                            commentData = type.comment,
//                            postData = type.data
                        )
                    }
                }
            }
            ReportLabel.values().forEachIndexed { index, reportLabel ->
                item {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(5.dp))
                            .border(1.dp, Color.Gray,RoundedCornerShape(5.dp))
                            .background(
                                animateColorAsState(if(index == selectItem.value) MaterialTheme.colors.error else MaterialTheme.colors.surface).value
                            )
                            .clickable {
                                selectItem.value = index
                            }
                            .padding(5.dp)
                    ){
                        Column {
                            Text(reportLabel.reason)
                            Text(reportLabel.description, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
        Button(
            modifier = Modifier
                .fillMaxWidth(0.4f),
            onClick = {

            }
        ){
            Text("举报")
        }
    }
}

interface ReportType{
    data class PostReportType(
        val id: String,
        val data : Data
    ):ReportType

    data class CommentReportType(
        val commentId: String,
        val postId : String,
//        val data: Data,
        val comment:MainComment
    ):ReportType
}



enum class ReportLabel(val reason: String, val description: String) {
    VIOLATE_COMMUNITY_GUIDELINES("违反社区准则", "发布违反社区准则的内容，包括侮辱、歧视、仇恨言论、骚扰、虚假信息等不良行为"),
    INAPPROPRIATE_CONTENT("色情或不适当内容", "发布成人内容、色情材料、淫秽或其他不适当的内容"),
    COPYRIGHT_ISSUE("版权问题", "发布侵犯他人知识产权的内容，包括未经授权使用的图片、音频或视频等"),
    SPAM_AND_ABUSE("垃圾信息和滥用行为", "发布垃圾信息、恶意链接、恶意软件或其他滥用行为"),
    POLITICALLY_SENSITIVE("政治敏感性", "发布政治敏感的内容可能触犯法律或引起争议"),
    PRIVACY_ISSUE("隐私问题", "公开他人的私人信息、违反隐私权的行为"),
    UNAUTHORIZED_ADVERTISEMENT("不当宣传或广告", "发布未经授权的广告、垃圾邮件或其他形式的不当宣传"),
    MALICIOUS_BEHAVIOR("恶意行为", "参与恶意行为，如网络欺凌、诽谤、虚假信息传播等")
}
