package id.vanard.ayatqu.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

// ─────────────────────────────────────────────────────────────────────────────
//  Phosphor icon set — locally defined for the AyatQu app.
//  Source: https://phosphoricons.com  (MIT License)
//  ViewBox 256×256, regular weight uses 16px stroke with round caps/joins,
//  filled weight uses solid fill.
// ─────────────────────────────────────────────────────────────────────────────


// ── House (regular) ──
val House: ImageVector
    get() {
        if (_house != null) {
            return _house!!
        }
        _house = ImageVector.Builder(
            name = "Regular.House",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(104f, 216f)
                verticalLineTo(152f)
                horizontalLineToRelative(48f)
                verticalLineToRelative(64f)
                horizontalLineToRelative(64f)
                verticalLineTo(120f)
                arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2.34f, -5.66f)
                lineToRelative(-80f, -80f)
                arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = false, -11.32f, 0f)
                lineToRelative(-80f, 80f)
                arcTo(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = false, 40f, 120f)
                verticalLineToRelative(96f)
                close()
            }
        }.build()

        return _house!!
    }


private var _house: ImageVector? = null

// ── BookOpen (regular) ──
val BookOpen: ImageVector
    get() {
        if (_bookOpen != null) {
            return _bookOpen!!
        }
        _bookOpen = ImageVector.Builder(
            name = "Regular.BookOpen",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(128f, 88f)
                arcToRelative(32f, 32f, 0f, isMoreThanHalf = false, isPositiveArc = true, 32f, -32f)
                horizontalLineToRelative(72f)
                verticalLineTo(200f)
                horizontalLineTo(160f)
                arcToRelative(32f, 32f, 0f, isMoreThanHalf = false, isPositiveArc = false, -32f, 32f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(24f, 200f)
                horizontalLineTo(96f)
                arcToRelative(32f, 32f, 0f, isMoreThanHalf = false, isPositiveArc = true, 32f, 32f)
                verticalLineTo(88f)
                arcTo(32f, 32f, 0f, isMoreThanHalf = false, isPositiveArc = false, 96f, 56f)
                horizontalLineTo(24f)
                close()
            }
        }.build()

        return _bookOpen!!
    }


private var _bookOpen: ImageVector? = null

// ── UserCircle (regular) ──
val UserCircle: ImageVector
    get() {
        if (_userCircle != null) {
            return _userCircle!!
        }
        _userCircle = ImageVector.Builder(
            name = "Regular.UserCircle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(128f, 128f)
                moveToRelative(-96f, 0f)
                arcToRelative(96f, 96f, 0f, isMoreThanHalf = true, isPositiveArc = true, 192f, 0f)
                arcToRelative(96f, 96f, 0f, isMoreThanHalf = true, isPositiveArc = true, -192f, 0f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(128f, 120f)
                moveToRelative(-40f, 0f)
                arcToRelative(40f, 40f, 0f, isMoreThanHalf = true, isPositiveArc = true, 80f, 0f)
                arcToRelative(40f, 40f, 0f, isMoreThanHalf = true, isPositiveArc = true, -80f, 0f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(63.8f, 199.37f)
                arcToRelative(72f, 72f, 0f, isMoreThanHalf = false, isPositiveArc = true, 128.4f, 0f)
            }
        }.build()

        return _userCircle!!
    }


private var _userCircle: ImageVector? = null

// ── Bell (regular) ──
val Bell: ImageVector
    get() {
        if (_bell != null) {
            return _bell!!
        }
        _bell = ImageVector.Builder(
            name = "Regular.Bell",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(96f, 192f)
                arcToRelative(32f, 32f, 0f, isMoreThanHalf = false, isPositiveArc = false, 64f, 0f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(56f, 104f)
                arcToRelative(72f, 72f, 0f, isMoreThanHalf = false, isPositiveArc = true, 144f, 0f)
                curveToRelative(0f, 35.82f, 8.3f, 64.6f, 14.9f, 76f)
                arcTo(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 208f, 192f)
                horizontalLineTo(48f)
                arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, -6.88f, -12f)
                curveTo(47.71f, 168.6f, 56f, 139.81f, 56f, 104f)
                close()
            }
        }.build()

        return _bell!!
    }


