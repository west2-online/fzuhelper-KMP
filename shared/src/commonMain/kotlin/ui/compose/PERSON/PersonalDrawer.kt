package ui.compose.PERSON

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.example.library.MR

@Composable
fun PersonalDrawer(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ){
        PersonalInformation(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(10.dp)
        )
        Functions(
            modifier = Modifier
                .padding(top = 20.dp)
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
        )
    }
}

@Composable
fun PersonalInformation(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ) {
        KamelImage(
            resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
            null,
            modifier = Modifier
                .height(50.dp)
                .aspectRatio(1f)
                .clip(CircleShape),
            contentScale = ContentScale.FillBounds
        )
        Text(
            text = "theonenull",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding( top = 10.dp )
        )
        Text(
            text =  "sqt18750016193@163.com",
            fontSize = 10.sp
        )
    }
}

@Composable
fun Functions(
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier
    ){
        (1..20).forEach { _ ->
            item {
                FunctionsItem(
                    painterResource(MR.images.feedback),
                    {},
                    "个人资料"
                )
            }
        }
    }
}

@Composable
fun LazyItemScope.FunctionsItem(
    painter: Painter,
    onclick :()->Unit = { },
    text : String
){
    Row(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .height(50.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onclick.invoke() }
            .padding( start = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            painter = painter,
            "",
            modifier = Modifier
                .fillMaxHeight(0.4f)
                .aspectRatio(1f)
        )
        Text(
            text,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false
        )
    }
}

