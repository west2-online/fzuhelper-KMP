package ui.compose.Post

import BackHandler
import ImagePickerFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.paging.Pager
import app.cash.paging.compose.collectAsLazyPagingItems
import asImageBitmap
import cafe.adriel.voyager.core.screen.Screen
import config.BaseUrlConfig
import data.post.PostById.FileData
import data.post.PostById.LineChartData
import data.post.PostById.PostContent
import data.post.PostById.ValueData
import data.post.PostById.toLineChartDataForShowOrNull
import data.post.PostCommentPreview.Data
import data.share.Comment
import dev.icerock.moko.resources.compose.painterResource
import getPlatformContext
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.util.decodeBase64String
import kotlin.jvm.Transient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.library.MR
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject
import ui.compose.Post.PostDisplayShare.XYChart
import ui.compose.Report.ReportType
import util.compose.EasyToast
import util.compose.Label
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.compose.rememberToastState
import util.compose.shimmerLoadingAnimation
import util.compose.toastBindNetworkResult
import util.network.CollectWithContent
import util.network.logicWithTypeWithLimit
import util.network.toEasyTime
import util.network.toast

@Composable @Preview fun test() {}

/**
 * 帖子详情页ui
 *
 * @param id String 帖子id
 * @param modifier Modifier
 * @param initPostDetail Function1<String, Unit> 初始化帖子
 * @param postCommentPreview Pager<Int, Data>? 帖子评论分页
 * @param postCommentTree StateFlow<Pager<Int, Data>?> 帖子评论树 评论树是评论的评论列表
 * @param getPostCommentTree Function1<String, Unit> 获取帖子评论树
 * @param submitComment Function5<[@kotlin.ParameterName] Int, [@kotlin.ParameterName] Int,
 *   [@kotlin.ParameterName] String, [@kotlin.ParameterName] String, [@kotlin.ParameterName]
 *   ByteArray?, Unit>
 * @param commentReport Function1<[@kotlin.ParameterName] ReportType, Unit> 举报评论
 * @param refreshCommentPreview Function0<Unit> 刷新评论分页
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostDetail(
  id: String,
  modifier: Modifier = Modifier,
  initPostDetail: (String) -> Unit,
  postCommentPreview: Pager<Int, Data>?,
  postCommentTree: StateFlow<Pager<Int, data.post.PostCommentTree.Data>?>,
  getPostCommentTree: (String) -> Unit,
  submitComment:
    (parentId: Int, postId: Int, tree: String, content: String, image: ByteArray?) -> Unit,
  commentReport: (type: ReportType) -> Unit = {},
  refreshCommentPreview: () -> Unit = {},
) {
  val postDetailViewModel = koinInject<PostDetailViewModel>()
  val commentItems = postCommentPreview?.flow?.collectAsLazyPagingItems()
  val isRefresh = rememberSaveable { mutableStateOf(false) }
  val scope = rememberCoroutineScope()
  val commentState =
    rememberSaveable(
      stateSaver =
        Saver<CommentState, String>(
          restore = {
            return@Saver Json.decodeFromString<CommentStateSerializable>(it).toCommentState()
          },
          save = {
            return@Saver Json.encodeToString(it.toCommentStateSerializable())
          },
        )
    ) {
      mutableStateOf<CommentState>(CommentState())
    }
  val state = rememberLazyListState()
  val currentMainComment =
    rememberSaveable(
      stateSaver =
        Saver<Comment?, String>(
          restore = {
            if (it == "null") {
              return@Saver null
            }
            return@Saver Json.decodeFromString<Comment>(it)
          },
          save = {
            it
              ?: let {
                return@Saver "null"
              }
            return@Saver Json.encodeToString(it)
          },
        )
    ) {
      mutableStateOf<Comment?>(null)
    }

  // 下拉刷新
  LaunchedEffect(state) {
    snapshotFlow { state.isScrollInProgress && !state.canScrollBackward }
      .filter { it }
      .collect {
        isRefresh.value = true
        delay(1000)
        postDetailViewModel.refreshPostById(id)
        refreshCommentPreview()
        isRefresh.value = false
      }
  }

  LaunchedEffect(currentMainComment.value) {
    currentMainComment.value?.let { getPostCommentTree(it.Id.toString()) }
  }

  LaunchedEffect(Unit) {
    initPostDetail(id)
    refreshCommentPreview()
  }

  Box(modifier = modifier) {
    // 主要的帖子和评论
    Box(modifier = Modifier.fillMaxSize().padding(10.dp)) {
      LazyColumn(modifier = Modifier.fillMaxSize(), state = state) {
        // 帖子
        item {
          Box(modifier = Modifier.fillParentMaxWidth().wrapContentHeight().animateContentSize()) {
            val postState = postDetailViewModel.currentPostDetail.collectAsState()
            postState.CollectWithContent(
              success = { postById ->
                Column {
                  PersonalInformationAreaInDetail(
                    userName = postById.Post.User.username,
                    url = "${BaseUrlConfig.UserAvatar}/${postById.Post.User.avatar}",
                  )
                  Time(postById.Post.Time)
                  Text(text = postById.Post.Title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                  Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                    FlowRow {
                      postById.labelData?.let { labelList -> labelList.forEach { Label(it.Value) } }
                    }
                  }
                  listOf<PostContent>()
                    .plus(postById.valueData ?: listOf())
                    .plus(postById.lineChartData ?: listOf())
                    .plus(postById.fileData ?: listOf())
                    .sortedBy { it.order }
                    .forEach {
                      when (it) {
                        is FileData -> {
                          ImageContent(it.fileName)
                        }
                        is ValueData -> {
                          TextContent(it.value.decodeBase64String())
                        }
                        is LineChartData -> {
                          val data = remember { it.toLineChartDataForShowOrNull() }
                          if (data == null) {
                            Text("数据解析失败")
                          } else {
                            XYChart(false, Modifier, data)
                          }
                        }
                      }
                    }
                  postDetailViewModel.postLikeSubmitState
                    .collectAsState()
                    .value
                    .logicWithTypeWithLimit(
                      success = { postDetailViewModel.refreshPostById(postById.Post.Id.toString()) }
                    )
                  Interaction(
                    modifier = Modifier.padding(top = 5.dp).fillMaxWidth(0.6f).wrapContentHeight(),
                    likeNumber = postById.Post.LikeNum,
                    like = {
                      postDetailViewModel.postLikes(postById.Post.Id)
                      println(1)
                    },
                  )
                }
              },
              loading = { CircularProgressIndicator() },
              content = { Spacer(modifier = Modifier.height(1.dp)) },
            )
          }
        }
        // 分界线
        item {
          Divider(modifier = Modifier.padding(top = 10.dp).fillMaxWidth().padding(bottom = 10.dp))
        }
        // 对帖子的直接评论
        commentItems?.let {
          items(commentItems.itemCount) { index ->
            CommentInPostDetail(
              commentItems[index],
              click = {
                scope.launch {
                  commentItems[index]?.let { currentMainComment.value = it.MainComment }
                }
              },
              report = {
                commentReport.invoke(ReportType.CommentReportType(id, it.Id.toString(), it))
              },
            )
          }
        }
      }
    }

    // 下方的评论按钮和刷新的ui
    Box(modifier = Modifier.fillMaxSize()) {
      FloatingActionButton(
        onClick = { commentState.value.setCommentAt(null) },
        modifier =
          Modifier.offset(x = (-15).dp, y = ((-5).dp)).size(50.dp).align(Alignment.BottomEnd),
        shape = CircleShape,
      ) {
        Icon(
          modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center).fillMaxSize(0.5f),
          painter = painterResource(MR.images.comment),
          contentDescription = null,
        )
      }
      AnimatedVisibility(
        isRefresh.value,
        modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
        exit = slideOutVertically(),
        enter = slideInVertically(),
      ) {
        Box(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
          CircularProgressIndicator(
            modifier = Modifier.padding(5.dp).align(Alignment.Center).size(30.dp)
          )
        }
      }
    }

    // 获取详细的评论
    Box(modifier = Modifier.fillMaxSize()) {
      AnimatedVisibility(
        visible = currentMainComment.value != null,
        exit =
          slideOutVertically {
            return@slideOutVertically it
          },
        enter =
          slideInVertically {
            return@slideInVertically it
          },
        modifier = Modifier.fillMaxSize(),
      ) {
        BackHandler(currentMainComment.value != null) { currentMainComment.value = null }
        Column(modifier = Modifier.fillMaxSize()) {
          Box(
            modifier =
              Modifier.fillMaxWidth()
                .weight(1f)
                .background(Color(216, 211, 210, 150))
                .clickable(
                  remember { MutableInteractionSource() },
                  indication = null,
                  onClick = { currentMainComment.value = null },
                )
          )
          Surface(
            modifier =
              Modifier.fillMaxWidth()
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
          ) {
            val isRefreshInCommentTree = remember { mutableStateOf(false) }
            Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
              val commentTree = postCommentTree.value?.flow?.collectAsLazyPagingItems()
              // 刷新和下拉按钮
              Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
              ) {
                Icon(
                  imageVector = Icons.Filled.Refresh,
                  null,
                  modifier =
                    Modifier.size(50.dp)
                      .clip(CircleShape)
                      .clickable {
                        scope.launch {
                          isRefreshInCommentTree.value = true
                          delay(1000)
                          commentTree?.refresh()
                          isRefreshInCommentTree.value = false
                        }
                      }
                      .wrapContentSize(Alignment.Center)
                      .fillMaxSize(0.6f),
                )
                Icon(
                  imageVector = Icons.Filled.KeyboardArrowDown,
                  null,
                  modifier =
                    Modifier.size(50.dp)
                      .clip(CircleShape)
                      .clickable { currentMainComment.value = null }
                      .wrapContentSize(Alignment.Center)
                      .fillMaxSize(0.6f),
                )
              }
              Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                commentTree?.let {
                  LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                      Box(modifier = Modifier.animateContentSize()) {
                        currentMainComment.value?.let {
                          CommentInPostDetail(
                            commentGroup = Data(MainComment = it, SonComment = listOf()),
                            click = { commentState.value.setCommentAt(it) },
                            report = {
                              commentReport.invoke(
                                ReportType.CommentReportType(id, it.Id.toString(), it)
                              )
                            },
                          )
                        }
                      }
                    }
                    item {
                      Divider(
                        modifier =
                          Modifier.padding(top = 10.dp).fillMaxWidth().padding(bottom = 10.dp)
                      )
                    }
                    items(commentTree.itemCount) {
                      commentTree[it]?.let { commentIndex ->
                        CommentTreeItem(
                          commentIndex,
                          click = { comment -> commentState.value.setCommentAt(comment) },
                        )
                      }
                    }
                  }
                }
                androidx.compose.animation.AnimatedVisibility(
                  isRefreshInCommentTree.value,
                  modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                  exit = slideOutVertically(),
                  enter = slideInVertically(),
                ) {
                  Box(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
                    CircularProgressIndicator(
                      modifier = Modifier.padding(5.dp).align(Alignment.Center).size(30.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // 帖子评论
    Box(modifier = Modifier.fillMaxSize()) {
      AnimatedVisibility(
        visible = commentState.value.isShow.value,
        exit =
          slideOutVertically {
            return@slideOutVertically it
          },
        enter =
          slideInVertically {
            return@slideInVertically it
          },
        modifier = Modifier.fillMaxSize(),
      ) {
        val commentValue = remember { mutableStateOf("") }
        BackHandler(commentState.value.isShow.value) { commentState.value.closeCommentAt() }
        Column(modifier = Modifier.fillMaxSize()) {
          Box(
            modifier =
              Modifier.fillMaxWidth()
                .weight(1f)
                .background(Color(216, 211, 210, 150))
                .clickable(
                  remember { MutableInteractionSource() },
                  indication = null,
                  onClick = { commentState.value.closeCommentAt() },
                )
          )
          Surface(
            modifier =
              Modifier.fillMaxWidth()
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
          ) {
            Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
              val imageByteArray = remember { mutableStateOf<ByteArray?>(null) }
              val imagePicker = ImagePickerFactory(context = getPlatformContext()).createPicker()
              imagePicker.registerPicker(onImagePicked = { imageByteArray.value = it })
              Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
              ) {
                Icon(
                  imageVector = Icons.Filled.KeyboardArrowDown,
                  null,
                  modifier =
                    Modifier.size(50.dp)
                      .clip(CircleShape)
                      .clickable { commentState.value.closeCommentAt() }
                      .wrapContentSize(Alignment.Center)
                      .fillMaxSize(0.6f),
                )
              }
              LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(1f)) {
                commentState.value.commentAt.value?.let {
                  item {
                    Column(
                      modifier =
                        Modifier.padding(bottom = 10.dp)
                          .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(5.dp),
                            color = Color.Gray,
                          )
                          .padding(5.dp)
                    ) {
                      Text(text = "回复@")
                      CommentInPostDetail(
                        commentGroup = Data(it, listOf()),
                        report = {
                          commentReport.invoke(
                            ReportType.CommentReportType(id, it.Id.toString(), it)
                          )
                        },
                      )
                    }
                  }
                }
                item {
                  TextField(
                    value = commentValue.value,
                    onValueChange = { commentValue.value = it },
                    modifier =
                      Modifier.padding(bottom = 5.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .animateContentSize(),
                  )
                }
                item {
                  Box(modifier = Modifier.wrapContentHeight().fillMaxWidth().animateContentSize()) {
                    imageByteArray.value?.let {
                      Image(
                        modifier =
                          Modifier.fillMaxWidth(0.5f)
                            .wrapContentHeight()
                            .clip(RoundedCornerShape(3.dp)),
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                      )
                    }
                  }
                }
                item {
                  Row(modifier = Modifier.padding(bottom = 5.dp)) {
                    Crossfade(imageByteArray.value) {
                      if (it == null) {
                        Icon(
                          modifier =
                            Modifier.size(50.dp)
                              .clip(RoundedCornerShape(5.dp))
                              .clickable { imagePicker.pickImage() }
                              .padding(7.dp),
                          painter = painterResource(MR.images.image),
                          contentDescription = null,
                          tint = Color.Gray,
                        )
                      } else {
                        Icon(
                          modifier =
                            Modifier.size(50.dp)
                              .clip(RoundedCornerShape(5.dp))
                              .clickable { imageByteArray.value = null }
                              .padding(7.dp),
                          imageVector = Icons.Filled.Close,
                          contentDescription = null,
                          tint = Color.Gray,
                        )
                      }
                    }
                  }
                }
                item {
                  Button(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    onClick = {
                      submitComment.invoke(
                        if (commentState.value.commentAt.value == null) -1
                        else commentState.value.commentAt.value!!.Id,
                        id.toInt(),
                        if (commentState.value.commentAt.value == null) "-1/"
                        else
                          commentState.value.commentAt.value!!.Tree +
                            commentState.value.commentAt.value!!.Id +
                            "/",
                        commentValue.value,
                        imageByteArray.value,
                      )
                    },
                  ) {
                    Icon(
                      modifier = Modifier.padding(end = 5.dp).size(30.dp).padding(5.dp),
                      imageVector = Icons.Filled.Done,
                      contentDescription = null,
                    )
                    Text("提交评论")
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/**
 * 显示数据ui
 *
 * @param time String
 */
