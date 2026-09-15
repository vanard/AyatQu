package id.vanard.ayatqu.presentation.qibla

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.vanard.ayatqu.R
import id.vanard.ayatqu.core.ui.icon.ArrowLeft
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.domain.model.QiblaDirection
import id.vanard.ayatqu.presentation.qibla.contract.OnQiblaEvent
import id.vanard.ayatqu.presentation.qibla.contract.QiblaEvent
import id.vanard.ayatqu.presentation.qibla.contract.QiblaState
import id.vanard.ayatqu.presentation.qibla.contract.QiblaTurn

@Composable
fun QiblaScreen(
    state: QiblaState,
    onEvent: OnQiblaEvent,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AyatQuTheme.colors.background)
            .statusBarsPadding(),
    ) {
        QiblaHeader(onBack = { onEvent(QiblaEvent.BackClicked) })

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            contentAlignment = Alignment.TopCenter,
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    color = AyatQuTheme.colors.accentGold,
                    modifier = Modifier.padding(top = 120.dp),
                )
                state.errorMessage != null -> QiblaError(
                    message = state.errorMessage,
                    onRetry = { onEvent(QiblaEvent.RetryClicked) },
                )
                state.qibla != null -> QiblaContent(state)
            }
        }
    }
}

@Composable
private fun QiblaHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = ArrowLeft,
                contentDescription = stringResource(R.string.back),
                tint = AyatQuTheme.colors.textPrimary,
            )
        }
        Text(
            text = stringResource(R.string.qibla),
            color = AyatQuTheme.colors.textPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun QiblaContent(state: QiblaState) {
    val qibla = checkNotNull(state.qibla)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = state.locationName.orEmpty(),
            color = AyatQuTheme.colors.textSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
        QiblaCompass(
            kaabaMarkerRotation = state.kaabaMarkerRotation,
            deviceNeedleRotation = state.deviceNeedleRotation ?: 0f,
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = state.roundedDeviceHeading?.let { "$it°" } ?: "—°",
                color = AyatQuTheme.colors.textPrimary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.current_device_bearing),
                color = AyatQuTheme.colors.textSecondary,
                fontSize = 15.sp,
            )
        }
        RotationInstruction(state)
        if (state.isCompassAvailable == false) {
            Text(
                text = stringResource(R.string.qibla_compass_unavailable),
                color = AyatQuTheme.colors.error,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            QiblaInfoCard(
                label = stringResource(R.string.qibla_direction),
                value = stringResource(
                    R.string.qibla_degrees_value,
                    qibla.directionDegrees,
                    qibla.compassBearing,
                ),
                modifier = Modifier.weight(1f),
            )
            QiblaInfoCard(
                label = stringResource(R.string.distance_to_kaaba),
                value = stringResource(R.string.qibla_distance_value, qibla.distanceKm),
                modifier = Modifier.weight(1f),
            )
        }
        Text(
            text = stringResource(R.string.qibla_true_north_note),
            color = AyatQuTheme.colors.textMuted,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
    }
}

