package com.myapplication

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import di.SystemAction
import ui.root.RootUi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT,Color.TRANSPARENT)
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