@Composable
fun Time(time: String) {
  val instant = Instant.parse(time)
  val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
  Text(
    text = "${ localDateTime.date.toString() } ${localDateTime.hour}:${localDateTime.minute}",
    fontSize = 10.sp,
  )
}

/**
 * 显示个人信息
 *
 * @param url String 头像url
 * @param modifier Modifier
 * @param userName String
 */
@Composable
fun PersonalInformationAreaInDetail(
  url: String = "https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg",
  modifier: Modifier = Modifier.fillMaxWidth().height(50.dp),
  userName: String = "theonenull",
) {
  Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
    KamelImage(
      resource = asyncPainterResource(url),
      null,
      modifier = Modifier.fillMaxHeight(0.7f).aspectRatio(1f).clip(CircleShape),
      contentScale = ContentScale.FillBounds,
    )
    Text(
      modifier = Modifier.padding(horizontal = 10.dp).weight(1f).wrapContentHeight(),
      text = userName,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
    )
  }
}

/**
 * 帖子评论详情
 *
 * @param commentGroup Data?
 * @param click Function0<Unit>
 * @param report Function1<[@kotlin.ParameterName] Comment, Unit>
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentInPostDetail(
  commentGroup: Data?,
  click: () -> Unit = {},
  report: (comment: Comment) -> Unit = {},
) {
  commentGroup?.let {
    Row(
      modifier =
        Modifier.padding(bottom = 10.dp)
          .animateContentSize()
          .clip(RoundedCornerShape(3.dp))
          .combinedClickable(
            onClick = { click() },
            onLongClick = { report.invoke(commentGroup.MainComment) },
          )
          .padding(vertical = 5.dp)
    ) {
      // 头像
      KamelImage(
        resource =
          asyncPainterResource(
            "${BaseUrlConfig.UserAvatar}/${commentGroup.MainComment.User.avatar}"
          ),
        null,
        modifier =
          Modifier.size(50.dp)
            .wrapContentSize(Alignment.TopCenter)
            .fillMaxSize(0.7f)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10)),
        contentScale = ContentScale.FillBounds,
        onLoading = { Box(modifier = Modifier.matchParentSize().shimmerLoadingAnimation()) },
      )
      Column(modifier = Modifier.padding(end = 10.dp).weight(1f).wrapContentHeight()) {
        Text(commentGroup.MainComment.User.username)
        Text(commentGroup.MainComment.Time.toEasyTime(), fontSize = 10.sp)
        Text(commentGroup.MainComment.Content, fontSize = 14.sp)
        commentGroup.MainComment.Image.let {
          if (it != "") {
            KamelImage(
              resource = asyncPainterResource("${BaseUrlConfig.CommentImage}/${it}"),
              null,
              modifier =
                Modifier.padding(vertical = 5.dp)
                  .fillMaxWidth(0.5f)
                  .wrapContentHeight()
                  .clip(RoundedCornerShape(3))
                  .animateContentSize(),
              contentScale = ContentScale.Inside,
              onLoading = { Box(modifier = Modifier.matchParentSize().shimmerLoadingAnimation()) },
            )
          }
        }
        if (commentGroup.SonComment.isNotEmpty()) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              commentGroup.SonComment.forEach {
                Text(
                  "${ it.User.username }: ${ it.Content }",
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.padding(bottom = 3.dp).fillMaxWidth().wrapContentHeight(),
                  fontSize = 10.sp,
                )
              }
            }
          }
        }
        if (commentGroup.MainComment.Status == 1) {
          Box(
            modifier =
              Modifier.fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(10))
                .padding(vertical = 10.dp)
          ) {
            Text(
              modifier = Modifier.fillMaxWidth().wrapContentHeight().animateContentSize(),
              overflow = TextOverflow.Ellipsis,
              fontSize = 10.sp,
              text = "该评论遭到较多举报，请谨慎看待",
              color = Color.Red,
            )
          }
        }
      }
    }
  }
}

/**
 * 在评论树中的item的ui
 *
 * @param data Data
 * @param click Function1<Comment, Unit>
 */
