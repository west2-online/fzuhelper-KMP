package ui.compose.Report

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.bumble.appyx.utils.multiplatform.RawValue
import data.share.Comment
import data.share.Post

/**
 * Report type 举报类
 *
 * @constructor Create empty Report type
 */
sealed interface ReportType {
  /**
   * 举报帖子
   *
   * @property id String
   * @property data Post
   * @constructor
   */
  data class PostReportType(val id: String, val data: Post) : ReportType

  /**
   * 举报评论
   *
   * @property commentId String
   * @property postId String
   * @property comment Comment
   * @constructor
   */
  data class CommentReportType(
    val commentId: String,
    val postId: String,
    //        val data: Data,
    val comment: Comment,
  ) : ReportType
}

sealed class ReportTarget {

  class PostReportType(val type: @RawValue ReportType.PostReportType) : ReportTarget()

  class CommentReportType(val type: @RawValue ReportType.CommentReportType) : ReportTarget()
}

/**
 * 举报的原因
 *
 * @property code Int
 * @property reason String
 * @property description String
 * @constructor
 */
enum class ReportLabel(val code: Int, val reason: String, val description: String) {
  VIOLATE_COMMUNITY_GUIDELINES(1, "违反社区准则", "发布违反社区准则的内容，包括侮辱、歧视、仇恨言论、骚扰、虚假信息等不良行为"),
  INAPPROPRIATE_CONTENT(2, "色情或不适当内容", "发布成人内容、色情材料、淫秽或其他不适当的内容"),
  COPYRIGHT_ISSUE(3, "版权问题", "发布侵犯他人知识产权的内容，包括未经授权使用的图片、音频或视频等"),
  SPAM_AND_ABUSE(4, "垃圾信息和滥用行为", "发布垃圾信息、恶意链接、恶意软件或其他滥用行为"),
  POLITICALLY_SENSITIVE(5, "政治敏感性", "发布政治敏感的内容可能触犯法律或引起争议"),
  PRIVACY_ISSUE(6, "隐私问题", "公开他人的私人信息、违反隐私权的行为"),
  UNAUTHORIZED_ADVERTISEMENT(7, "不当宣传或广告", "发布未经授权的广告、垃圾邮件或其他形式的不当宣传"),
  MALICIOUS_BEHAVIOR(8, "恶意行为", "参与恶意行为，如网络欺凌、诽谤、虚假信息传播等"),
}

/**
 * 举报界面 二级界面
 *
 * @property type ReportType
 * @constructor
 */
class ReportVoyagerScreen(val type: ReportType) : Screen {
  @Composable
  override fun Content() {
    Box(modifier = Modifier.padding(10.dp)) {
      Navigator(
        when (type) {
          is ReportType.CommentReportType -> CommentReportVoyagerScreen(type)
          is ReportType.PostReportType -> PostRepostVoyagerScreen(type)
        }
      )
    }
  }
}
