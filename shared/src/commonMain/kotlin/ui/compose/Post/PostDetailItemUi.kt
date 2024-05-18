package ui.compose.Post

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import config.BaseUrlConfig
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

/**
 * 显示detail中的图片
 * @param imageData String
 */
@Composable
fun ImageContent(
    imageData: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .animateContentSize()
    ){
        KamelImage(
            resource = asyncPainterResource("${BaseUrlConfig.PostImage}/${imageData}"),
            null,
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * 显示detail中的文字
 * @param text String
 */
@Composable
fun TextContent(
    text :String
){
    Text(text)
}