@Composable
fun CommentTreeItem(data: data.post.PostCommentTree.Data, click: (Comment) -> Unit) {
  val showParent = remember { mutableStateOf(false) }
  val rotate: Float by animateFloatAsState(if (showParent.value) 180f else 0f)
  Column(modifier = Modifier.fillMaxWidth().animateContentSize()) {
    data.MainComment.let { comment ->
      Column {
        Row(
          modifier =
            Modifier.padding(bottom = 10.dp)
              .animateContentSize()
              .clip(RoundedCornerShape(3.dp))
              .clickable { click.invoke(data.MainComment) }
              .padding(vertical = 5.dp)
        ) {
          KamelImage(
            resource = asyncPainterResource("${BaseUrlConfig.UserAvatar}/${comment.User.avatar}"),
            null,
            modifier =
              Modifier.size(50.dp)
                .wrapContentSize(Alignment.TopCenter)
                .fillMaxSize(0.7f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10)),
            contentScale = ContentScale.FillBounds,
            onLoading = { Box(modifier = Modifier.matchParentSize().shimmerLoadingAnimation()) },
          )
          Column(modifier = Modifier.padding(end = 10.dp).weight(1f).wrapContentHeight()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Column(modifier = Modifier.weight(1f)) {
                Text(comment.User.username)
                Text("回复@${data.ParentComment.User.username}", fontSize = 10.sp)
              }
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                  Modifier.wrapContentSize()
                    .clip(RoundedCornerShape(10))
                    .clickable { showParent.value = !showParent.value }
                    .padding(start = 3.dp),
              ) {
                Text(text = if (showParent.value) "隐藏回复内容" else "显示回复内容", fontSize = 9.sp)
                Icon(
                  imageVector = Icons.Filled.KeyboardArrowDown,
                  null,
                  modifier = Modifier.rotate(rotate).size(30.dp).clip(CircleShape),
                )
              }
            }
            Text(comment.Time.toEasyTime(), fontSize = 10.sp)
            Text(comment.Content, fontSize = 14.sp)
            comment.Image.let {
              if (it != "") {
                KamelImage(
                  resource = asyncPainterResource("${BaseUrlConfig.CommentImage}/${it}"),
                  null,
                  modifier =
                    Modifier.padding(vertical = 5.dp)
                      .fillMaxWidth(0.5f)
                      .wrapContentHeight()
                      .clip(RoundedCornerShape(3))
                      .animateContentSize(),
                  contentScale = ContentScale.Inside,
                  onLoading = {
                    Box(modifier = Modifier.matchParentSize().shimmerLoadingAnimation())
                  },
                )
              }
            }
            AnimatedVisibility(showParent.value, modifier = Modifier) {
              Surface {
                data.ParentComment.let {
                  Row(
                    modifier =
                      Modifier.padding(bottom = 10.dp)
                        .animateContentSize()
                        .clip(RoundedCornerShape(5.dp))
                        .border(
                          width = (0.5).dp,
                          shape = RoundedCornerShape(3.dp),
                          brush = Brush.linearGradient(listOf(Color.Gray, Color.Gray)),
                        )
                        .padding(vertical = 5.dp)
                  ) {
                    KamelImage(
                      resource =
                        asyncPainterResource("${BaseUrlConfig.UserAvatar}/${it.User.avatar}"),
                      null,
                      modifier =
                        Modifier.size(50.dp)
                          .wrapContentSize(Alignment.TopCenter)
                          .fillMaxSize(0.7f)
                          .aspectRatio(1f)
                          .clip(RoundedCornerShape(10)),
                      contentScale = ContentScale.FillBounds,
                      onLoading = {
                        Box(modifier = Modifier.matchParentSize().shimmerLoadingAnimation())
                      },
                    )
                    Column(
                      modifier = Modifier.padding(end = 10.dp).weight(1f).wrapContentHeight()
                    ) {
                      Text(it.User.username)
                      Text(it.Time.toEasyTime(), fontSize = 10.sp)
                      Text(it.Content, fontSize = 14.sp)
                      it.Image.let {
                        if (it != "") {
                          KamelImage(
                            resource = asyncPainterResource("${BaseUrlConfig.CommentImage}/${it}"),
                            null,
                            modifier =
                              Modifier.padding(vertical = 5.dp)
                                .fillMaxWidth(0.5f)
                                .wrapContentHeight()
                                .clip(RoundedCornerShape(3))
                                .animateContentSize(),
                            contentScale = ContentScale.Inside,
                            onLoading = {
                              Box(modifier = Modifier.matchParentSize().shimmerLoadingAnimation())
                            },
                          )
                        }
                      }
                    }
                  }
                }
              }
            }
            if (comment.Status == 1) {
              Box(
                modifier =
                  Modifier.fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(10))
                    .padding(vertical = 5.dp)
              ) {
                Text(
                  modifier = Modifier.fillMaxWidth().wrapContentHeight().animateContentSize(),
                  overflow = TextOverflow.Ellipsis,
                  fontSize = 10.sp,
                  text = "该评论遭到较多举报，请谨慎看待",
                  color = Color.Red,
                )
              }
            }
          }
        }
      }
    }
  }
}

