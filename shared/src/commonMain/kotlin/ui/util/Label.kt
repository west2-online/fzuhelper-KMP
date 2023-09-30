package ui.util

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Label(
    string: String,
){
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(end = 5.dp, bottom = 5.dp),
        backgroundColor = Color(224, 244, 255),
        shape = RoundedCornerShape(100)
    ) {
        Text(
            string,
            fontSize = 10.sp,
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 10.dp),
            color = Color(41, 108, 218),
        )
    }
}