private var _bell: ImageVector? = null

// ── Moon (regular) ──
val Moon: ImageVector
    get() {
        if (_moon != null) {
            return _moon!!
        }
        _moon = ImageVector.Builder(
            name = "Regular.Moon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(108.11f, 28.11f)
                arcTo(96.09f, 96.09f, 0f, isMoreThanHalf = false, isPositiveArc = false, 227.89f, 147.89f)
                arcTo(96f, 96f, 0f, isMoreThanHalf = true, isPositiveArc = true, 108.11f, 28.11f)
                close()
            }
        }.build()

        return _moon!!
    }


private var _moon: ImageVector? = null

// ── Globe (regular) ──
val Globe: ImageVector
    get() {
        if (_globe != null) {
            return _globe!!
        }
        _globe = ImageVector.Builder(
            name = "Regular.Globe",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(128f, 128f)
                moveToRelative(-96f, 0f)
                arcToRelative(96f, 96f, 0f, isMoreThanHalf = true, isPositiveArc = true, 192f, 0f)
                arcToRelative(96f, 96f, 0f, isMoreThanHalf = true, isPositiveArc = true, -192f, 0f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(168f, 128f)
                curveToRelative(0f, 64f, -40f, 96f, -40f, 96f)
                reflectiveCurveToRelative(-40f, -32f, -40f, -96f)
                reflectiveCurveToRelative(40f, -96f, 40f, -96f)
                reflectiveCurveTo(168f, 64f, 168f, 128f)
                close()
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(37.46f, 96f)
                lineTo(218.54f, 96f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(37.46f, 160f)
                lineTo(218.54f, 160f)
            }
        }.build()

        return _globe!!
    }


private var _globe: ImageVector? = null

// ── Star (regular) ──
val Star: ImageVector
    get() {
        if (_star != null) {
            return _star!!
        }
        _star = ImageVector.Builder(
            name = "Regular.Star",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(128f, 189.09f)
                lineToRelative(54.72f, 33.65f)
                arcToRelative(8.4f, 8.4f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12.52f, -9.17f)
                lineToRelative(-14.88f, -62.79f)
                lineToRelative(48.7f, -42f)
                arcTo(8.46f, 8.46f, 0f, isMoreThanHalf = false, isPositiveArc = false, 224.27f, 94f)
                lineTo(160.36f, 88.8f)
                lineTo(135.74f, 29.2f)
                arcToRelative(8.36f, 8.36f, 0f, isMoreThanHalf = false, isPositiveArc = false, -15.48f, 0f)
                lineTo(95.64f, 88.8f)
                lineTo(31.73f, 94f)
                arcToRelative(8.46f, 8.46f, 0f, isMoreThanHalf = false, isPositiveArc = false, -4.79f, 14.83f)
                lineToRelative(48.7f, 42f)
                lineTo(60.76f, 213.57f)
                arcToRelative(8.4f, 8.4f, 0f, isMoreThanHalf = false, isPositiveArc = false, 12.52f, 9.17f)
                close()
            }
        }.build()

        return _star!!
    }


private var _star: ImageVector? = null