/**
 * 创建新评论的数据
 *
 * @property isShow MutableState<Boolean> 是否显示该ui
 * @property commentAt MutableState<Comment?> 要评论的对象
 * @constructor
 */
class CommentState(
  val isShow: MutableState<Boolean> = mutableStateOf(false),
  val commentAt: MutableState<Comment?> = mutableStateOf(null),
) {

  fun setCommentAt(mainComment: Comment?) {
    commentAt.value = mainComment
    isShow.value = true
  }

  fun closeCommentAt() {
    isShow.value = false
    commentAt.value = null
  }
}

/**
 * 用于恢复的序列化操作
 *
 * @property isShow Boolean 是否显示
 * @property commentAt Comment? 评论对象
 * @constructor
 */
@Serializable data class CommentStateSerializable(val isShow: Boolean, val commentAt: Comment?)

fun CommentState.toCommentStateSerializable(): CommentStateSerializable {
  return CommentStateSerializable(isShow = this.isShow.value, commentAt = this.commentAt.value)
}

fun CommentStateSerializable.toCommentState(): CommentState {
  return CommentState(
    isShow = mutableStateOf(this.isShow),
    commentAt = mutableStateOf(this.commentAt),
  )
}

/**
 * 帖子详情页 二级界面
 *
 * @property id String
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class PostDetailVoyagerScreen(
  val id: String,
  @Transient val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl(),
) : Screen {
  @Composable
  override fun Content() {
    Box(modifier = Modifier.fillMaxSize()) {
      val postDetailViewModel = koinInject<PostDetailViewModel>()
      val commentSubmitState = postDetailViewModel.commentSubmitState.collectAsState()
      val postCommentPreview = postDetailViewModel.postCommentPreviewFlow.collectAsState().value
      val toastState = rememberToastState()
      toastState.toastBindNetworkResult(
        postDetailViewModel.postLikeSubmitState.collectAsState(),
        postDetailViewModel.commentSubmitState.collectAsState(),
      )
      LaunchedEffect(commentSubmitState.value.key) {
        commentSubmitState.value.toast(
          success = { toastState.addToast(it) },
          error = { toastState.addToast(it.message.toString(), Color.Red) },
        )
      }
      PostDetail(
        id = id,
        modifier = Modifier.fillMaxSize().parentSystemControl(parentPaddingControl),
        initPostDetail = {
          //                    postDetailViewModel.initPostById(it)
        },
        postCommentPreview = postCommentPreview,
        postCommentTree = postDetailViewModel.postCommentTreeFlow,
        getPostCommentTree = { treeStart ->
          postDetailViewModel.getPostCommentTree(treeStart, postId = id)
        },
        submitComment = { parentId, postIdInComment, tree, content, image ->
          postDetailViewModel.submitComment(parentId, postIdInComment, tree, content, image)
        },
        commentReport = { postDetailViewModel.navigateToReport(it) },
        refreshCommentPreview = { postDetailViewModel.initPostCommentPreview(id) },
      )
      EasyToast(toastState)
    }
  }
}
