package io.iskopasi.kmpvpntest.api

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.asImage
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.request.Options

class AppIconFetcher(
    private val info: ApplicationInfo,
    private val pm: PackageManager
) : Fetcher {
    override suspend fun fetch(): FetchResult {
        return ImageFetchResult(
            image = info.loadIcon(pm).asImage(),
            isSampled = false,
            dataSource = DataSource.DISK
        )
    }

    class Factory(private val context: Context) : Fetcher.Factory<ApplicationInfo> {
        override fun create(
            data: ApplicationInfo,
            options: Options,
            imageLoader: ImageLoader
        ): Fetcher {
            return AppIconFetcher(data, context.packageManager)
        }
    }
}

fun initializeCoil(context: Context) {
    SingletonImageLoader.setSafe {
        ImageLoader.Builder(context)
            .components {
                add(AppIconFetcher.Factory(context))
            }
            .build()
    }
}
