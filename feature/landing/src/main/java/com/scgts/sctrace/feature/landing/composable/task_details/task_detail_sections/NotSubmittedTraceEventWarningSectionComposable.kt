import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.scgts.sctrace.root.components.R
import theme.Pink
import theme.Red
import theme.SCGTSTheme

@Composable
fun NotSubmittedTraceEventWarningSection(show: Boolean) {
    if (show) {
        Column(
            modifier = Modifier
                .padding(top = 18.dp)
                .background(Pink)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_icon_information),
                    modifier = Modifier
                        .size(32.dp),
                    contentDescription = stringResource(id = R.string.info_icon_description),
                    tint = Red
                )
                Text(
                    text = stringResource(id = R.string.message_no_submit_assets),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewMessageNoSubmit() {
    SCGTSTheme {
        NotSubmittedTraceEventWarningSection(true)
    }
}
