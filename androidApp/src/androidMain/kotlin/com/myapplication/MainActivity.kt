package com.myapplication

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.bumble.appyx.navigation.integration.NodeActivity
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import di.SystemAction
import ui.root.RootNode
import util.compose.FuTalkTheme

class MainActivity : NodeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FuTalkTheme {
                NodeHost(
                    modifier = Modifier.fillMaxSize(),
                    lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                    integrationPoint = appyxV2IntegrationPoint
                ) {
                    RootNode(
                        buildContext = it,
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
    }
}

