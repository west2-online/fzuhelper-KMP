package com.myapplication

import android.os.Bundle
import androidx.activity.compose.setContent
import com.bumble.appyx.navigation.integration.NodeActivity
import di.SystemAction
import ui.root.RootUi
import util.compose.FuTalkTheme

class MainActivity : NodeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FuTalkTheme {
//                NodeHost(
//                    modifier = Modifier.fillMaxSize(),
//                    lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
//                    integrationPoint = appyxV2IntegrationPoint
//                ) {
//                    RootNode(
//                        buildContext = it,
//                        systemAction = SystemAction(
//                            onFinish = {
//                                this.finish()
//                            },
//                            onBack = {
//                                this.onBackPressed()
//                            }
//                        )
//                    )
//                }
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
}

