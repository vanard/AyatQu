package id.vanard.ayatqu.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.vanard.ayatqu.core.ui.theme.BorderSubtle
import id.vanard.ayatqu.core.ui.theme.AyatQuInputBg
import id.vanard.ayatqu.core.ui.theme.AyatQuTextDark
import id.vanard.ayatqu.core.ui.theme.AyatQuTextStrong
import id.vanard.ayatqu.core.ui.theme.TextHint

// ── Background ────────────────────────────────────────────────────────────────

@Composable
fun AuthBackground() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1C3A4A), Color(0xFF0A1A24)),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0x1A000000), Color(0x99000000)),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
    )
}

// ── Logo ──────────────────────────────────────────────────────────────────────

@Composable
fun AuthLogo() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.White)
            // TODO: replace with actual logo icon
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Ayat Qu",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            color = Color.White,
            letterSpacing = (-0.6).sp
        )
    }
}

// ── Back buttons ──────────────────────────────────────────────────────────────

/** Ghost circle back button — for use on the dark background area */
@Composable
fun BackButtonLight(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            ArrowBackIcon(color = Color.White)
        }
    }
}

/** Gray circle back button — for use inside the white bottom sheet */
@Composable
fun BackButtonDark(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(AyatQuInputBg),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            ArrowBackIcon(color = AyatQuTextStrong)
        }
    }
}

@Composable
fun ArrowBackIcon(color: Color) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .drawWithCache {
                val stroke = 2.dp.toPx()
                val arrowSize = 12.dp.toPx()
                val cx = size.width / 2f
                val cy = size.height / 2f
                val path = Path().apply {
                    moveTo(cx + arrowSize / 2f, cy)
                    lineTo(cx - arrowSize / 2f, cy)
                    moveTo(cx - arrowSize / 2f, cy)
                    lineTo(cx - arrowSize / 2f + arrowSize * 0.45f, cy - arrowSize * 0.45f)
                    moveTo(cx - arrowSize / 2f, cy)
                    lineTo(cx - arrowSize / 2f + arrowSize * 0.45f, cy + arrowSize * 0.45f)
                }
                onDrawWithContent {
                    drawContent()
                    drawPath(path, color, style = Stroke(width = stroke))
                }
            }
    )
}

// ── Input field ───────────────────────────────────────────────────────────────

@Composable
fun AuthInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = TextHint
    )
    Spacer(Modifier.height(6.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = TextHint
            )
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = AyatQuInputBg,
            focusedContainerColor = Color.White,
            unfocusedBorderColor = BorderSubtle,
            focusedBorderColor = AyatQuTextStrong,
        ),
        modifier = modifier.fillMaxWidth()
    )
}

// ── Or divider ────────────────────────────────────────────────────────────────

@Composable
fun OrDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderSubtle)
        Text(
            text = "  or  ",
            style = MaterialTheme.typography.labelSmall,
            color = TextHint
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = BorderSubtle)
    }
}

// ── Social button ─────────────────────────────────────────────────────────────

@Composable
fun SocialButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(46.dp),
        shape = CircleShape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = AyatQuTextDark
        ),
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = AyatQuTextStrong
        )
    }
}