// ── Info (regular) ──
val Info: ImageVector
    get() {
        if (_info != null) {
            return _info!!
        }
        _info = ImageVector.Builder(
            name = "Regular.Info",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(128f, 128f)
                moveToRelative(-96f, 0f)
                arcToRelative(96f, 96f, 0f, isMoreThanHalf = true, isPositiveArc = true, 192f, 0f)
                arcToRelative(96f, 96f, 0f, isMoreThanHalf = true, isPositiveArc = true, -192f, 0f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(120f, 120f)
                arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8f, 8f)
                verticalLineToRelative(40f)
                arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = false, 8f, 8f)
            }
            path(fill = SolidColor(Color.Black)) {
                moveTo(124f, 84f)
                moveToRelative(-12f, 0f)
                arcToRelative(12f, 12f, 0f, isMoreThanHalf = true, isPositiveArc = true, 24f, 0f)
                arcToRelative(12f, 12f, 0f, isMoreThanHalf = true, isPositiveArc = true, -24f, 0f)
            }
        }.build()

        return _info!!
    }


private var _info: ImageVector? = null

// ── SignOut (regular) ──
val SignOut: ImageVector
    get() {
        if (_signOut != null) {
            return _signOut!!
        }
        _signOut = ImageVector.Builder(
            name = "Regular.SignOut",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(112f, 40f)
                lineToRelative(-64f, 0f)
                lineToRelative(0f, 176f)
                lineToRelative(64f, 0f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(112f, 128f)
                lineTo(224f, 128f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(184f, 88f)
                lineToRelative(40f, 40f)
                lineToRelative(-40f, 40f)
            }
        }.build()

        return _signOut!!
    }


private var _signOut: ImageVector? = null

// ── CaretRight (regular) ──
val CaretRight: ImageVector
    get() {
        if (_caretRight != null) {
            return _caretRight!!
        }
        _caretRight = ImageVector.Builder(
            name = "Regular.CaretRight",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(96f, 48f)
                lineToRelative(80f, 80f)
                lineToRelative(-80f, 80f)
            }
        }.build()

        return _caretRight!!
    }


private var _caretRight: ImageVector? = null

// ── HouseFill (filled) ──
val HouseFill: ImageVector
    get() {
        if (_houseFill != null) {
            return _houseFill!!
        }
        _houseFill = ImageVector.Builder(
            name = "Filled.HouseFill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(224f, 120f)
                verticalLineToRelative(96f)
                arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, -8f, 8f)
                horizontalLineTo(160f)
                arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, -8f, -8f)
                verticalLineTo(164f)
                arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = false, -4f, -4f)
                horizontalLineTo(108f)
                arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = false, -4f, 4f)
                verticalLineToRelative(52f)
                arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, -8f, 8f)
                horizontalLineTo(40f)
                arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, -8f, -8f)
                verticalLineTo(120f)
                arcToRelative(16f, 16f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.69f, -11.31f)
                lineToRelative(80f, -80f)
                arcToRelative(16f, 16f, 0f, isMoreThanHalf = false, isPositiveArc = true, 22.62f, 0f)
                lineToRelative(80f, 80f)
                arcTo(16f, 16f, 0f, isMoreThanHalf = false, isPositiveArc = true, 224f, 120f)
                close()
            }
        }.build()

        return _houseFill!!
    }


private var _houseFill: ImageVector? = null

// ── BookOpenFill (filled) ──
val BookOpenFill: ImageVector
    get() {
        if (_bookOpenFill != null) {
            return _bookOpenFill!!
        }
        _bookOpenFill = ImageVector.Builder(
            name = "Filled.BookOpenFill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(240f, 56f)
                verticalLineTo(200f)
                arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, -8f, 8f)
                horizontalLineTo(160f)
                arcToRelative(24f, 24f, 0f, isMoreThanHalf = false, isPositiveArc = false, -24f, 23.94f)
                arcToRelative(7.9f, 7.9f, 0f, isMoreThanHalf = false, isPositiveArc = true, -5.12f, 7.55f)
                arcTo(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 120f, 232f)
                arcToRelative(24f, 24f, 0f, isMoreThanHalf = false, isPositiveArc = false, -24f, -24f)
                horizontalLineTo(24f)
                arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, -8f, -8f)
                verticalLineTo(56f)
                arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8f, -8f)
                horizontalLineTo(88f)
                arcToRelative(32f, 32f, 0f, isMoreThanHalf = false, isPositiveArc = true, 32f, 32f)
                verticalLineToRelative(87.73f)
                arcToRelative(8.17f, 8.17f, 0f, isMoreThanHalf = false, isPositiveArc = false, 7.47f, 8.25f)
                arcToRelative(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = false, 8.53f, -8f)
                verticalLineTo(80f)
                arcToRelative(32f, 32f, 0f, isMoreThanHalf = false, isPositiveArc = true, 32f, -32f)
                horizontalLineToRelative(64f)
                arcTo(8f, 8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 240f, 56f)
                close()
            }
        }.build()

        return _bookOpenFill!!
    }


