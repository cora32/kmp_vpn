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

val TablerRouteAltLeft: ImageVector
    get() {
        if (_TablerRouteAltLeft != null) return _TablerRouteAltLeft!!

        _TablerRouteAltLeft = ImageVector.Builder(
            name = "route-alt-left",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(8f, 3f)
                horizontalLineToRelative(-5f)
                verticalLineToRelative(5f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(16f, 3f)
                horizontalLineToRelative(5f)
                verticalLineToRelative(5f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 3f)
                lineToRelative(7.536f, 7.536f)
                arcToRelative(5f, 5f, 0f, false, true, 1.464f, 3.534f)
                verticalLineToRelative(6.93f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(18f, 6.01f)
                verticalLineToRelative(-0.01f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(16f, 8.02f)
                verticalLineToRelative(-0.01f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(14f, 10f)
                verticalLineToRelative(0.01f)
            }
        }.build()

        return _TablerRouteAltLeft!!
    }

private var _TablerRouteAltLeft: ImageVector? = null

val TablerRouter: ImageVector
    get() {
        if (_TablerRouter != null) return _TablerRouter!!

        _TablerRouter = ImageVector.Builder(
            name = "router",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(3f, 15f)
                arcToRelative(2f, 2f, 0f, false, true, 2f, -2f)
                horizontalLineToRelative(14f)
                arcToRelative(2f, 2f, 0f, false, true, 2f, 2f)
                verticalLineToRelative(4f)
                arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
                horizontalLineToRelative(-14f)
                arcToRelative(2f, 2f, 0f, false, true, -2f, -2f)
                lineToRelative(0f, -4f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(17f, 17f)
                lineToRelative(0f, 0.01f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(13f, 17f)
                lineToRelative(0f, 0.01f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(15f, 13f)
                lineToRelative(0f, -2f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(11.75f, 8.75f)
                arcToRelative(4f, 4f, 0f, false, true, 6.5f, 0f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(8.5f, 6.5f)
                arcToRelative(8f, 8f, 0f, false, true, 13f, 0f)
            }
        }.build()

        return _TablerRouter!!
    }

private var _TablerRouter: ImageVector? = null


val LucideListFilterPlus: ImageVector
    get() {
        if (_LucideListFilterPlus != null) return _LucideListFilterPlus!!

        _LucideListFilterPlus = ImageVector.Builder(
            name = "list-filter-plus",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(12f, 5f)
                horizontalLineTo(2f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(6f, 12f)
                horizontalLineToRelative(12f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(9f, 19f)
                horizontalLineToRelative(6f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(16f, 5f)
                horizontalLineToRelative(6f)
            }
            path(
                fill = SolidColor(Color.Transparent),
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round
            ) {
                moveTo(19f, 8f)
                verticalLineTo(2f)
            }
        }.build()

        return _LucideListFilterPlus!!
    }

private var _LucideListFilterPlus: ImageVector? = null