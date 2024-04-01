package com.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import di.SystemAction
import ui.root.RootUi

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