private var _bookOpenFill: ImageVector? = null

// ── UserCircleFill (filled) ──
val UserCircleFill: ImageVector
    get() {
        if (_userCircleFill != null) {
            return _userCircleFill!!
        }
        _userCircleFill = ImageVector.Builder(
            name = "Filled.UserCircleFill",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(172f, 120f)
                arcToRelative(44f, 44f, 0f, isMoreThanHalf = true, isPositiveArc = true, -44f, -44f)
                arcTo(44.05f, 44.05f, 0f, isMoreThanHalf = false, isPositiveArc = true, 172f, 120f)
                close()
                moveTo(232f, 128f)
                arcTo(104f, 104f, 0f, isMoreThanHalf = true, isPositiveArc = true, 128f, 24f)
                arcTo(104.11f, 104.11f, 0f, isMoreThanHalf = false, isPositiveArc = true, 232f, 128f)
                close()
                moveTo(216f, 128f)
                arcToRelative(88.09f, 88.09f, 0f, isMoreThanHalf = false, isPositiveArc = false, -91.47f, -87.93f)
                curveTo(77.43f, 41.89f, 39.87f, 81.12f, 40f, 128.25f)
                arcToRelative(87.65f, 87.65f, 0f, isMoreThanHalf = false, isPositiveArc = false, 22.24f, 58.16f)
                arcTo(79.71f, 79.71f, 0f, isMoreThanHalf = false, isPositiveArc = true, 84f, 165.1f)
                arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.83f, 0.32f)
                arcToRelative(59.83f, 59.83f, 0f, isMoreThanHalf = false, isPositiveArc = false, 78.28f, 0f)
                arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.83f, -0.32f)
                arcToRelative(79.71f, 79.71f, 0f, isMoreThanHalf = false, isPositiveArc = true, 21.79f, 21.31f)
                arcTo(87.62f, 87.62f, 0f, isMoreThanHalf = false, isPositiveArc = false, 216f, 128f)
                close()
            }
        }.build()

        return _userCircleFill!!
    }


private var _userCircleFill: ImageVector? = null

// ── MagnifyingGlass (regular) ──
val MagnifyingGlass: ImageVector
    get() {
        if (_magnifyingGlass != null) {
            return _magnifyingGlass!!
        }
        _magnifyingGlass = ImageVector.Builder(
            name = "Regular.MagnifyingGlass",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(112f, 112f)
                moveToRelative(-80f, 0f)
                arcToRelative(80f, 80f, 0f, isMoreThanHalf = true, isPositiveArc = true, 160f, 0f)
                arcToRelative(80f, 80f, 0f, isMoreThanHalf = true, isPositiveArc = true, -160f, 0f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(168.57f, 168.57f)
                lineTo(224f, 224f)
            }
        }.build()

        return _magnifyingGlass!!
    }


private var _magnifyingGlass: ImageVector? = null

// ── X (regular) ──
val X: ImageVector
    get() {
        if (_x != null) {
            return _x!!
        }
        _x = ImageVector.Builder(
            name = "Regular.X",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(200f, 56f)
                lineTo(56f, 200f)
            }
            path(
                fill = SolidColor(Color.Black),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(200f, 200f)
                lineTo(56f, 56f)
            }
        }.build()

        return _x!!
    }


