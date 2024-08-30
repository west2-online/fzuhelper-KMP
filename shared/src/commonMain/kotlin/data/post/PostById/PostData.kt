package data.post.PostById

import data.share.Label
import data.share.Post
import kotlinx.serialization.Serializable

@Serializable
data class PostData(
  val Post: Post,
  val fileData: List<FileData>?,
  val valueData: List<ValueData>?,
  val labelData: List<Label>?,
  val lineChartData: List<LineChartData>?,
)
