package util.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ErrorText(
    text:String,
    onClick:()->Unit,
    modifier: Modifier = Modifier,
    boxModifier : Modifier = Modifier
){
    Box(
        boxModifier
            .clickable{
                onClick.invoke()
            }
    ){
        Text(
            modifier = modifier
                .align(Alignment.Center),
            text = text,
            color = MaterialTheme.colors.error
        )
    }
}