private var _x: ImageVector? = null

// ── WifiSlash (regular) ──
val WifiSlash: ImageVector
    get() {
        if (_wifiSlash != null) {
            return _wifiSlash!!
        }
        _wifiSlash = ImageVector.Builder(
            name = "Regular.WifiSlash",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(80f, 184f)
                arcToRelative(16f, 16f, 0f, isMoreThanHalf = true, isPositiveArc = true, 16f, -16f)
                arcToRelative(16f, 16f, 0f, isMoreThanHalf = false, isPositiveArc = true, -16f, 16f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(128f, 128f)
                curveToRelative(16f, 0f, 32f, 4.8f, 48f, 14.4f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(128f, 88f)
                curveToRelative(32f, 0f, 64f, 9.6f, 96f, 28.8f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(32f, 116.8f)
                curveToRelative(32f, -19.2f, 64f, -28.8f, 96f, -28.8f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(216f, 40f)
                lineTo(40f, 216f)
            }
        }.build()

        return _wifiSlash!!
    }


private var _wifiSlash: ImageVector? = null

// ── Trash (regular) ──
val Trash: ImageVector
    get() {
        if (_trash != null) {
            return _trash!!
        }
        _trash = ImageVector.Builder(
            name = "Regular.Trash",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 256f,
            viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(216f, 56f)
                lineTo(40f, 56f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(216f, 56f)
                lineTo(40f, 56f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(72f, 56f)
                verticalLineToRelative(-8f)
                arcToRelative(16f, 16f, 0f, isMoreThanHalf = false, isPositiveArc = true, 16f, -16f)
                horizontalLineToRelative(80f)
                arcToRelative(16f, 16f, 0f, isMoreThanHalf = false, isPositiveArc = true, 16f, 16f)
                verticalLineToRelative(8f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(176f, 56f)
                verticalLineToRelative(152f)
                arcToRelative(16f, 16f, 0f, isMoreThanHalf = false, isPositiveArc = true, -16f, 16f)
                horizontalLineTo(96f)
                arcToRelative(16f, 16f, 0f, isMoreThanHalf = false, isPositiveArc = true, -16f, -16f)
                verticalLineTo(56f)
            }
        }.build()

        return _trash!!
    }

private var _trash: ImageVector? = null


// ── ArrowLeft (regular) ──
val ArrowLeft: ImageVector
    get() {
        if (_arrowLeft != null) return _arrowLeft!!
        _arrowLeft = ImageVector.Builder(
            name = "Regular.ArrowLeft",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 256f, viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(216f, 128f)
                horizontalLineTo(40f)
                moveTo(104f, 56f)
                lineTo(40f, 128f)
                lineTo(104f, 200f)
            }
        }.build()
        return _arrowLeft!!
    }
private var _arrowLeft: ImageVector? = null


// ── Play (regular) ──
val Play: ImageVector
    get() {
        if (_play != null) return _play!!
        _play = ImageVector.Builder(
            name = "Regular.Play",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 256f, viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(224f, 128f)
                arcTo(16f, 16f, 0f, isMoreThanHalf = false, isPositiveArc = false, 192f, 128f)
                lineTo(80f, 56f)
                arcTo(16f, 16f, 0f, isMoreThanHalf = false, isPositiveArc = true, 64f, 69.3f)
                verticalLineTo(186.7f)
                arcTo(16f, 16f, 0f, isMoreThanHalf = false, isPositiveArc = true, 80f, 200f)
                close()
            }
        }.build()
        return _play!!
    }
private var _play: ImageVector? = null


// ── Pause (regular) ──
val Pause: ImageVector
    get() {
        if (_pause != null) return _pause!!
        _pause = ImageVector.Builder(
            name = "Regular.Pause",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 256f, viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(160f, 56f)
                verticalLineTo(200f)
                moveTo(96f, 56f)
                verticalLineTo(200f)
            }
        }.build()
        return _pause!!
    }
private var _pause: ImageVector? = null


// ── DotsThree (regular) ──
val DotsThree: ImageVector
    get() {
        if (_dotsThree != null) return _dotsThree!!
        _dotsThree = ImageVector.Builder(
            name = "Regular.DotsThree",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 256f, viewportHeight = 256f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Center dot
                moveTo(140f, 128f)
                arcTo(28f, 28f, 0f, isMoreThanHalf = true, isPositiveArc = true, 84f, 128f)
                arcTo(28f, 28f, 0f, isMoreThanHalf = true, isPositiveArc = true, 140f, 128f)
                // Right dot
                moveTo(196f, 128f)
                arcTo(28f, 28f, 0f, isMoreThanHalf = true, isPositiveArc = true, 140f, 128f)
                arcTo(28f, 28f, 0f, isMoreThanHalf = true, isPositiveArc = true, 196f, 128f)
                // Left dot
                moveTo(84f, 128f)
                arcTo(28f, 28f, 0f, isMoreThanHalf = true, isPositiveArc = true, 28f, 128f)
                arcTo(28f, 28f, 0f, isMoreThanHalf = true, isPositiveArc = true, 84f, 128f)
            }
        }.build()
        return _dotsThree!!
    }
private var _dotsThree: ImageVector? = null


// ── DotsThreeVertical (regular) ──
val DotsThreeVertical: ImageVector
    get() {
        if (_dotsThreeVertical != null) return _dotsThreeVertical!!
        _dotsThreeVertical = ImageVector.Builder(
            name = "Regular.DotsThreeVertical",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 256f, viewportHeight = 256f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                // Top dot
                moveTo(128f, 84f)
                arcTo(28f, 28f, 0f, isMoreThanHalf = true, isPositiveArc = true, 72f, 84f)
                arcTo(28f, 28f, 0f, isMoreThanHalf = true, isPositiveArc = true, 128f, 84f)
                // Center dot
                moveTo(128f, 140f)
                arcTo(28f, 28f, 0f, isMoreThanHalf = true, isPositiveArc = true, 72f, 140f)
                arcTo(28f, 28f, 0f, isMoreThanHalf = true, isPositiveArc = true, 128f, 140f)
                // Bottom dot
                moveTo(128f, 196f)
                arcTo(28f, 28f, 0f, isMoreThanHalf = true, isPositiveArc = true, 72f, 196f)
                arcTo(28f, 28f, 0f, isMoreThanHalf = true, isPositiveArc = true, 128f, 196f)
            }
        }.build()
        return _dotsThreeVertical!!
    }
