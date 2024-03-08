package ui.compose.Feedback

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition







class FeedbackVoyagerScreen():Screen{
    @Composable
    override fun Content() {
        Navigator(FeedbackListVoyagerScreen()){ navigator ->
            SlideTransition(navigator = navigator)
        }
    }
}