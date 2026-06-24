package io.iskopasi.kmpvpntest.api

import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.Uri
import coil3.asImage
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.request.Options
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ImageInfo
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.filechooser.FileSystemView

class WindowsAppIconFetcher(
    private val file: File
) : Fetcher {
    override suspend fun fetch(): FetchResult? {
        "==> Fetching icon for: ${file.absolutePath}".e

        return try {
            val icon = FileSystemView.getFileSystemView().getSystemIcon(file) ?: run {
                "==> No system icon found for: ${file.name}".e
                return null
            }

            val bufferedImage = BufferedImage(
                icon.iconWidth,
                icon.iconHeight,
                BufferedImage.TYPE_INT_ARGB
            )
            val g = bufferedImage.createGraphics()
            icon.paintIcon(null, g, 0, 0)
            g.dispose()

            val width = bufferedImage.width
            val height = bufferedImage.height
            val bytesPerPixel = 4
            val pixels = ByteArray(width * height * bytesPerPixel)

            var k = 0
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val argb = bufferedImage.getRGB(x, y)
                    val a = (argb shr 24) and 0xff
                    val r = (argb shr 16) and 0xff
                    val gVal = (argb shr 8) and 0xff
                    val b = (argb shr 0) and 0xff
                    pixels[k++] = b.toByte()
                    pixels[k++] = gVal.toByte()
                    pixels[k++] = r.toByte()
                    pixels[k++] = a.toByte()
                }
            }

            val bitmap = Bitmap()
            bitmap.allocPixels(ImageInfo.makeS32(width, height, ColorAlphaType.UNPREMUL))
            bitmap.installPixels(pixels)

            "==> Successfully created bitmap for: ${file.name}".e

            ImageFetchResult(
                image = bitmap.asImage(),
                isSampled = false,
                dataSource = DataSource.DISK
            )
        } catch (e: Exception) {
            "==> Error fetching icon for ${file.name}: ${e.message}".e
            e.printStackTrace()
            null
        }
    }

    class Factory : Fetcher.Factory<Any> {
        override fun create(
            data: Any,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher? {
            "==> Factory.create called with: $data (${data::class.java.name})".e

            val path = when (data) {
                is String -> data
                is Uri -> data.path
                is File -> data.absolutePath
                else -> null
            }

            if (path != null) {
                val file = File(path)
                val ext = file.extension.lowercase()
                "==> Checking path: $path (exists: ${file.exists()}, extension: $ext)".e

                if (file.exists() && (ext == "exe" || ext == "lnk")) {
                    "==> Creating fetcher for: $path".e
                    return WindowsAppIconFetcher(file)
                }
            }
            return null
        }
    }
}

fun initializeCoil() {
    SingletonImageLoader.setSafe { context ->
        ImageLoader.Builder(context)
            .components {
                add(WindowsAppIconFetcher.Factory())
            }
            .build()
    }
}