private var _dotsThreeVertical: ImageVector? = null


// ── Download (regular) ──
val Download: ImageVector
    get() {
        if (_download != null) return _download!!
        _download = ImageVector.Builder(
            name = "Regular.Download",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 256f, viewportHeight = 256f
        ).apply {
            // Arrow down
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(128f, 32f)
                verticalLineTo(168f)
                moveTo(88f, 128f)
                lineTo(128f, 168f)
                lineTo(168f, 128f)
            }
            // Tray
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(216f, 136f)
                verticalLineTo(192f)
                arcTo(16f, 16f, 0f, isMoreThanHalf = false, isPositiveArc = true, 200f, 208f)
                horizontalLineTo(56f)
                arcTo(16f, 16f, 0f, isMoreThanHalf = false, isPositiveArc = true, 40f, 192f)
                verticalLineTo(136f)
            }
        }.build()
        return _download!!
    }
private var _download: ImageVector? = null


// ── Check (regular) ──
val Check: ImageVector
    get() {
        if (_check != null) return _check!!
        _check = ImageVector.Builder(
            name = "Regular.Check",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 256f, viewportHeight = 256f
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 16f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(216f, 72f)
                lineTo(96f, 184f)
                lineTo(40f, 128f)
            }
        }.build()
        return _check!!
    }
private var _check: ImageVector? = null
