package io.iskopasi.kmpvpntest.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val MaterialIconsMinimize: ImageVector
    get() {
        if (_MaterialIconsMinimize != null) return _MaterialIconsMinimize!!

        _MaterialIconsMinimize = ImageVector.Builder(
            name = "minimize",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Transparent)
            ) {
                moveTo(0f, 0f)
                horizontalLineToRelative(24f)
                verticalLineToRelative(24f)
                horizontalLineTo(0f)
                verticalLineTo(0f)
                close()
            }
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(6f, 19f)
                horizontalLineToRelative(12f)
                verticalLineToRelative(2f)
                horizontalLineTo(6f)
                verticalLineToRelative(-2f)
                close()
            }
        }.build()

        return _MaterialIconsMinimize!!
    }

private var _MaterialIconsMinimize: ImageVector? = null

val VscodeCodiconsClose: ImageVector
    get() {
        if (_VscodeCodiconsClose != null) return _VscodeCodiconsClose!!

        _VscodeCodiconsClose = ImageVector.Builder(
            name = "close",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(8.70701f, 8.00001f)
                lineTo(12.353f, 4.35401f)
                curveTo(12.548f, 4.15901f, 12.548f, 3.84201f, 12.353f, 3.64701f)
                curveTo(12.158f, 3.45201f, 11.841f, 3.45201f, 11.646f, 3.64701f)
                lineTo(8.00001f, 7.29301f)
                lineTo(4.35401f, 3.64701f)
                curveTo(4.15901f, 3.45201f, 3.84201f, 3.45201f, 3.64701f, 3.64701f)
                curveTo(3.45201f, 3.84201f, 3.45201f, 4.15901f, 3.64701f, 4.35401f)
                lineTo(7.29301f, 8.00001f)
                lineTo(3.64701f, 11.646f)
                curveTo(3.45201f, 11.841f, 3.45201f, 12.158f, 3.64701f, 12.353f)
                curveTo(3.74501f, 12.451f, 3.87301f, 12.499f, 4.00101f, 12.499f)
                curveTo(4.12901f, 12.499f, 4.25701f, 12.45f, 4.35501f, 12.353f)
                lineTo(8.00101f, 8.70701f)
                lineTo(11.647f, 12.353f)
                curveTo(11.745f, 12.451f, 11.873f, 12.499f, 12.001f, 12.499f)
                curveTo(12.129f, 12.499f, 12.257f, 12.45f, 12.355f, 12.353f)
                curveTo(12.55f, 12.158f, 12.55f, 11.841f, 12.355f, 11.646f)
                lineTo(8.70901f, 8.00001f)
                horizontalLineTo(8.70701f)
                close()
            }
        }.build()

        return _VscodeCodiconsClose!!
    }

private var _VscodeCodiconsClose: ImageVector? = null

val CloudSimple: ImageVector
    get() = ImageVector.Builder(
        name = "CloudSimple",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 100f,
        viewportHeight = 60f
    ).apply {
        path(
            fill = SolidColor(Color.White),
            stroke = null,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round
        ) {
            moveTo(25f, 45f)
            lineTo(75f, 45f)

            curveTo(85f, 45f, 90f, 40f, 90f, 32f)
            curveTo(90f, 25f, 84f, 20f, 77f, 20f)

            curveTo(74f, 10f, 65f, 5f, 55f, 10f)
            curveTo(50f, 5f, 40f, 5f, 35f, 12f)

            curveTo(25f, 12f, 18f, 18f, 18f, 28f)
            curveTo(10f, 30f, 10f, 45f, 25f, 45f)

            close()
        }
    }.build()

val CloudPuffy: ImageVector
    get() = ImageVector.Builder(
        name = "CloudPuffy",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 120f,
        viewportHeight = 70f
    ).apply {
        path(
            fill = SolidColor(Color.White)
        ) {
            moveTo(30f, 50f)
            lineTo(85f, 50f)

            curveTo(100f, 50f, 110f, 42f, 110f, 30f)
            curveTo(110f, 18f, 98f, 10f, 85f, 15f)

            curveTo(80f, 5f, 65f, 0f, 55f, 10f)
            curveTo(45f, 0f, 30f, 5f, 25f, 18f)

            curveTo(12f, 18f, 5f, 28f, 5f, 40f)
            curveTo(5f, 50f, 15f, 55f, 30f, 50f)

            close()
        }
    }.build()

val CloudMinimal: ImageVector
    get() = ImageVector.Builder(
        name = "CloudMinimal",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 100f,
        viewportHeight = 60f
    ).apply {
        path(
            fill = SolidColor(Color.White)
        ) {
            moveTo(30f, 40f)

            curveTo(30f, 30f, 38f, 25f, 45f, 28f)
            curveTo(48f, 18f, 60f, 15f, 65f, 25f)

            curveTo(75f, 22f, 85f, 28f, 85f, 38f)
            curveTo(85f, 48f, 75f, 52f, 65f, 50f)

            lineTo(40f, 50f)

            curveTo(33f, 50f, 30f, 46f, 30f, 40f)

            close()
        }
    }.build()


val CloudVolumetricWide: ImageVector
    get() = ImageVector.Builder(
        name = "CloudVolumetricWide",
        defaultWidth = 128.dp,
        defaultHeight = 72.dp,
        viewportWidth = 200f,
        viewportHeight = 120f
    ).apply {

        // ─────────────────────────────
        // BACK LAYER (shadow / depth)
        // ─────────────────────────────
        path(
            fill = SolidColor(Color(0x22000000)), // soft shadow
        ) {
            moveTo(35f, 75f)

            curveTo(20f, 75f, 10f, 60f, 18f, 48f)
            curveTo(5f, 40f, 10f, 20f, 28f, 22f)

            curveTo(40f, 5f, 65f, 10f, 72f, 22f)
            curveTo(85f, 10f, 110f, 12f, 118f, 28f)

            curveTo(145f, 25f, 160f, 40f, 155f, 58f)
            curveTo(175f, 65f, 170f, 90f, 145f, 92f)

            lineTo(45f, 92f)

            curveTo(25f, 92f, 18f, 85f, 35f, 75f)
            close()
        }

        // ─────────────────────────────
        // MAIN CLOUD BODY
        // ─────────────────────────────
        path(
            fill = SolidColor(Color.White),
        ) {
            moveTo(40f, 70f)

            curveTo(25f, 70f, 15f, 58f, 22f, 48f)
            curveTo(10f, 40f, 15f, 22f, 32f, 25f)

            curveTo(45f, 10f, 70f, 15f, 78f, 28f)
            curveTo(92f, 15f, 115f, 18f, 125f, 32f)

            curveTo(150f, 30f, 160f, 45f, 150f, 60f)
            curveTo(170f, 70f, 165f, 88f, 142f, 88f)

            lineTo(50f, 88f)

            curveTo(30f, 88f, 25f, 80f, 40f, 70f)
            close()
        }

        // ─────────────────────────────
        // INNER HIGHLIGHT LAYER (volume edge)
        // ─────────────────────────────
        path(
            fill = SolidColor(Color(0x33FFFFFF)),
        ) {
            moveTo(55f, 60f)

            curveTo(45f, 55f, 40f, 45f, 48f, 38f)
            curveTo(50f, 25f, 70f, 25f, 78f, 35f)

            curveTo(90f, 25f, 110f, 30f, 112f, 42f)
            curveTo(120f, 55f, 110f, 65f, 95f, 62f)

            curveTo(85f, 70f, 65f, 70f, 55f, 60f)
            close()
        }

    }.build()