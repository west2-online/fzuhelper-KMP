package ui.compose.Report

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import config.BaseUrlConfig
import data.post.PostList.Data
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import ui.compose.Post.PersonalInformationAreaInList

@Composable
fun PostReport(
    modifier: Modifier = Modifier,
    data : Data
){
    Column(
        modifier = modifier
    ) {
        Text("举报@", color = Color.Red,modifier = Modifier.padding(bottom = 10.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))
                .clip(RoundedCornerShape(5.dp))
                .padding(10.dp)
        ){
            PersonalInformationAreaInList(
                userAvatar = data.User.avatar ,
                userName = data.User.username ,
            )
            data.FirstImage?.let {
                KamelImage(
                    resource = asyncPainterResource("${BaseUrlConfig.PostImage}/${it}"),
                    null,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit,
                    onFailure = {
                        Text("加载失败")
                    },
                    onLoading = {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(vertical = 20.dp)
                                .align(Alignment.Center)
                        )
                    }
                )
            }

            Text(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(end = 10.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                text = data.Title
            )
            Text(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .animateContentSize(),
                maxLines = 10,
                overflow = TextOverflow.Ellipsis,
                fontSize = 10.sp,
                text = data.LittleDescribe?:""
            )
        }
    }
}
