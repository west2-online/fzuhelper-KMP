package com.myapplication

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import di.SystemAction
import ui.compose.Release.ReleasePageItem
import ui.compose.Release.ReleasePageItemLineChart
import ui.root.RootUi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window,false)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT,Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT,Color.TRANSPARENT)
        )
        setContent {
            RootUi(
                systemAction = SystemAction(
                    onFinish = {
                        this.finish()
                    },
                    onBack = {
                        this.onBackPressed()
                    }
                )
            )
        }
    }
}


@Preview
@Composable
fun Preview(){
    var data  = remember {
        ReleasePageItem.LineChartItem()
    }
    ReleasePageItemLineChart(
        modifier = Modifier
            .fillMaxWidth(),
        data
    )
}