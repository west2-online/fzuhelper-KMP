package util.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.MarkdownComponents
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.model.MarkdownColors
import com.mikepenz.markdown.model.MarkdownPadding
import com.mikepenz.markdown.model.markdownColor
import com.mikepenz.markdown.model.markdownPadding
import com.mikepenz.markdown.model.markdownTypography
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor

@Composable
fun OwnMarkdown(
  content: String,
  colors: MarkdownColors = markdownColor(),
  padding: MarkdownPadding = markdownPadding(),
  modifier: Modifier = Modifier.fillMaxSize(),
  flavour: MarkdownFlavourDescriptor = GFMFlavourDescriptor(),
  components: MarkdownComponents = markdownComponents(),
) {
  Markdown(
    content = content,
    colors = colors,
    typography =
      markdownTypography(
        h1 = MaterialTheme.typography.h1.copy(fontSize = 30.sp),
        h2 = MaterialTheme.typography.h2.copy(fontSize = 25.sp),
        h3 = MaterialTheme.typography.h3.copy(fontSize = 20.sp),
        h4 = MaterialTheme.typography.h4.copy(fontSize = 15.sp),
        h5 = MaterialTheme.typography.h5.copy(fontSize = 10.sp),
        h6 = MaterialTheme.typography.h6.copy(fontSize = 5.sp),
        text = MaterialTheme.typography.body1,
        code = MaterialTheme.typography.body1,
        quote = MaterialTheme.typography.body1,
        paragraph = MaterialTheme.typography.body1,
        ordered = MaterialTheme.typography.body1,
        bullet = MaterialTheme.typography.body1,
        list = MaterialTheme.typography.body1,
      ),
    padding = padding,
    modifier = modifier,
    flavour = flavour,
    components = components,
  )
}