@Composable
private fun RotationInstruction(state: QiblaState) {
    val text = when (val turn = state.turn) {
        is QiblaTurn.Left -> stringResource(R.string.rotate_phone_left, turn.degrees)
        is QiblaTurn.Right -> stringResource(R.string.rotate_phone_right, turn.degrees)
        QiblaTurn.Aligned -> stringResource(R.string.qibla_aligned)
        null -> stringResource(R.string.qibla_calibrating_compass)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AyatQuTheme.colors.accentGold.copy(alpha = 0.12f))
            .padding(horizontal = 22.dp, vertical = 14.dp),
    ) {
        Text(
            text = text,
            color = AyatQuTheme.colors.accentGold,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun QiblaCompass(
    kaabaMarkerRotation: Float,
    deviceNeedleRotation: Float,
) {
    val primary = AyatQuTheme.colors.primary
    val gold = AyatQuTheme.colors.accentGold
    val needleBorder = AyatQuTheme.colors.onPrimary
    Box(
        modifier = Modifier.size(300.dp),
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.BottomCenter)
                .clip(CircleShape)
                .background(AyatQuTheme.colors.surface)
                .border(3.dp, gold.copy(alpha = 0.38f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("W", Modifier.align(Alignment.TopCenter).padding(top = 60.dp), color = primary, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("S", Modifier.align(Alignment.CenterStart).padding(start = 25.dp), color = AyatQuTheme.colors.textPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("N", Modifier.align(Alignment.CenterEnd).padding(end = 25.dp), color = AyatQuTheme.colors.textPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("E", Modifier.align(Alignment.BottomCenter).padding(bottom = 25.dp), color = AyatQuTheme.colors.textPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)

            CompassDot(Modifier.align(Alignment.TopStart).padding(start = 74.dp, top = 73.dp))
            CompassDot(Modifier.align(Alignment.TopEnd).padding(end = 74.dp, top = 73.dp))
            CompassDot(Modifier.align(Alignment.BottomStart).padding(start = 74.dp, bottom = 73.dp))
            CompassDot(Modifier.align(Alignment.BottomEnd).padding(end = 74.dp, bottom = 73.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(kaabaMarkerRotation),
            ) {
                KaabaTarget(
                    iconRotation = -kaabaMarkerRotation,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }

            Canvas(
                modifier = Modifier
                    .size(165.dp)
                    .rotate(deviceNeedleRotation),
            ) {
                val center = this.center
                val arrow = Path().apply {
                    moveTo(center.x, 4.dp.toPx())
                    lineTo(center.x - 10.dp.toPx(), center.y)
                    lineTo(center.x + 10.dp.toPx(), center.y)
                    close()
                }
                drawPath(
                    path = arrow,
                    brush = Brush.verticalGradient(listOf(gold, primary)),
                )
                drawCircle(
                    brush = Brush.verticalGradient(listOf(gold, primary)),
                    radius = 25.dp.toPx(),
                    center = center,
                )
                drawCircle(
                    color = needleBorder,
                    radius = 25.dp.toPx(),
                    center = center,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
        }
    }
}

@Composable
private fun CompassDot(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(AyatQuTheme.colors.accentGold.copy(alpha = 0.75f)),
    )
}

@Composable
private fun KaabaTarget(
    iconRotation: Float,
    modifier: Modifier = Modifier,
) {
    val gold = AyatQuTheme.colors.accentGold
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(Modifier.size(width = 24.dp, height = 15.dp)) {
            val triangle = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2f, size.height)
                close()
            }
            drawPath(triangle, gold)
        }
        Spacer(Modifier.height(7.dp))
        Box(
            modifier = Modifier
                .size(48.dp)
                .rotate(iconRotation)
                .shadow(5.dp, CircleShape)
                .clip(CircleShape)
                .background(AyatQuTheme.colors.surface),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 27.dp, height = 25.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(AyatQuTheme.colors.textPrimary),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .padding(top = 1.dp)
                        .background(AyatQuTheme.colors.accentGold),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .size(width = 6.dp, height = 10.dp)
                        .background(AyatQuTheme.colors.accentGold.copy(alpha = 0.75f)),
                )
            }
        }
    }
}

@Composable
private fun QiblaInfoCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(AyatQuTheme.colors.surface)
            .border(1.dp, AyatQuTheme.colors.border, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(label, color = AyatQuTheme.colors.textMuted, fontSize = 12.sp)
        Text(value, color = AyatQuTheme.colors.textPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun QiblaError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = message,
            color = AyatQuTheme.colors.textMuted,
            textAlign = TextAlign.Center,
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(AyatQuTheme.colors.accentGold)
                .clickable(onClick = onRetry)
                .padding(horizontal = 24.dp, vertical = 10.dp),
        ) {
            Text(
                text = stringResource(R.string.retry),
                color = AyatQuTheme.colors.onPrimary,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QiblaScreenPreview() {
    AyatQuTheme {
        QiblaScreen(
            state = QiblaState(
                qibla = QiblaDirection(
                    directionDegrees = 292.1,
                    compassBearing = "WNW",
                    latitude = -8.5069,
                    longitude = 115.2625,
                    distanceKm = 8234.6,
                    distanceMiles = 5116.8,
                    note = "Bearing is calculated as true north.",
                ),
                locationName = "Ubud",
                headingDegrees = 40f,
                isCompassAvailable = true,
            ),
            onEvent = {},
        )
    }
}
