package ui.compose.PERSON

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun PersonScreen(
    modifier: Modifier = Modifier,
    token:String
){
    Column (
        modifier = Modifier
            .fillMaxSize()
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Red)
        )
        Row {
            PersonalInformation(
                modifier = Modifier
                    .offset(y = (-25).dp)
                    .wrapContentHeight()
                    .weight(1f)
                    .padding( start = 10.dp )
            )
            Button(
                onClick = {},
                modifier = Modifier
                    .padding(horizontal = 5.dp)
            ){
                Text(
                    "编辑个人信息",

                )
            }
        }
    }
}

//@Composable
//fun PersonHeader()

