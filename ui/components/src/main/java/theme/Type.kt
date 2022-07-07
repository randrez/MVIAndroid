package theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.scgts.sctrace.root.components.R

private val ProximaNova = FontFamily(
    Font(R.font.proximanova_regular),
    Font(R.font.proximanova_medium, FontWeight.W500),
    Font(R.font.proximanova_semibold, FontWeight.W600)
)

val SCGTSTypography = Typography(
    defaultFontFamily = ProximaNova,
    h4 = TextStyle(
        color = N900,
        fontSize = 26.sp
    ),
    h5 = TextStyle(
        color = N900,
        fontSize = 20.sp
    ),
    h6 = TextStyle(
        color = N900,
        fontSize = 16.sp
    ),
    subtitle1 = TextStyle(
        color = N500,
        fontSize = 14.sp
    ),
    subtitle2 = TextStyle(
        color = N500,
        fontSize = 12.sp
    ),
    body1 = TextStyle(
        color = N900,
        fontSize = 14.sp
    ),
    body2 = TextStyle(
        color = N900,
        fontSize = 12.sp
    ),
    button = TextStyle(
        textAlign = TextAlign.Center,
        fontSize = 18.sp
    ),
    caption = TextStyle(
        fontSize = 12.sp
    )
)